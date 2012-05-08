package com.dask.pent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dask.pent.DistanceNotifier.Listener;

public class FoundationActivity extends Activity {
	private CloudMade CMManager;
	private Geocoding GCManager;
	
	private Compass compass;
	public Handler handler;
	
	private Navigation navi;
	
	String[] Intent_start;
	String[] Intent_dest;
	CMJson cmjson;
	
	private int initCounter = 0;
	private long refreshS = 0;
	private long refreshE = 0;
	private long delay = 1000;
	
	private int naviS = 0;
	private int GPSBearing = 0;
	private int magneticBearing = 0;
	
	double mDistance;
	int[] mBearingM = new int[10];
	int[] mBearingG = new int[3];
	
	int mStepCount = 0;
	int mMagnetCount = 0;
	int mGPSCount = 0;
	
	SensorManager mSensorManager;
	Sensor mSensor;
	StepDetector mStepDetector;
	DistanceNotifier mDistanceNotifier;
	
	TextView tview;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tview = (TextView) this.findViewById(R.id.mainText);
        
        tview.setText("Starting...");
        
        handler = new Handler(){
        	@Override
        	public void handleMessage(Message msg) {
        		super.handleMessage(msg);
        		switch(msg.arg1){
        		case 0:
        			mBearingM[mMagnetCount] = compass.getMagDir();
        			mMagnetCount++;
        			if(mMagnetCount == 10) {
        				mMagnetCount = 0;
        				magneticBearing = 0;
        				for(int i=0; i<mBearingM.length; i++)
        					magneticBearing += mBearingM[i];
        				magneticBearing = magneticBearing/mBearingM.length;
        				Log.i("handler","Notified that sensor changed, MAG: "+magneticBearing);
        				
        			}
        			break;
        		case 1:
        			mBearingG[mGPSCount] = compass.getGPSBearing();
        			mGPSCount++;
        			if(mGPSCount == 10) {
        				mGPSCount = 0;
        				GPSBearing = 0;
        				for(int i=0; i<mBearingG.length; i++)
        					GPSBearing += mBearingG[i];
        				GPSBearing = GPSBearing/mBearingG.length;
        				Log.i("handler","Notified that location changed, GPS: "+GPSBearing);
        			}
        			break;
        		}
        	}

        };        
        
        GCManager = new Geocoding();
        try {
        	CMManager = new CloudMade(
        			"bf762d57ab76419f8d55e5ead01230fd", 
        			"krpent", 
        			"1234");
        } catch (ClientProtocolException e) { e.printStackTrace();
        } catch (IOException e) { e.printStackTrace(); }       
        
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        LocationManager LM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        compass = new Compass(LM, mSensorManager, handler);
        
        mStepDetector = new StepDetector();
        registerDetector();        
        
