package com.trafficnewsapp.scheduler.dao;

import com.trafficnewsapp.scheduler.models.Submission;
import com.trafficnewsapp.scheduler.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Submission
 */
public class SubmissionDAO {
    
    public List<Submission> getPendingSubmissions() {
        List<Submission> submissions = new ArrayList<>();
        String sql = "SELECT * FROM submissions WHERE status = 'pending' ORDER BY timestamp ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                submissions.add(mapResultSetToSubmission(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting pending submissions: " + e.getMessage());
        }
        
        return submissions;
    }
    
    public boolean saveSubmission(Submission submission) {
        if (submission.getId() == null) {
            submission.setId(Submission.generateId());
        }
        
        Submission existing = getSubmissionById(submission.getId());
        
        if (existing != null) {
            return updateSubmission(submission);
        } else {
            return insertSubmission(submission);
        }
    }
    
    private boolean insertSubmission(Submission submission) {
        String sql = "INSERT INTO submissions (id, incident_data, timestamp, status) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, submission.getId());
            pstmt.setString(2, submission.getIncidentData());
            pstmt.setTimestamp(3, Timestamp.valueOf(submission.getTimestamp()));
            pstmt.setString(4, submission.getStatus());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting submission: " + e.getMessage());
            return false;
        }
    }
    
    private boolean updateSubmission(Submission submission) {
        String sql = "UPDATE submissions SET incident_data = ?, timestamp = ?, status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, submission.getIncidentData());
            pstmt.setTimestamp(2, Timestamp.valueOf(submission.getTimestamp()));
            pstmt.setString(3, submission.getStatus());
            pstmt.setString(4, submission.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating submission: " + e.getMessage());
            return false;
        }
    }
    
    public Submission getSubmissionById(String id) {
        String sql = "SELECT * FROM submissions WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToSubmission(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting submission by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    public boolean deleteSubmission(String id) {
        String sql = "DELETE FROM submissions WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting submission: " + e.getMessage());
            return false;
        }
    }
    
    private Submission mapResultSetToSubmission(ResultSet rs) throws SQLException {
        Submission submission = new Submission();
        submission.setId(rs.getString("id"));
        submission.setIncidentData(rs.getString("incident_data"));
        
        Timestamp ts = rs.getTimestamp("timestamp");
        if (ts != null) {
            submission.setTimestamp(ts.toLocalDateTime());
        }
        
        submission.setStatus(rs.getString("status"));
        return submission;
    }
}













