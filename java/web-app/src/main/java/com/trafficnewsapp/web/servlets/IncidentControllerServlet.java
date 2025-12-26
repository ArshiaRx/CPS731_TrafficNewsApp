package com.trafficnewsapp.web.servlets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * IncidentControllerServlet
 * Fetches incidents from incident-service API and passes to JSP
 */
public class IncidentControllerServlet extends HttpServlet {
    
    private static final String INCIDENT_SERVICE_URL = "http://localhost:8080/incident-service-1.0.0/api/incidents";
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        this.gson = new Gson();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Fetch incidents from incident service
            List<Map<String, Object>> incidents = fetchIncidents();
            
            // Get filter parameters
            String typeFilter = request.getParameter("type");
            String severityFilter = request.getParameter("severity");
            String keyword = request.getParameter("keyword");
            
            // Apply filters
            List<Map<String, Object>> filteredIncidents = filterIncidents(incidents, typeFilter, severityFilter, keyword);
            
            // Set attributes for JSP
            request.setAttribute("incidents", filteredIncidents);
            request.setAttribute("allIncidents", incidents);
            request.setAttribute("typeFilter", typeFilter != null ? typeFilter : "");
            request.setAttribute("severityFilter", severityFilter != null ? severityFilter : "");
            request.setAttribute("keyword", keyword != null ? keyword : "");
            
            // Forward to JSP
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading incidents: " + e.getMessage());
            request.setAttribute("incidents", new ArrayList<>());
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }
    
    /**
     * Fetch incidents from incident service API
     */
    private List<Map<String, Object>> fetchIncidents() throws IOException {
        URL url = new URL(INCIDENT_SERVICE_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();
            
            // Parse JSON response
            Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
            List<Map<String, Object>> incidents = gson.fromJson(response.toString(), listType);
            return incidents != null ? incidents : new ArrayList<>();
        } else {
            throw new IOException("Failed to fetch incidents. Response code: " + responseCode);
        }
    }
    
    /**
     * Filter incidents based on type, severity, and keyword
     */
    private List<Map<String, Object>> filterIncidents(List<Map<String, Object>> incidents, 
                                                       String typeFilter, String severityFilter, String keyword) {
        List<Map<String, Object>> filtered = new ArrayList<>();
        
        for (Map<String, Object> incident : incidents) {
            boolean matches = true;
            
            // Type filter
            if (typeFilter != null && !typeFilter.isEmpty()) {
                String type = (String) incident.get("type");
                if (!typeFilter.equalsIgnoreCase(type)) {
                    matches = false;
                }
            }
            
            // Severity filter
            if (matches && severityFilter != null && !severityFilter.isEmpty()) {
                String severity = (String) incident.get("severity");
                if (!severityFilter.equalsIgnoreCase(severity)) {
                    matches = false;
                }
            }
            
            // Keyword search
            if (matches && keyword != null && !keyword.isEmpty()) {
                String location = (String) incident.get("location");
                String description = (String) incident.get("description");
                String searchText = (location + " " + (description != null ? description : "")).toLowerCase();
                if (!searchText.contains(keyword.toLowerCase())) {
                    matches = false;
                }
            }
            
            if (matches) {
                filtered.add(incident);
            }
        }
        
        return filtered;
    }
}











