package com.emergya.mtsp.webapp.dto;

import com.emergya.mtsp.model.Stop;
import com.emergya.mtsp.model.Vehicle;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author lroman
 */
public class MTSPRequest implements Serializable {

    private List<Vehicle> vehicles;
    
    private List<Stop> stops;

    /**
     * @return the stops
     */
    public List<Stop> getStops() {
        return stops;
    }

    /**
     * @param stops the stops to set
     */
    public void setStops(List<Stop> stops) {
        this.stops = stops;
    }

    /**
     * @return the vehicles
     */
    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    /**
     * @param vehicles the vehicles to set
     */
    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }
}
