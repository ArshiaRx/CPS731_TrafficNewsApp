package com.trafficnewsapp.user.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trafficnewsapp.user.dao.RouteDAO;
import com.trafficnewsapp.user.models.Route;
import com.trafficnewsapp.user.services.SavedRoutesService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * RouteServlet
 * REST API endpoint for route operations
 */
public class RouteServlet extends HttpServlet {
    private SavedRoutesService savedRoutesService;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        RouteDAO routeDAO = new RouteDAO();
        this.savedRoutesService = new SavedRoutesService(routeDAO);
        
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
        String userId = request.getParameter("userId");
        
        if (userId == null) {
            userId = "default"; // Default user
        }
        
        try {
            if (pathInfo != null && pathInfo.length() > 1) {
                // Get route by ID
                String id = pathInfo.substring(1);
                Route route = savedRoutesService.getRouteById(id);
                
                if (route != null) {
                    out.print(gson.toJson(route));
                    response.setStatus(HttpServletResponse.SC_OK);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gson.toJson(Map.of("error", "Route not found")));
                }
            } else {
                // Get all routes for user
                var routes = savedRoutesService.getSavedRoutes(userId);
                out.print(gson.toJson(routes));
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
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                json.append(line);
            }
            
            Route route = gson.fromJson(json.toString(), Route.class);
            Route created = savedRoutesService.addRoute(route);
            
            if (created != null) {
                out.print(gson.toJson(created));
                response.setStatus(HttpServletResponse.SC_CREATED);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(gson.toJson(Map.of("error", "Failed to create route")));
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
            out.print(gson.toJson(Map.of("error", "Route ID required")));
            return;
        }
        
        String id = pathInfo.substring(1);
        
        try {
            boolean deleted = savedRoutesService.deleteRoute(id);
            if (deleted) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(gson.toJson(Map.of("message", "Route deleted successfully")));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.toJson(Map.of("error", "Route not found")));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Map.of("error", e.getMessage())));
        }
    }
}