        mDistanceNotifier = new DistanceNotifier(mDistanceListener);
        mDistanceNotifier.setDistance(mDistance = 0); 
        mStepDetector.addStepListener(mDistanceNotifier);
            
//        debugger();
        LM.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,listen2GPS);
    }

    LocationListener listen2GPS = new LocationListener() {
		public void onStatusChanged(String provider, int status, Bundle extras) {}
		public void onProviderEnabled(String provider) {}
		public void onProviderDisabled(String provider) {}
		public void onLocationChanged(Location location) {
    		locChanged(location);
    	}
	};
    
	public void locChanged(Location location) {
		Log.d("listen2GPS", "Got Location w/" +
				" mBearing: " + magneticBearing +
				" mSteps: " + mStepCount);
		
		if(location != null) {
			if(naviS == 0) {
				if(initCounter < 10) {
					initCounter++;
					return;
				}
				try {
					Log.d("listen2PGS", "Initializing Navigation");
					naviS = 1;
					
					// Ideal 
					Intent_start = new String[]{
							"" + location.getLatitude(),
							"" + location.getLongitude()};
					
					Log.d("listen2PGS", "Dest: "+Arrays.toString(Intent_dest));
					
					cmjson = CMManager.getDirections(
							Intent_start, 
							Intent_dest, 
							"foot", "js", "Pent");
					Log.d("listen2PGS", Arrays.deepToString(
							cmjson.route_instructions.toArray()));
					navi = new Navigation(cmjson, new double[] {
							Double.parseDouble(Intent_dest[0]),
							Double.parseDouble(Intent_dest[1])
					});
				} catch (IOException e) { e.printStackTrace(); }
			}
			else {
				double[] pt = {
						location.getLatitude(), 
						location.getLongitude()};
				String[] res = navi.Navigate(
						pt, 
						GPSBearing, 
						mStepCount);
				
				Log.d("listen2PGS", Arrays.toString(res));
				
				refreshE = System.currentTimeMillis();
				if(refreshS == 0 || (refreshE-refreshS) > delay) {
					tview.setText(Arrays.toString(res));
					refreshS = System.currentTimeMillis();
					
					if(res[1].startsWith("0.1"))
						delay = 2000;
					else
						delay = 1000;
				}
				
				if(res[0].startsWith("0.") == true) {
					resetValues(null);
					Log.d("listen2PGS", "Reseting values!");
				}
			}
		}
		else
			Log.d("listen2PGS", "Location is null.");
	}
	
    public void resetValues(View v) {
    	mStepCount = 0;
    	mDistanceNotifier.setDistance(mDistance = 0);
    }
    
    /**
     * Forwards distance values from DistanceNotifier to the activity. 
     */
    private Listener mDistanceListener = new Listener() {
        public void valueChanged(double value) {
            mDistance = value;
//            tview.setText("Steps: "+(mStepCount++)+" \tDistance: "+mDistance+" meters.");
        }
    };
    
    private void registerDetector() {
        mSensor = mSensorManager.getDefaultSensor(
            Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(mStepDetector,
            mSensor,
            SensorManager.SENSOR_DELAY_FASTEST);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	compass.startMagneticCompass();
    	compass.startGPSCompass();
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	compass.stopMagneticCompass();
    	compass.stopGPSCompass();
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
	    super.onCreateOptionsMenu(menu);
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    
	    return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.exit_title) {
			finish();
			System.exit(0);
			
			return true;
		}
		return false;
    }
    
    public void debugger() {
//    	double[][] pts = {
//    			{40.821312,-73.94787, 285,   0},	// Front of ST
//    			{40.821312,-73.94787, 285,   0},	// Front of ST
//    			{40.821405,-73.948181, 285, 45},	// Middle of ST
//    			{40.821519,-73.948427, 285, 90},	// N.Corner of ST
//    			{40.821588,-73.948615, 285,  0}, 	// S.Corner of W140
//    			{40.821848,-73.949237, 285, 90},	// Middle of W140
//    			{40.822111,-73.949806, 210, 180},	// N.Corner of W140
//    			{40.822061,-73.950015, 210,  0},	// S.Corner of W139
//    			{40.821807,-73.950184, 210, 40},	// Middle of W139
//    			{40.821535,-73.950396, 210, 80},	// N.Corner of W139
//    			{40.821407,-73.950498, 210,  0},	// S.Corner of W138
//    			{40.821174,-73.95063, 210,  40},	// Middle of W138
//    			{40.820932,-73.950841, 210, 80},	// N.Corner of W138
//    			{40.820808,-73.950933, 210,  0},	// S.Corner of W137
//    			{40.820451,-73.951161, 210, 40},	// Middle of W137
//    			{40.819661,-73.951769, 210, 80},	// N.Corner of W137
//    			{40.819556,-73.951837, 210,  0},	// S.Corner of W136
//    			{40.819268,-73.952035, 210, 40},	// Middle of W136
//    			{40.819012,-73.95226, 210,  80},	// N.Corner of W136
//    			{40.818931,-73.95229, 210,   0},	// S.Corner of W135
//    			{40.818827,-73.952365, 210, 20}		// Subway
//    	};
    	
    	double[][] pts = {
    			{40.82140047064303, -73.94782474966814, 225, 0},	// F: ST 
    			{40.821274625142884, -73.94795617790987, 285, 0},	// S: ST
    			{40.82138220276265, -73.9481948945122, 285, 30},	// M: ST 
    			{40.82149992901453, -73.9484470221596, 285, 60}, 	// N: ST
    			{40.82154255398522, -73.9485543105202, 285, 0}, 	// S: Conv
    			{40.82183483876072, -73.94921949835589, 285, 60}, 	// M: Conv
    			{40.82208043816645, -73.94982031317522, 285, 120}, 	// N: Conv
    			{40.822157568370706, -73.94996783467104, 285, 0},	// S: Amst 
    			{40.82242143418061, -73.9506276580887, 285, 60},	// M: Amst
    			{40.82266906113967, -73.9512016508179, 285, 120},	// N: Amst 
    			{40.822758368996425, -73.9513652655678, 285, 0}, 	// S: Ham
    			{40.82300193526726, -73.95199022026827, 285, 60}, 
    			{40.82325158976658, -73.95255884857943, 285, 120}, 
    			{40.82333074831405, -73.95271978112032, 285, 0}, 
    			{40.823413966172325, -73.95291021796038, 15, 0}, 
    			{40.82366970820524, -73.95270905228426, 15, 30}
    	};
    	
    	Location Loc;
    	for(int i=0; i<pts.length; i++) {
    		Log.d("Debugger", "=============================================");
    		Loc = new Location("");
    		Loc.setLatitude(pts[i][0]);
    		Loc.setLongitude(pts[i][1]);
    		
    		magneticBearing = (int) pts[i][2];
    		mStepCount = (int) pts[i][3];
    		
    		locChanged(Loc);
    	}
    }
}