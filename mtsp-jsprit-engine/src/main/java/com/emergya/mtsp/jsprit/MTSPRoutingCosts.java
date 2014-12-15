package com.emergya.mtsp.jsprit;

import java.util.HashMap;
import java.util.Map;

import jsprit.core.problem.cost.VehicleRoutingTransportCosts;
import jsprit.core.problem.driver.Driver;
import jsprit.core.problem.vehicle.Vehicle;

import com.emergya.mtsp.util.MTSPUtil;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.util.shapes.GHPoint;

public class MTSPRoutingCosts implements VehicleRoutingTransportCosts {
	
	private GraphHopper hopper;
	private Map<String, Double> distances;
	private Map<String, Double> times;
	
	public MTSPRoutingCosts(GraphHopper hopper){
		super();
		this.hopper = hopper;
		this.distances = new HashMap<String, Double>();
		this.times = new HashMap<String, Double>();
	}

	@Override
	public double getTransportTime(String fromId, String toId, double departureTime, Driver driver, Vehicle vehicle) {
		Double time = 0.0;
		String fromId_toId = fromId + "-" + toId;
		
		if(times.containsKey(fromId_toId)){
			time = times.get(fromId_toId);
		}else{
			GHPoint startPoint = MTSPUtil.getCoordinatesFromString(fromId);
			GHPoint endPoint = MTSPUtil.getCoordinatesFromString(toId);
			GHRequest request = new GHRequest(startPoint, endPoint);
			GHResponse route = hopper.route(request);
			Long t = route.getMillis();
			time = Double.parseDouble(t.toString());
			times.put(fromId_toId, time);
		}
		
		return time;
	}

	@Override
	public double getBackwardTransportTime(String fromId, String toId, double arrivalTime, Driver driver, Vehicle vehicle) {
		return getTransportTime(toId, fromId, arrivalTime, driver, vehicle);
	}

	@Override
	public double getTransportCost(String fromId, String toId, double departureTime, Driver driver, Vehicle vehicle) {
		Double distance = 0.0;
		String fromId_toId = fromId + "-" + toId;
		
		if(distances.containsKey(fromId_toId)){
			distance = distances.get(fromId_toId);
		}else{
			GHPoint startPoint = MTSPUtil.getCoordinatesFromString(fromId);
			GHPoint endPoint = MTSPUtil.getCoordinatesFromString(toId);
			GHRequest request = new GHRequest(startPoint, endPoint);
			GHResponse route = hopper.route(request);
			distance = route.getDistance();
			distances.put(fromId_toId, distance);
		}
		
		return distance;
	}

	@Override
	public double getBackwardTransportCost(String fromId, String toId, double arrivalTime, Driver driver, Vehicle vehicle) {
		return getTransportCost(toId, fromId, arrivalTime, driver, vehicle);
	}

}
