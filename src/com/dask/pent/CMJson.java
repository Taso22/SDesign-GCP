package com.dask.pent;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CMJson {
	public double version;			// API Version. 
	public int status;				// Error codes identifier. Zero is OK.
	public String status_message;	// In case of any error, small text description of error code.
	
	public static class RSummary { 	
		public int total_distance;	// Route distance in meters
		public int total_time;		// Route time in seconds
		public String start_point;	// Route start point street name
		public String end_point;	// Route end point street name
		
		RSummary(int total_distanceC, int total_timeC, String start_pointC, String end_pointC) {
			total_distance = total_distanceC;
			total_time = total_timeC; 
			start_point = start_pointC; 
			end_point = end_pointC; 
		}
		
		public String toString() {
			String res = "";
			
			res = " Total distance: "+total_distance+" Total Time: "+total_time+" Starting Point: "+start_point+" End Point: "+end_point+"\n";
			
			return res;
		}
	}
	
	public RSummary route_summary = new RSummary(0, 0, "", ""); // 	Summary of the route	
	public ArrayList<double[]> route_geometry = new ArrayList<double[]>();	// List of geometry points. Each point is represented by [lat,lon] pair.
	public ArrayList<CMRouteInstruction> route_instructions = new ArrayList<CMRouteInstruction>();
	
	CMJson(String json) {
		if(json != "") {
			JsonParser parser = new JsonParser();
	        JsonObject jobject = parser.parse(json).getAsJsonObject();
	        
	        version = jobject.get("version").getAsDouble();
	        status = jobject.get("status").getAsInt();
	        if(status == 0)
	        	status_message = "OK";
	        else
	        	status_message = jobject.get("status_message").getAsString();
	        
	        //System.out.println(jobject.);
	        JsonObject rsobj = jobject.get("route_summary").getAsJsonObject();
	        route_summary = new RSummary(rsobj.get("total_distance").getAsInt(), rsobj.get("total_time").getAsInt(),
	        		rsobj.get("start_point").getAsString(), rsobj.get("end_point").getAsString());
	        
	        JsonArray rgarray = jobject.get("route_geometry").getAsJsonArray();
	        JsonArray latlon;
	        double coor[];
	        for(int i=0; i<rgarray.size(); i++) {
	        	latlon = rgarray.get(i).getAsJsonArray();
	        	coor = new double[]{ latlon.get(0).getAsDouble(), latlon.get(1).getAsDouble()};
	        	route_geometry.add(coor);
	        }
	        
	        JsonArray routeIns = jobject.get("route_instructions").getAsJsonArray();
	        for(int i=0; i<routeIns.size(); i++)
	        	route_instructions.add(new CMRouteInstruction(routeIns.get(i).getAsJsonArray()));
		}
	}
	
	public String toString() {
		String res = "";
		
		res = route_summary.toString()+Arrays.deepToString(route_geometry.toArray())+"\n"+Arrays.deepToString(route_instructions.toArray());
		
		return res;
	}
}

