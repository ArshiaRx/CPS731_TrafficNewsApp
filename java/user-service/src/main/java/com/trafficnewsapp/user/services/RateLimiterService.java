package com.trafficnewsapp.user.services;

import java.util.HashMap;
import java.util.Map;

/**
 * RateLimiterService (C08)
 * Business Logic Layer - Enforces submission frequency limits
 */
public class RateLimiterService {
    private int limit; // Max requests
    private long windowMs; // Time window in milliseconds
    private Map<String, RequestRecord> requests; // userId -> RequestRecord
    
    private static class RequestRecord {
        long firstRequestTime;
        int count;
    }
    
    public RateLimiterService(int limit, long windowMs) {
        this.limit = limit;
        this.windowMs = windowMs;
        this.requests = new HashMap<>();
    }
    
    public RateLimiterService() {
        this(5, 3600000); // Default: 5 requests per hour
    }
    
    /**
     * Check if a user can make a submission
     * @param userId User identifier
     * @return true if allowed
     */
    public boolean canSubmit(String userId) {
        long now = System.currentTimeMillis();
        RequestRecord record = requests.get(userId);
        
        if (record == null) {
            record = new RequestRecord();
            record.firstRequestTime = now;
            record.count = 1;
            requests.put(userId, record);
            return true;
        }
        
        // Check if window has expired
        if (now - record.firstRequestTime > windowMs) {
            // Reset window
            record.firstRequestTime = now;
            record.count = 1;
            return true;
        }
        
        // Check if under limit
        if (record.count < limit) {
            record.count++;
            return true;
        }
        
        return false; // Rate limit exceeded
    }
    
    /**
     * Reset rate limit for a user
     * @param userId User identifier
     */
    public void resetUser(String userId) {
        requests.remove(userId);
    }
}













