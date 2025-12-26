package com.trafficnewsapp.map.servlets;

import com.google.gson.Gson;
import com.trafficnewsapp.map.services.MapService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * MapServlet
 * REST API endpoint for map operations
 */
public class MapServlet extends HttpServlet {
    private MapService mapService;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        this.mapService = new MapService();
        this.gson = new Gson();
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
            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson(Map.of("error", "Invalid endpoint")));
                return;
            }
            
            String[] pathParts = pathInfo.substring(1).split("/");
            
            if (pathParts.length > 0) {
                switch (pathParts[0]) {
                    case "geocode":
                        handleGeocode(request, response, out);
                        break;
                    case "reverse":
                        handleReverseGeocode(request, response, out);
                        break;
                    case "tile":
                        handleTile(request, response, out);
                        break;
                    default:
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.print(gson.toJson(Map.of("error", "Endpoint not found")));
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Map.of("error", e.getMessage())));
        }
    }
    
    private void handleGeocode(HttpServletRequest request, HttpServletResponse response, 
                               PrintWriter out) {
        String address = request.getParameter("address");
        if (address == null || address.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(Map.of("error", "Address parameter required")));
            return;
        }
        
        Map<String, Double> coords = mapService.geocode(address);
        if (coords != null) {
            out.print(gson.toJson(coords));
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print(gson.toJson(Map.of("error", "Address not found")));
        }
    }
    
    private void handleReverseGeocode(HttpServletRequest request, HttpServletResponse response,
                                     PrintWriter out) {
        String latStr = request.getParameter("lat");
        String lngStr = request.getParameter("lng");
        
        if (latStr == null || lngStr == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(Map.of("error", "Lat and lng parameters required")));
            return;
        }
        
        try {
            double lat = Double.parseDouble(latStr);
            double lng = Double.parseDouble(lngStr);
            
            String address = mapService.reverseGeocode(lat, lng);
            if (address != null) {
                Map<String, String> result = new HashMap<>();
                result.put("address", address);
                out.print(gson.toJson(result));
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.toJson(Map.of("error", "Location not found")));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(Map.of("error", "Invalid lat/lng format")));
        }
    }
    
    private void handleTile(HttpServletRequest request, HttpServletResponse response,
                           PrintWriter out) {
        String zStr = request.getParameter("z");
        String xStr = request.getParameter("x");
        String yStr = request.getParameter("y");
        
        if (zStr == null || xStr == null || yStr == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(Map.of("error", "z, x, y parameters required")));
            return;
        }
        
        try {
            int z = Integer.parseInt(zStr);
            int x = Integer.parseInt(xStr);
            int y = Integer.parseInt(yStr);
            
            String tileUrl = mapService.getTileUrl(z, x, y);
            Map<String, String> result = new HashMap<>();
            result.put("url", tileUrl);
            out.print(gson.toJson(result));
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(Map.of("error", "Invalid tile coordinates")));
        }
    }
}



