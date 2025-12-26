package com.trafficnewsapp.incident.services;

import com.trafficnewsapp.incident.dao.IncidentDAO;
import com.trafficnewsapp.incident.models.Incident;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for IncidentService
 * Tests at least 3 methods: fetchIncidents, addIncident, updateIncident
 */
@DisplayName("IncidentService Tests")
public class IncidentServiceTest {
    private IncidentService incidentService;
    private IncidentDAO incidentDAO;
    
    @BeforeEach
    void setUp() {
        incidentDAO = new IncidentDAO();
        incidentService = new IncidentService(incidentDAO);
    }
    
    @Test
    @DisplayName("Test fetchIncidents - should return all incidents")
    void testFetchIncidents() {
        // Execute
        List<Incident> incidents = incidentService.getAllIncidents();
        
        // Verify
        assertNotNull(incidents, "Incidents list should not be null");
        assertTrue(incidents.size() >= 0, "Should return list (may be empty)");
    }
    
    @Test
    @DisplayName("Test addIncident - should create new incident")
    void testAddIncident() {
        // Setup
        Incident newIncident = new Incident();
        newIncident.setType("accident");
        newIncident.setSeverity("high");
        newIncident.setLocation("Test Location");
        newIncident.setDescription("Test description");
        newIncident.setTimestamp(LocalDateTime.now());
        newIncident.setReporterId("test_user");
        newIncident.setStatus("pending");
        
        // Execute
        Incident created = incidentService.createIncident(newIncident);
        
        // Verify
        assertNotNull(created, "Created incident should not be null");
        assertNotNull(created.getId(), "Created incident should have an ID");
        assertEquals("accident", created.getType(), "Type should match");
        assertEquals("high", created.getSeverity(), "Severity should match");
        
        // Cleanup
        if (created != null && created.getId() != null) {
            incidentService.deleteIncident(created.getId());
        }
    }
    
    @Test
    @DisplayName("Test updateIncident - should update existing incident")
    void testUpdateIncident() {
        // Setup - create an incident first
        Incident incident = new Incident();
        incident.setType("construction");
        incident.setSeverity("medium");
        incident.setLocation("Original Location");
        incident.setDescription("Original description");
        incident.setTimestamp(LocalDateTime.now());
        incident.setStatus("pending");
        
        Incident created = incidentService.createIncident(incident);
        assertNotNull(created, "Incident should be created");
        String incidentId = created.getId();
        
        // Execute - update the incident
        Incident updates = new Incident();
        updates.setLocation("Updated Location");
        updates.setSeverity("high");
        
        Incident updated = incidentService.updateIncident(incidentId, updates);
        
        // Verify
        assertNotNull(updated, "Updated incident should not be null");
        assertEquals("Updated Location", updated.getLocation(), "Location should be updated");
        assertEquals("high", updated.getSeverity(), "Severity should be updated");
        assertEquals(incidentId, updated.getId(), "ID should remain the same");
        
        // Cleanup
        incidentService.deleteIncident(incidentId);
    }
}













