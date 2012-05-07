/**
 * 
 */
package com.dask.pent;

import com.google.gson.JsonArray;

/**
 * @author Kevin R.
 *
 */
public class CMRouteInstruction {
	public String instruction;		// Text instruction, e.g. Turn left at Oxford Street
	public double length;			// Length of the segment in meters. I.e. how long there won't be new instruction.
	public int position;			// Index of the first point of the segment in route_geometry
	public int time;				// Estimated time required to travel the segment in seconds
	public String length_caption;	// Length of the segments in specified units e.g. 22m, 23.4 km, 14.4 miles
	public String earth_direction; // Earth direction code of the start of the segment (now only 8 directions are supported, N, NE, E, SE, S, SW, W, NW)
	public double azimuth;			// North-based azimuth
	public String turn_type;		// Optional. Code of the turn type, absent for the first segment. See full description below.
	public double turn_angle;		// Optional. Angle in degrees of the turn between two segments, 0 for go straight, 90 for turn right, 270 for turn left, 180 for U-turn

	CMRouteInstruction(JsonArray json) {
		if(json != null) {
			instruction = json.get(0).getAsString();
			length = json.get(1).getAsDouble();
			position = json.get(2).getAsInt();		
			time = json.get(3).getAsInt();		
			length_caption = json.get(4).getAsString();  
			earth_direction = json.get(5).getAsString(); 
			azimuth = json.get(6).getAsDouble();
			if(json.size() > 7) {
				turn_type = json.get(7).getAsString(); 
				turn_angle = json.get(8).getAsDouble();
			}
			else {
				turn_type = "";
				turn_angle = 0;
			}
		}
	}
	
	public String toString() {
		return instruction + " Length: " + length + " Position: " + position + " Time: "+ time + " Length Caption: "+ length_caption + ((turn_type == "")? "\n" : 
			(" - " + (time/60) + "min " + (time%60) + "sec Azimuth: "+azimuth+" " + turn_type + " " + turn_angle + " Degrees Clockwise\n"));
	}
}

