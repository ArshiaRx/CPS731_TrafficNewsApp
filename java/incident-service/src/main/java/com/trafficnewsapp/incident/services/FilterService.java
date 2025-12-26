package com.trafficnewsapp.incident.services;

import com.trafficnewsapp.incident.models.Incident;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * FilterService (C04)
 * Business Logic Layer - Filtering logic
 */
public class FilterService {
    private Map<String, String> activeFilters;
    
    public FilterService() {
        this.activeFilters = new HashMap<>();
    }
    
    /**
     * Apply filters to incidents
     * @param incidents Incidents to filter
     * @param filters Filter criteria
     * @return Filtered incidents
     */
    public List<Incident> filterIncidents(List<Incident> incidents, Map<String, String> filters) {
        if (filters != null) {
            this.activeFilters.putAll(filters);
        }
        
        return incidents.stream()
            .filter(incident -> {
                // Type filter
                if (activeFilters.containsKey("type") && activeFilters.get("type") != null) {
                    if (!activeFilters.get("type").equals(incident.getType())) {
                        return false;
                    }
                }
                
                // Severity filter
                if (activeFilters.containsKey("severity") && activeFilters.get("severity") != null) {
                    if (!activeFilters.get("severity").equals(incident.getSeverity())) {
                        return false;
                    }
                }
                
                // Status filter
                if (activeFilters.containsKey("status") && activeFilters.get("status") != null) {
                    if (!activeFilters.get("status").equals(incident.getStatus())) {
                        return false;
                    }
                }
                
                return true;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Apply filters to incidents (using current active filters)
     * @param incidents Incidents to filter
     * @return Filtered incidents
     */
    public List<Incident> applyFilters(List<Incident> incidents) {
        return filterIncidents(incidents, null);
    }
    
    /**
     * Clear all filters
     */
    public void clearFilters() {
        this.activeFilters.clear();
    }
    
    /**
     * Get active filters
     * @return Active filter state
     */
    public Map<String, String> getActiveFilters() {
        return new HashMap<>(activeFilters);
    }
    
    /**
     * Set filter
     * @param filterType Filter type ('type', 'severity', 'status')
     * @param value Filter value
     */
    public void setFilter(String filterType, String value) {
        if (filterType != null && (filterType.equals("type") || filterType.equals("severity") || filterType.equals("status"))) {
            activeFilters.put(filterType, value);
        }
    }
    
    /**
     * Remove specific filter
     * @param filterType Filter type to remove
     */
    public void removeFilter(String filterType) {
        activeFilters.remove(filterType);
    }
}













