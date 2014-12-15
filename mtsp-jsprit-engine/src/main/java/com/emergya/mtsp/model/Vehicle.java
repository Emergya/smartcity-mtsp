package com.emergya.mtsp.model;

/**
 *
 * @author lroman
 */
public class Vehicle {
    private String id;
    private String name;
    private Stop origin;
    private Stop target;
    private double costPerDistance;
    private double costPerTime;
    
     public Vehicle() {
    }

    public Vehicle(String id, String name,Stop origin, Stop target, double costPerDistance, double costPerTime) {
        this.id = id;
        this.name = name;
        this.origin = origin;
        this.target = target;
        this.costPerDistance = costPerDistance;
        this.costPerTime = costPerTime;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    } 
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * @return the origin
     */
    public Stop getOrigin() {
        return origin;
    }

    /**
     * @param origin the origin to set
     */
    public void setOrigin(Stop origin) {
        this.origin = origin;
    }
    
    /**
     * @return the target
     */
    public Stop getTarget() {
        return target;
    }

    /**
     * @param origin the origin to set
     */
    public void setTarget(Stop target) {
        this.target = target;
    }

    /**
     * @return the costPerDistance
     */
    public double getCostPerDistance() {
        return costPerDistance;
    }

    /**
     * @param costPerDistance the costPerDistance to set
     */
    public void setCostPerDistance(double costPerDistance) {
        this.costPerDistance = costPerDistance;
    }
    
     /**
     * @return the getCostPerTime
     */
    public double getCostPerTime() {
        return costPerTime;
    }

    /**
     * @param costPerTime the costPerTime to set
     */
    public void getCostPerTime(double costPerTime) {
        this.costPerTime = costPerTime;
    }
}
