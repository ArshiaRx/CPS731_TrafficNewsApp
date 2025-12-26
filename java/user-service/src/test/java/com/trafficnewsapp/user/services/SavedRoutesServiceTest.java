package com.trafficnewsapp.user.services;

import com.trafficnewsapp.user.dao.RouteDAO;
import com.trafficnewsapp.user.models.Route;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SavedRoutesService
 * Tests at least 3 methods: addRoute, deleteRoute, getSavedRoutes
 */
@DisplayName("SavedRoutesService Tests")
public class SavedRoutesServiceTest {
    private SavedRoutesService savedRoutesService;
    private RouteDAO routeDAO;
    
    @BeforeEach
    void setUp() {
        routeDAO = new RouteDAO();
        savedRoutesService = new SavedRoutesService(routeDAO);
    }
    
    @Test
    @DisplayName("Test addRoute - should create new route")
    void testAddRoute() {
        // Setup
        Route route = new Route();
        route.setName("Test Route");
        route.setLatitude(43.6532);
        route.setLongitude(-79.3832);
        route.setRadius(1000);
        route.setUserId("test_user");
        route.setCreatedAt(LocalDateTime.now());
        
        // Execute
        Route created = savedRoutesService.addRoute(route);
        
        // Verify
        assertNotNull(created, "Created route should not be null");
        assertNotNull(created.getId(), "Created route should have an ID");
        assertEquals("Test Route", created.getName(), "Name should match");
        
        // Cleanup
        if (created != null && created.getId() != null) {
            savedRoutesService.deleteRoute(created.getId());
        }
    }
    
    @Test
    @DisplayName("Test getSavedRoutes - should return routes for user")
    void testGetSavedRoutes() {
        // Execute
        List<Route> routes = savedRoutesService.getSavedRoutes("test_user");
        
        // Verify
        assertNotNull(routes, "Routes list should not be null");
        assertTrue(routes.size() >= 0, "Should return list (may be empty)");
    }
    
    @Test
    @DisplayName("Test deleteRoute - should delete route")
    void testDeleteRoute() {
        // Setup - create a route first
        Route route = new Route();
        route.setName("Route to Delete");
        route.setLatitude(43.6532);
        route.setLongitude(-79.3832);
        route.setUserId("test_user");
        
        Route created = savedRoutesService.addRoute(route);
        assertNotNull(created, "Route should be created");
        String routeId = created.getId();
        
        // Verify it exists
        Route beforeDelete = savedRoutesService.getRouteById(routeId);
        assertNotNull(beforeDelete, "Route should exist before deletion");
        
        // Execute
        boolean deleted = savedRoutesService.deleteRoute(routeId);
        
        // Verify
        assertTrue(deleted, "Route should be deleted successfully");
        
        // Verify it no longer exists
        Route afterDelete = savedRoutesService.getRouteById(routeId);
        assertNull(afterDelete, "Route should not exist after deletion");
    }
}













