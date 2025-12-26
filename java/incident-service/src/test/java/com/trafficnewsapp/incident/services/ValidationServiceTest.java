package com.trafficnewsapp.incident.services;

import com.trafficnewsapp.incident.models.Incident;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ValidationService
 * Tests at least 3 methods: validateIncident, validateCoordinates
 */
@DisplayName("ValidationService Tests")
public class ValidationServiceTest {
    private ValidationService validationService;
    
    @BeforeEach
    void setUp() {
        validationService = new ValidationService();
    }
    
    @Test
    @DisplayName("Test validateIncident - valid incident should pass")
    void testValidateIncident_Valid() {
        // Setup
        Incident incident = new Incident();
        incident.setType("accident");
        incident.setSeverity("high");
        incident.setLocation("Test Location");
        incident.setDescription("Test description");
        incident.setLatitude(43.6532);
        incident.setLongitude(-79.3832);
        
        // Execute
        ValidationService.ValidationResult result = validationService.validateIncident(incident);
        
        // Verify
        assertTrue(result.isValid(), "Valid incident should pass validation");
        assertTrue(result.getErrors().isEmpty(), "Should have no errors");
    }
    
    @Test
    @DisplayName("Test validateIncident - invalid type should fail")
    void testValidateIncident_InvalidType() {
        // Setup
        Incident incident = new Incident();
        incident.setType("invalid_type");
        incident.setSeverity("high");
        incident.setLocation("Test Location");
        
        // Execute
        ValidationService.ValidationResult result = validationService.validateIncident(incident);
        
        // Verify
        assertFalse(result.isValid(), "Invalid type should fail validation");
        assertFalse(result.getErrors().isEmpty(), "Should have errors");
    }
    
    @Test
    @DisplayName("Test validateCoordinates - valid coordinates should pass")
    void testValidateCoordinates_Valid() {
        // Execute
        ValidationService.ValidationResult result = validationService.validateCoordinates(43.6532, -79.3832);
        
        // Verify
        assertTrue(result.isValid(), "Valid coordinates should pass validation");
        assertTrue(result.getErrors().isEmpty(), "Should have no errors");
    }
    
    @Test
    @DisplayName("Test validateCoordinates - invalid latitude should fail")
    void testValidateCoordinates_InvalidLatitude() {
        // Execute
        ValidationService.ValidationResult result = validationService.validateCoordinates(91.0, -79.3832);
        
        // Verify
        assertFalse(result.isValid(), "Invalid latitude should fail validation");
        assertFalse(result.getErrors().isEmpty(), "Should have errors");
    }
    
    @Test
    @DisplayName("Test validateCoordinates - invalid longitude should fail")
    void testValidateCoordinates_InvalidLongitude() {
        // Execute
        ValidationService.ValidationResult result = validationService.validateCoordinates(43.6532, -181.0);
        
        // Verify
        assertFalse(result.isValid(), "Invalid longitude should fail validation");
        assertFalse(result.getErrors().isEmpty(), "Should have errors");
    }
}













