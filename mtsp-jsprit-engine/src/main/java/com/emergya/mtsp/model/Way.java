/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emergya.mtsp.model;

import com.graphhopper.util.Instruction;
import java.util.List;
import org.geojson.LineString;

/**
 *
 * @author amartinez
 */
public class Way {

    private String origin;
    private String target;
    private LineString geometry;
    private double time;
    private double distance;
    private double cost;

    public Way() {
    }

    public Way(String origin, String target, LineString geometry, double time, double distance, double cost) {
        this.origin = origin;
        this.target = target;
        this.geometry = geometry;
        this.time = time;
        this.distance = distance;
        this.cost = cost;

    }

    /**
     * @return the origin
     */
    public String getOrigin() {
        return origin;
    }

    /**
     * @param origin the origin to set
     */
    public void setOrigin(String origin) {
        this.origin = origin;
    }

    /**
     * @return the target
     */
    public String getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * @return the geometry
     */
    public LineString getGeometry() {
        return geometry;
    }

    /**
     * @param geometry the geometry to set
     */
    public void setGeometry(LineString geometry) {
        this.geometry = geometry;
    }

    /**
     * @return the time
     */
    public double getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(double time) {
        this.time = time;
    }

    /**
     * @return the distance
     */
    public double getDistance() {
        return distance;
    }

    /**
     * @param distance the distance to set
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     * @return the cost
     */
    public double getCost() {
        return cost;
    }

    /**
     * @param cost the cost to set
     */
    public void setCost(double cost) {
        this.cost = cost;
    }

}
