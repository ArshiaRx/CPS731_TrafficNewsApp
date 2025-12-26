package com.trafficnewsapp.scheduler.models;

import java.time.LocalDateTime;

/**
 * Submission Model
 * Represents a submitted incident report with status tracking
 */
public class Submission {
    private String id;
    private String incidentData; // JSON string of incident data
    private LocalDateTime timestamp;
    private String status; // 'pending', 'sent', 'failed'
    
    public Submission() {
        this.status = "pending";
        this.timestamp = LocalDateTime.now();
    }
    
    public Submission(String id, String incidentData, LocalDateTime timestamp, String status) {
        this.id = id;
        this.incidentData = incidentData;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
        this.status = status != null ? status : "pending";
    }
    
    public static String generateId() {
        return "sub_" + System.currentTimeMillis() + "_" + 
               Long.toString(System.nanoTime(), 36);
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getIncidentData() { return incidentData; }
    public void setIncidentData(String incidentData) { this.incidentData = incidentData; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}













