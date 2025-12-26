package com.trafficnewsapp.incident.services;

import com.trafficnewsapp.incident.models.Incident;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SearchService (C05)
 * Business Logic Layer - Search functionality
 */
public class SearchService {
    private LinkedList<String> searchHistory;
    private static final int MAX_HISTORY = 10;
    
    public SearchService() {
        this.searchHistory = new LinkedList<>();
    }
    
    /**
     * Search incidents by keyword
     * @param incidents Incidents to search
     * @param keyword Search keyword
     * @return Matching incidents
     */
    public List<Incident> searchIncidents(List<Incident> incidents, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return incidents;
        }
        
        String searchTerm = keyword.toLowerCase().trim();
        addToHistory(searchTerm);
        
        return incidents.stream()
            .filter(incident -> {
                // Search in location
                if (incident.getLocation() != null && 
                    incident.getLocation().toLowerCase().contains(searchTerm)) {
                    return true;
                }
                
                // Search in description
                if (incident.getDescription() != null && 
                    incident.getDescription().toLowerCase().contains(searchTerm)) {
                    return true;
                }
                
                // Search in type
                if (incident.getType() != null && 
                    incident.getType().toLowerCase().contains(searchTerm)) {
                    return true;
                }
                
                return false;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Add search term to history
     * @param term Search term
     */
    private void addToHistory(String term) {
        if (term != null && !term.isEmpty() && !searchHistory.contains(term)) {
            searchHistory.addFirst(term);
            // Keep only last 10 searches
            if (searchHistory.size() > MAX_HISTORY) {
                searchHistory.removeLast();
            }
        }
    }
    
    /**
     * Get search history
     * @return Search history
     */
    public List<String> getSearchHistory() {
        return new ArrayList<>(searchHistory);
    }
    
    /**
     * Clear search history
     */
    public void clearSearchHistory() {
        searchHistory.clear();
    }
}













