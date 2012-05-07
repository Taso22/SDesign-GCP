/**
 * 
 */
package com.dask.pent;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;

/**
 * @author Kevin R.
 *
 */
public class CloudMade {
	private HttpClient httpclient = new DefaultHttpClient();
    private HttpPost httppost;// = new HttpPost("http://auth.cloudmade.com/token/bf762d57ab76419f8d55e5ead01230fd?userid=krpent&deviceid=1234");
    private HttpGet httpget;
    private HttpResponse httpres;
    
    private String APIkey;
    private String token;
    
    /**
     * 
     * @param APIKEY
     * @param userid
     * @param deviceid
     * @throws ClientProtocolException
     * @throws IOException
     */
    public CloudMade(String APIKEY, String userid, String deviceid) throws ClientProtocolException, IOException {
    	//Generate the token:
    	httppost = new HttpPost("http://auth.cloudmade.com/token/"+APIKEY+"?userid="+userid+"&deviceid="+deviceid);
    	httpres = httpclient.execute(httppost);
    	
    	APIkey = APIKEY;
    	token = responseToString(httpres);  	
    }   
    
    /**
     * 
     * @param start
     * @param end
     * @param rtype
     * @param format
     * @param tID
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public CMJson getDirections(String[] start, String[] end, String rtype, 
    		String format, String tID) 
    				throws ClientProtocolException, IOException {
    	String url = "http://navigation.cloudmade.com/"+APIkey+"/api/latest/" +
    				 start[0]+","+start[1]+","+end[0]+","+end[1]+"/"+rtype+"." +
    				 format+"?tId="+tID+"&token="+token;
    	httpget = new HttpGet(url);
    	
    	httpres = httpclient.execute(httpget);
    	String directions = responseToString(httpres);
    	
    	return new CMJson(directions);
    }
        
    private String responseToString(HttpResponse res) 
    		throws IllegalStateException, IOException {
    	InputStream is = res.getEntity().getContent();
        BufferedInputStream bis = new BufferedInputStream(is);
        ByteArrayBuffer baf = new ByteArrayBuffer(20);

         int current = 0;  
         while((current = bis.read()) != -1) 
                baf.append((byte)current);
           
        // Convert the Bytes read to a String. 
        String text = new String(baf.toByteArray());
    	return text;
    }
    
}
