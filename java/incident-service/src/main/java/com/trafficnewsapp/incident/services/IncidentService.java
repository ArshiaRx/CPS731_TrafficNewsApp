package com.trafficnewsapp.incident.services;

import com.trafficnewsapp.incident.dao.IncidentDAO;
import com.trafficnewsapp.incident.models.Incident;

import java.util.List;
import java.util.stream.Collectors;

/**
 * IncidentService (C02)
 * Business Logic Layer - Core incident management
 */
public class IncidentService {
    private IncidentDAO incidentDAO;
    
    public IncidentService(IncidentDAO incidentDAO) {
        this.incidentDAO = incidentDAO;
    }
    
    /**
     * Get all incidents
     * @return List of all incidents
     */
    public List<Incident> getAllIncidents() {
        return incidentDAO.getAllIncidents();
    }
    
    /**
     * Get incident by ID
     * @param id Incident ID
     * @return Incident or null if not found
     */
    public Incident getIncidentById(String id) {
        return incidentDAO.getIncidentById(id);
    }
    
    /**
     * Create new incident
     * @param incidentData Incident data
     * @return Created incident or null if failed
     */
    public Incident createIncident(Incident incidentData) {
        if (incidentData.getId() == null) {
            incidentData.setId(Incident.generateId());
        }
        
        boolean success = incidentDAO.saveIncident(incidentData);
        return success ? incidentData : null;
    }
    
    /**
     * Update incident
     * @param id Incident ID
     * @param updates Fields to update
     * @return Updated incident or null if not found
     */
    public Incident updateIncident(String id, Incident updates) {
        Incident incident = incidentDAO.getIncidentById(id);
        if (incident == null) {
            return null;
        }
        
        // Update fields
        if (updates.getType() != null) incident.setType(updates.getType());
        if (updates.getSeverity() != null) incident.setSeverity(updates.getSeverity());
        if (updates.getLocation() != null) incident.setLocation(updates.getLocation());
        if (updates.getLatitude() != null) incident.setLatitude(updates.getLatitude());
        if (updates.getLongitude() != null) incident.setLongitude(updates.getLongitude());
        if (updates.getDescription() != null) incident.setDescription(updates.getDescription());
        if (updates.getStatus() != null) incident.setStatus(updates.getStatus());
        
        boolean success = incidentDAO.saveIncident(incident);
        return success ? incident : null;
    }
    
    /**
     * Delete incident
     * @param id Incident ID
     * @return true if successful
     */
    public boolean deleteIncident(String id) {
        return incidentDAO.deleteIncident(id);
    }
    
    /**
     * Sort incidents
     * @param incidents Incidents to sort
     * @param sortBy Sort field ('time', 'severity', 'type')
     * @param order Sort order ('asc', 'desc')
     * @return Sorted list of incidents
     */
    public List<Incident> sortIncidents(List<Incident> incidents, String sortBy, String order) {
        List<Incident> sorted = incidents.stream().collect(Collectors.toList());
        
        sorted.sort((a, b) -> {
            int comparison = 0;
            
            switch (sortBy != null ? sortBy.toLowerCase() : "time") {
                case "time":
                    comparison = a.getTimestamp().compareTo(b.getTimestamp());
                    break;
                case "severity":
                    int severityOrderA = getSeverityOrder(a.getSeverity());
                    int severityOrderB = getSeverityOrder(b.getSeverity());
                    comparison = Integer.compare(severityOrderA, severityOrderB);
                    break;
                case "type":
                    comparison = a.getType().compareTo(b.getType());
                    break;
                default:
                    comparison = 0;
            }
            
            return "asc".equalsIgnoreCase(order) ? comparison : -comparison;
        });
        
        return sorted;
    }
    
    /**
     * Get severity order for sorting
     */
    private int getSeverityOrder(String severity) {
        switch (severity != null ? severity.toLowerCase() : "") {
            case "critical": return 4;
            case "high": return 3;
            case "medium": return 2;
            case "low": return 1;
            default: return 0;
        }
    }
    
    /**
     * Get incidents by status
     * @param status Status filter
     * @return List of incidents with the specified status
     */
    public List<Incident> getIncidentsByStatus(String status) {
        return incidentDAO.getIncidentsByStatus(status);
    }
}













