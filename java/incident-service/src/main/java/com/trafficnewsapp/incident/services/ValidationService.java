package com.trafficnewsapp.incident.services;

import com.trafficnewsapp.incident.models.Incident;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * ValidationService (C03)
 * Business Logic Layer - Input validation
 */
public class ValidationService {
    private static final List<String> INCIDENT_TYPES = Arrays.asList("accident", "construction", "closure", "hazard");
    private static final List<String> SEVERITY_LEVELS = Arrays.asList("low", "medium", "high", "critical");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    /**
     * Validate incident data
     * @param incident Incident to validate
     * @return ValidationResult with valid flag and errors list
     */
    public ValidationResult validateIncident(Incident incident) {
        List<String> errors = new ArrayList<>();
        
        // Type validation
        if (incident.getType() == null || !INCIDENT_TYPES.contains(incident.getType().toLowerCase())) {
            errors.add("Invalid incident type. Must be one of: " + String.join(", ", INCIDENT_TYPES));
        }
        
        // Severity validation
        if (incident.getSeverity() == null || !SEVERITY_LEVELS.contains(incident.getSeverity().toLowerCase())) {
            errors.add("Invalid severity level. Must be one of: " + String.join(", ", SEVERITY_LEVELS));
        }
        
        // Location validation
        if (incident.getLocation() == null || incident.getLocation().trim().isEmpty()) {
            errors.add("Location is required");
        } else if (incident.getLocation().length() > 200) {
            errors.add("Location must be 200 characters or less");
        }
        
        // Description validation
        if (incident.getDescription() != null && incident.getDescription().length() > 1000) {
            errors.add("Description must be 1000 characters or less");
        }
        
        // Coordinates validation
        if (incident.getLatitude() != null) {
            if (incident.getLatitude() < -90 || incident.getLatitude() > 90) {
                errors.add("Invalid latitude. Must be between -90 and 90");
            }
        }
        
        if (incident.getLongitude() != null) {
            if (incident.getLongitude() < -180 || incident.getLongitude() > 180) {
                errors.add("Invalid longitude. Must be between -180 and 180");
            }
        }
        
        return new ValidationResult(errors.isEmpty(), errors);
    }
    
    /**
     * Validate coordinates
     * @param latitude Latitude value
     * @param longitude Longitude value
     * @return ValidationResult
     */
    public ValidationResult validateCoordinates(Double latitude, Double longitude) {
        List<String> errors = new ArrayList<>();
        
        if (latitude != null && (latitude < -90 || latitude > 90)) {
            errors.add("Invalid latitude. Must be between -90 and 90");
        }
        
        if (longitude != null && (longitude < -180 || longitude > 180)) {
            errors.add("Invalid longitude. Must be between -180 and 180");
        }
        
        return new ValidationResult(errors.isEmpty(), errors);
    }
    
    /**
     * Sanitize string input
     * @param input Input to sanitize
     * @return Sanitized string
     */
    public String sanitizeString(String input) {
        if (input == null) {
            return "";
        }
        return input.trim().replaceAll("[<>]", "");
    }
    
    /**
     * Validate email format
     * @param email Email to validate
     * @return true if valid
     */
    public boolean validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validation result class
     */
    public static class ValidationResult {
        private boolean valid;
        private List<String> errors;
        
        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors != null ? errors : new ArrayList<>();
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public List<String> getErrors() {
            return errors;
        }
    }
}













