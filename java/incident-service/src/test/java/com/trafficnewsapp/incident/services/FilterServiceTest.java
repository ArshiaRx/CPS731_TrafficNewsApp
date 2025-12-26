package com.trafficnewsapp.incident.services;

import com.trafficnewsapp.incident.models.Incident;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FilterService
 * Tests at least 3 methods: applyFilters, clearFilters
 */
@DisplayName("FilterService Tests")
public class FilterServiceTest {
    private FilterService filterService;
    private List<Incident> testIncidents;
    
    @BeforeEach
    void setUp() {
        filterService = new FilterService();
        
        // Create test incidents
        testIncidents = new ArrayList<>();
        
        Incident inc1 = new Incident();
        inc1.setType("accident");
        inc1.setSeverity("high");
        inc1.setStatus("confirmed");
        testIncidents.add(inc1);
        
        Incident inc2 = new Incident();
        inc2.setType("construction");
        inc2.setSeverity("medium");
        inc2.setStatus("pending");
        testIncidents.add(inc2);
        
        Incident inc3 = new Incident();
        inc3.setType("accident");
        inc3.setSeverity("low");
        inc3.setStatus("confirmed");
        testIncidents.add(inc3);
    }
    
    @Test
    @DisplayName("Test applyFilters - filter by type")
    void testApplyFilters_ByType() {
        // Setup
        Map<String, String> filters = new HashMap<>();
        filters.put("type", "accident");
        
        // Execute
        List<Incident> filtered = filterService.filterIncidents(testIncidents, filters);
        
        // Verify
        assertEquals(2, filtered.size(), "Should return 2 accidents");
        assertTrue(filtered.stream().allMatch(inc -> "accident".equals(inc.getType())), 
                  "All incidents should be accidents");
    }
    
    @Test
    @DisplayName("Test applyFilters - filter by severity")
    void testApplyFilters_BySeverity() {
        // Setup
        Map<String, String> filters = new HashMap<>();
        filters.put("severity", "high");
        
        // Execute
        List<Incident> filtered = filterService.filterIncidents(testIncidents, filters);
        
        // Verify
        assertEquals(1, filtered.size(), "Should return 1 high severity incident");
        assertEquals("high", filtered.get(0).getSeverity(), "Severity should be high");
    }
    
    @Test
    @DisplayName("Test applyFilters - multiple filters")
    void testApplyFilters_MultipleFilters() {
        // Setup
        Map<String, String> filters = new HashMap<>();
        filters.put("type", "accident");
        filters.put("status", "confirmed");
        
        // Execute
        List<Incident> filtered = filterService.filterIncidents(testIncidents, filters);
        
        // Verify
        assertEquals(2, filtered.size(), "Should return 2 confirmed accidents");
        assertTrue(filtered.stream().allMatch(inc -> 
            "accident".equals(inc.getType()) && "confirmed".equals(inc.getStatus())), 
            "All should be confirmed accidents");
    }
    
    @Test
    @DisplayName("Test clearFilters - should clear all active filters")
    void testClearFilters() {
        // Setup - set some filters
        filterService.setFilter("type", "accident");
        filterService.setFilter("severity", "high");
        
        // Execute
        filterService.clearFilters();
        
        // Verify
        Map<String, String> activeFilters = filterService.getActiveFilters();
        assertTrue(activeFilters.isEmpty(), "Active filters should be empty after clearing");
    }
}













