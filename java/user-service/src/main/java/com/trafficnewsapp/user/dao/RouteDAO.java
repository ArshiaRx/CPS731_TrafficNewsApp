package com.trafficnewsapp.user.dao;

import com.trafficnewsapp.user.models.Route;
import com.trafficnewsapp.user.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Route
 */
public class RouteDAO {
    
    public List<Route> getAllRoutes(String userId) {
        List<Route> routes = new ArrayList<>();
        String sql = "SELECT * FROM routes WHERE user_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                routes.add(mapResultSetToRoute(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting routes: " + e.getMessage());
        }
        
        return routes;
    }
    
    public Route getRouteById(String id) {
        String sql = "SELECT * FROM routes WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToRoute(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting route by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    public boolean saveRoute(Route route) {
        if (route.getId() == null) {
            route.setId(Route.generateId());
        }
        
        Route existing = getRouteById(route.getId());
        
        if (existing != null) {
            return updateRoute(route);
        } else {
            return insertRoute(route);
        }
    }
    
    private boolean insertRoute(Route route) {
        String sql = "INSERT INTO routes (id, name, latitude, longitude, radius, user_id, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, route.getId());
            pstmt.setString(2, route.getName());
            pstmt.setObject(3, route.getLatitude(), Types.DECIMAL);
            pstmt.setObject(4, route.getLongitude(), Types.DECIMAL);
            pstmt.setInt(5, route.getRadius());
            pstmt.setString(6, route.getUserId());
            pstmt.setTimestamp(7, Timestamp.valueOf(route.getCreatedAt()));
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting route: " + e.getMessage());
            return false;
        }
    }
    
    private boolean updateRoute(Route route) {
        String sql = "UPDATE routes SET name = ?, latitude = ?, longitude = ?, " +
                    "radius = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, route.getName());
            pstmt.setObject(2, route.getLatitude(), Types.DECIMAL);
            pstmt.setObject(3, route.getLongitude(), Types.DECIMAL);
            pstmt.setInt(4, route.getRadius());
            pstmt.setString(5, route.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating route: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteRoute(String id) {
        String sql = "DELETE FROM routes WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting route: " + e.getMessage());
            return false;
        }
    }
    
    private Route mapResultSetToRoute(ResultSet rs) throws SQLException {
        Route route = new Route();
        route.setId(rs.getString("id"));
        route.setName(rs.getString("name"));
        route.setLatitude(rs.getObject("latitude", Double.class));
        route.setLongitude(rs.getObject("longitude", Double.class));
        route.setRadius(rs.getInt("radius"));
        route.setUserId(rs.getString("user_id"));
        
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            route.setCreatedAt(ts.toLocalDateTime());
        }
        
        return route;
    }
}













