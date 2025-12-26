package com.trafficnewsapp.user.models;

import java.time.LocalDateTime;

/**
 * Route Model
 * Represents a saved route/area for monitoring
 */
public class Route {
    private String id;
    private String name;
    private Double latitude;
    private Double longitude;
    private Integer radius;
    private String userId;
    private LocalDateTime createdAt;
    
    public Route() {
        this.radius = 1000; // Default 1km
        this.createdAt = LocalDateTime.now();
    }
    
    public Route(String id, String name, Double latitude, Double longitude, 
                 Integer radius, String userId, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius != null ? radius : 1000;
        this.userId = userId;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
    
    public static String generateId() {
        return "route_" + System.currentTimeMillis() + "_" + 
               Long.toString(System.nanoTime(), 36);
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    
    public Integer getRadius() { return radius; }
    public void setRadius(Integer radius) { this.radius = radius; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}













