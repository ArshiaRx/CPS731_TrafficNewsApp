package com.trafficnewsapp.user.services;

import com.trafficnewsapp.user.dao.RouteDAO;
import com.trafficnewsapp.user.models.Route;

import java.util.List;

/**
 * SavedRoutesService (C10)
 * Business Logic Layer - Route management
 */
public class SavedRoutesService {
    private RouteDAO routeDAO;
    
    public SavedRoutesService(RouteDAO routeDAO) {
        this.routeDAO = routeDAO;
    }
    
    /**
     * Get all saved routes for a user
     * @param userId User ID
     * @return List of routes
     */
    public List<Route> getSavedRoutes(String userId) {
        return routeDAO.getAllRoutes(userId);
    }
    
    /**
     * Get route by ID
     * @param routeId Route ID
     * @return Route or null
     */
    public Route getRouteById(String routeId) {
        return routeDAO.getRouteById(routeId);
    }
    
    /**
     * Add a new route
     * @param route Route to add
     * @return Created route or null if failed
     */
    public Route addRoute(Route route) {
        boolean success = routeDAO.saveRoute(route);
        return success ? route : null;
    }
    
    /**
     * Update an existing route
     * @param route Route with updates
     * @return Updated route or null if failed
     */
    public Route updateRoute(Route route) {
        boolean success = routeDAO.saveRoute(route);
        return success ? route : null;
    }
    
    /**
     * Delete a route
     * @param routeId Route ID
     * @return true if successful
     */
    public boolean deleteRoute(String routeId) {
        return routeDAO.deleteRoute(routeId);
    }
}













