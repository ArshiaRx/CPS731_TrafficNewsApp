package com.trafficnewsapp.incident.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trafficnewsapp.incident.dao.IncidentDAO;
import com.trafficnewsapp.incident.models.Incident;
import com.trafficnewsapp.incident.services.FilterService;
import com.trafficnewsapp.incident.services.IncidentService;
import com.trafficnewsapp.incident.services.SearchService;
import com.trafficnewsapp.incident.services.ValidationService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * IncidentServlet
 * REST API endpoint for incident operations
 */
public class IncidentServlet extends HttpServlet {
    private IncidentService incidentService;
    private ValidationService validationService;
    private FilterService filterService;
    private SearchService searchService;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        IncidentDAO incidentDAO = new IncidentDAO();
        this.incidentService = new IncidentService(incidentDAO);
        this.validationService = new ValidationService();
        this.filterService = new FilterService();
        this.searchService = new SearchService();
        
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, 
            (com.google.gson.JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> 
                context.serialize(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
        this.gson = gsonBuilder.create();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // CORS headers
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        String pathInfo = request.getPathInfo();
        
        try {
            // Get incident by ID: /api/incidents/{id}
            if (pathInfo != null && pathInfo.length() > 1) {
                String id = pathInfo.substring(1);
                Incident incident = incidentService.getIncidentById(id);
                
                if (incident != null) {
                    out.print(gson.toJson(incident));
                    response.setStatus(HttpServletResponse.SC_OK);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gson.toJson(Map.of("error", "Incident not found")));
                }
            } else {
                // Get all incidents with optional filters
                List<Incident> incidents = incidentService.getAllIncidents();
                
                // Apply filters
                String type = request.getParameter("type");
                String severity = request.getParameter("severity");
                String status = request.getParameter("status");
                
                Map<String, String> filters = new HashMap<>();
                if (type != null && !type.isEmpty()) filters.put("type", type);
                if (severity != null && !severity.isEmpty()) filters.put("severity", severity);
                if (status != null && !status.isEmpty()) filters.put("status", status);
                
                if (!filters.isEmpty()) {
                    incidents = filterService.filterIncidents(incidents, filters);
                }
                
                // Apply search
                String keyword = request.getParameter("keyword");
                if (keyword != null && !keyword.isEmpty()) {
                    incidents = searchService.searchIncidents(incidents, keyword);
                }
                
                // Apply sorting
                String sortBy = request.getParameter("sortBy");
                String order = request.getParameter("order");
                if (sortBy != null) {
                    incidents = incidentService.sortIncidents(incidents, sortBy, order != null ? order : "desc");
                }
                
                out.print(gson.toJson(incidents));
                response.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Map.of("error", e.getMessage())));
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // CORS headers
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            // Parse JSON request body
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                json.append(line);
            }
            
            Incident incident = gson.fromJson(json.toString(), Incident.class);
            
            // Validate
            ValidationService.ValidationResult validation = validationService.validateIncident(incident);
            if (!validation.isValid()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson(Map.of("error", "Validation failed", "errors", validation.getErrors())));
                return;
            }
            
            // Create incident
            Incident created = incidentService.createIncident(incident);
            if (created != null) {
                out.print(gson.toJson(created));
                response.setStatus(HttpServletResponse.SC_CREATED);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(gson.toJson(Map.of("error", "Failed to create incident")));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Map.of("error", e.getMessage())));
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // CORS headers
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(Map.of("error", "Incident ID required")));
            return;
        }
        
        String id = pathInfo.substring(1);
        
        try {
            // Parse JSON request body
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                json.append(line);
            }
            
            Incident updates = gson.fromJson(json.toString(), Incident.class);
            
            // Validate
            ValidationService.ValidationResult validation = validationService.validateIncident(updates);
            if (!validation.isValid()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson(Map.of("error", "Validation failed", "errors", validation.getErrors())));
                return;
            }
            
            // Update incident
            Incident updated = incidentService.updateIncident(id, updates);
            if (updated != null) {
                out.print(gson.toJson(updated));
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.toJson(Map.of("error", "Incident not found")));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Map.of("error", e.getMessage())));
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // CORS headers
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(Map.of("error", "Incident ID required")));
            return;
        }
        
        String id = pathInfo.substring(1);
        
        try {
            boolean deleted = incidentService.deleteIncident(id);
            if (deleted) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(gson.toJson(Map.of("message", "Incident deleted successfully")));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.toJson(Map.of("error", "Incident not found")));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Map.of("error", e.getMessage())));
        }
    }
}



