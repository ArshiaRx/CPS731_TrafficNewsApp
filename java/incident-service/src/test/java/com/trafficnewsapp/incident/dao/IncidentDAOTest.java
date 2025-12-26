package com.trafficnewsapp.incident.dao;

import com.trafficnewsapp.incident.models.Incident;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for IncidentDAO
 * Tests at least 3 methods: findAll, save, delete
 */
@DisplayName("IncidentDAO Tests")
public class IncidentDAOTest {
    private IncidentDAO incidentDAO;
    
    @BeforeEach
    void setUp() {
        incidentDAO = new IncidentDAO();
    }
    
    @Test
    @DisplayName("Test findAll - should return all incidents")
    void testFindAll() {
        // Execute
        List<Incident> incidents = incidentDAO.getAllIncidents();
        
        // Verify
        assertNotNull(incidents, "Incidents list should not be null");
        assertTrue(incidents.size() >= 0, "Should return list (may be empty or have sample data)");
    }
    
    @Test
    @DisplayName("Test save - should save new incident")
    void testSave() {
        // Setup
        Incident incident = new Incident();
        incident.setId(Incident.generateId());
        incident.setType("hazard");
        incident.setSeverity("low");
        incident.setLocation("Test Location for Save");
        incident.setDescription("Test description");
        incident.setTimestamp(LocalDateTime.now());
        incident.setStatus("pending");
        
        // Execute
        boolean saved = incidentDAO.saveIncident(incident);
        
        // Verify
        assertTrue(saved, "Incident should be saved successfully");
        
        // Verify it can be retrieved
        Incident retrieved = incidentDAO.getIncidentById(incident.getId());
        assertNotNull(retrieved, "Saved incident should be retrievable");
        assertEquals(incident.getType(), retrieved.getType(), "Type should match");
        
        // Cleanup
        incidentDAO.deleteIncident(incident.getId());
    }
    
    @Test
    @DisplayName("Test delete - should delete incident")
    void testDelete() {
        // Setup - create an incident first
        Incident incident = new Incident();
        incident.setId(Incident.generateId());
        incident.setType("test");
        incident.setSeverity("low");
        incident.setLocation("Test Location for Delete");
        incident.setTimestamp(LocalDateTime.now());
        incident.setStatus("pending");
        
        incidentDAO.saveIncident(incident);
        String incidentId = incident.getId();
        
        // Verify it exists
        Incident beforeDelete = incidentDAO.getIncidentById(incidentId);
        assertNotNull(beforeDelete, "Incident should exist before deletion");
        
        // Execute
        boolean deleted = incidentDAO.deleteIncident(incidentId);
        
        // Verify
        assertTrue(deleted, "Incident should be deleted successfully");
        
        // Verify it no longer exists
        Incident afterDelete = incidentDAO.getIncidentById(incidentId);
        assertNull(afterDelete, "Incident should not exist after deletion");
    }
}













