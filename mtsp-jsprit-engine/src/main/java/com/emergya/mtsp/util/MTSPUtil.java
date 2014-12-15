package com.emergya.mtsp.util;

import java.util.HashMap;
import java.util.Map;

import com.graphhopper.util.shapes.GHPoint;

public class MTSPUtil {

	public static GHPoint getCoordinatesFromString(String coordinate){
		// [x=-22.2][y=33.3]
		Double[] d = new Double[2];
		String[] splitted = null;
		
		while(coordinate.indexOf("[") > -1){
			coordinate = coordinate.replace("[", "");
		}
		
		while(coordinate.indexOf("]") > -1){
			if(coordinate.indexOf("]") < coordinate.length()-1){
				coordinate = coordinate.replace("]", ",");
			}
		}
		
		if(coordinate.indexOf(",") > -1){
			splitted = coordinate.split(",");
		}
		Map<String, String> coord_kv = new HashMap<String, String>();
		for(int i=0; i<splitted.length; i++){
			if(splitted[i].indexOf("=") > -1){
				coord_kv.put(splitted[i].split("=")[0], splitted[i].split("=")[1]);
			}
		}
		
		d[0] = Double.parseDouble(coord_kv.get("x"));
		d[1] = Double.parseDouble(coord_kv.get("y"));
		
		return new GHPoint(d[1], d[0]);
	}
}
