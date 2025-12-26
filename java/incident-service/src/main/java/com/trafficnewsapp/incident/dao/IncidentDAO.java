package com.trafficnewsapp.incident.dao;

import com.trafficnewsapp.incident.models.Incident;
import com.trafficnewsapp.incident.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Incident
 * Handles all database operations for incidents
 */
public class IncidentDAO {
    
    /**
     * Get all incidents from database
     * @return List of all incidents
     */
    public List<Incident> getAllIncidents() {
        List<Incident> incidents = new ArrayList<>();
        String sql = "SELECT * FROM incidents ORDER BY timestamp DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                incidents.add(mapResultSetToIncident(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all incidents: " + e.getMessage());
        }
        
        return incidents;
    }
    
    /**
     * Get incident by ID
     * @param id Incident ID
     * @return Incident or null if not found
     */
    public Incident getIncidentById(String id) {
        String sql = "SELECT * FROM incidents WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToIncident(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting incident by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Save incident (insert or update)
     * @param incident Incident to save
     * @return true if successful
     */
    public boolean saveIncident(Incident incident) {
        if (incident.getId() == null) {
            incident.setId(Incident.generateId());
        }
        
        // Check if incident exists
        Incident existing = getIncidentById(incident.getId());
        
        if (existing != null) {
            return updateIncident(incident);
        } else {
            return insertIncident(incident);
        }
    }
    
    /**
     * Insert new incident
     */
    private boolean insertIncident(Incident incident) {
        String sql = "INSERT INTO incidents (id, type, severity, location, latitude, longitude, " +
                    "description, timestamp, reporter_id, status, submission_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, incident.getId());
            pstmt.setString(2, incident.getType());
            pstmt.setString(3, incident.getSeverity());
            pstmt.setString(4, incident.getLocation());
            pstmt.setObject(5, incident.getLatitude(), Types.DECIMAL);
            pstmt.setObject(6, incident.getLongitude(), Types.DECIMAL);
            pstmt.setString(7, incident.getDescription());
            pstmt.setTimestamp(8, Timestamp.valueOf(incident.getTimestamp()));
            pstmt.setString(9, incident.getReporterId());
            pstmt.setString(10, incident.getStatus());
            pstmt.setString(11, incident.getSubmissionId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting incident: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update existing incident
     */
    private boolean updateIncident(Incident incident) {
        String sql = "UPDATE incidents SET type = ?, severity = ?, location = ?, " +
                    "latitude = ?, longitude = ?, description = ?, timestamp = ?, " +
                    "reporter_id = ?, status = ?, submission_id = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, incident.getType());
            pstmt.setString(2, incident.getSeverity());
            pstmt.setString(3, incident.getLocation());
            pstmt.setObject(4, incident.getLatitude(), Types.DECIMAL);
            pstmt.setObject(5, incident.getLongitude(), Types.DECIMAL);
            pstmt.setString(6, incident.getDescription());
            pstmt.setTimestamp(7, Timestamp.valueOf(incident.getTimestamp()));
            pstmt.setString(8, incident.getReporterId());
            pstmt.setString(9, incident.getStatus());
            pstmt.setString(10, incident.getSubmissionId());
            pstmt.setString(11, incident.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating incident: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete incident by ID
     * @param id Incident ID
     * @return true if successful
     */
    public boolean deleteIncident(String id) {
        String sql = "DELETE FROM incidents WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting incident: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get incidents by status
     * @param status Status to filter by
     * @return List of incidents with the specified status
     */
    public List<Incident> getIncidentsByStatus(String status) {
        List<Incident> incidents = new ArrayList<>();
        String sql = "SELECT * FROM incidents WHERE status = ? ORDER BY timestamp DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                incidents.add(mapResultSetToIncident(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting incidents by status: " + e.getMessage());
        }
        
        return incidents;
    }
    
    /**
     * Map ResultSet to Incident object
     */
    private Incident mapResultSetToIncident(ResultSet rs) throws SQLException {
        Incident incident = new Incident();
        incident.setId(rs.getString("id"));
        incident.setType(rs.getString("type"));
        incident.setSeverity(rs.getString("severity"));
        incident.setLocation(rs.getString("location"));
        
        Double lat = rs.getObject("latitude", Double.class);
        Double lon = rs.getObject("longitude", Double.class);
        incident.setLatitude(lat);
        incident.setLongitude(lon);
        
        incident.setDescription(rs.getString("description"));
        
        Timestamp ts = rs.getTimestamp("timestamp");
        if (ts != null) {
            incident.setTimestamp(ts.toLocalDateTime());
        }
        
        incident.setReporterId(rs.getString("reporter_id"));
        incident.setStatus(rs.getString("status"));
        incident.setSubmissionId(rs.getString("submission_id"));
        
        return incident;
    }
}













