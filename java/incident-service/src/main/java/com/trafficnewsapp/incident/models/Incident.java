package com.trafficnewsapp.incident.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Incident Model
 * Represents a traffic incident with all required properties
 */
public class Incident {
    private String id;
    private String type; // 'accident', 'construction', 'closure', 'hazard'
    private String severity; // 'low', 'medium', 'high', 'critical'
    private String location;
    private Double latitude;
    private Double longitude;
    private String description;
    private LocalDateTime timestamp;
    private String reporterId;
    private String status; // 'pending', 'confirmed', 'rejected'
    private String submissionId;
    
    // Default constructor
    public Incident() {
        this.timestamp = LocalDateTime.now();
        this.status = "pending";
    }
    
    // Constructor with data
    public Incident(String id, String type, String severity, String location, 
                   Double latitude, Double longitude, String description, 
                   LocalDateTime timestamp, String reporterId, String status, String submissionId) {
        this.id = id;
        this.type = type;
        this.severity = severity;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
        this.reporterId = reporterId;
        this.status = status != null ? status : "pending";
        this.submissionId = submissionId;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getSeverity() {
        return severity;
    }
    
    public void setSeverity(String severity) {
        this.severity = severity;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public Double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getReporterId() {
        return reporterId;
    }
    
    public void setReporterId(String reporterId) {
        this.reporterId = reporterId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getSubmissionId() {
        return submissionId;
    }
    
    public void setSubmissionId(String submissionId) {
        this.submissionId = submissionId;
    }
    
    /**
     * Generate a unique ID for the incident
     */
    public static String generateId() {
        return "inc_" + System.currentTimeMillis() + "_" + 
               Long.toString(System.nanoTime(), 36);
    }
    
    @Override
    public String toString() {
        return "Incident{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", severity='" + severity + '\'' +
                ", location='" + location + '\'' +
                ", timestamp=" + timestamp +
                ", status='" + status + '\'' +
                '}';
    }
}













