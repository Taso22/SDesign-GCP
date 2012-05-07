/**
 * 
 */
package com.dask.pent;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/**
 * @author pent
 *
 */
public class Geocoding {
	private HttpClient httpclient = new DefaultHttpClient();
	private HttpPost httppost;
    private HttpGet httpget;
    private HttpResponse httpres;
    
    public String geoGG = "";
    public String geoYG = "";
    public String geoCMG = "";
    
    public String[] geocodingGOOGLE(String query) throws IOException {
		query = query.trim().replace(" ", "+");
		
		String url = "http://maps.googleapis.com/maps/api/geocode/json?address="+query+"&sensor=false";
		
		httpget = new HttpGet(url);    	
    	httpres = httpclient.execute(httpget);
    	return parseGG(responseToString(httpres));	
	}
	
	public String[] geocodingYAHOO(String query) throws IOException {
		query = query.trim().replace(" ", "+");
		
		String url = "http://where.yahooapis.com/geocode?location="+query+"&flags=J&gflags=R&appid=Jg9Kfl6o";

		httpget = new HttpGet(url);
    	httpres = httpclient.execute(httpget);
    	return parseYG(responseToString(httpres));
	}
	
	private String getToken(String APIKEY, String userid, String deviceid) throws IOException {		
		httppost = new HttpPost("http://auth.cloudmade.com/token/"+APIKEY+"?userid="+userid+"&deviceid="+deviceid);
    	httpres = httpclient.execute(httppost);
    	return responseToString(httpres);
	}
	
	public String[] geocodingCLOUDMADE(String query) throws IOException {
		String APIkey = "bf762d57ab76419f8d55e5ead01230fd";
		String token = getToken(APIkey, "krpent", "1234");
		
		query = query.trim().replace(" ", "+");
		
		String url = "http://geocoding.cloudmade.com/"+APIkey+"/geocoding/v2/find.js?query="+query+"&token="+token;
		
		httpget = new HttpGet(url);
    	httpres = httpclient.execute(httpget);
    	return parseCMG(responseToString(httpres));
	}
    
	private String[] parseGG(String json) {		
		JsonParser parser = new JsonParser();
		JsonObject jobject = parser.parse(json).getAsJsonObject();
        JsonObject coor = jobject.get("results").getAsJsonArray().get(0).getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject();
        
		geoGG = jobject.get("results").getAsJsonArray().get(0).getAsJsonObject().get("formatted_address").getAsString();
        
        String pt[] = {coor.get("lat").getAsString(), coor.get("lng").getAsString()};
        return pt;
	}
	
	private String[] parseYG(String json){
		JsonParser parser = new JsonParser();
		JsonObject jobject = parser.parse(json).getAsJsonObject();
		JsonObject coor = jobject.get("ResultSet").getAsJsonObject().get("Results").getAsJsonArray().get(0).getAsJsonObject();
		
		String pt[] = {
				coor.get("latitude").getAsString(), 
				coor.get("longitude").getAsString(),
				coor.get("offsetlat").getAsString(), 
				coor.get("offsetlon").getAsString()};
		return pt;
	}
	
	private String[] parseCMG(String json){
		System.out.println(json);
		JsonParser parser = new JsonParser();
		JsonObject jobject = parser.parse(json).getAsJsonObject();
		JsonArray coor = jobject.get("features").getAsJsonArray().get(0).getAsJsonObject().get("centroid").getAsJsonObject().get("coordinates").getAsJsonArray();
		
		String pt[] = {coor.get(0).getAsString(), coor.get(1).getAsString()};
        return pt;
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
