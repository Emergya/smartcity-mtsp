/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emergya.mtsp.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lroman
 */
public class MTSPRoutes implements Serializable {
    private final List<MTSPRoute> routes;

    public MTSPRoutes() {
        routes = new ArrayList<>();
    }
    
    

    /**
     * @return the routes
     */
    public List<MTSPRoute> getRoutes() {
        return routes;
    }
}
