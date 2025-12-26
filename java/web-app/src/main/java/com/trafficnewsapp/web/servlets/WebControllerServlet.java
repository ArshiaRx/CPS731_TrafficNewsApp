package com.trafficnewsapp.web.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * WebControllerServlet
 * Main controller for web application - forwards to IncidentController for main page
 */
public class WebControllerServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String path = request.getServletPath();
        String requestURI = request.getRequestURI();
        
        // Don't handle static resources - they should be handled by default servlet
        if (requestURI != null && requestURI.contains("/resources/")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        if (path == null || path.equals("/") || path.equals("/index")) {
            // Forward to IncidentController to fetch data
            request.getRequestDispatcher("/home").forward(request, response);
        } else if (path.equals("/incidents")) {
            request.getRequestDispatcher("/incidents/list").forward(request, response);
        } else if (path.equals("/report")) {
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/home").forward(request, response);
        }
    }
}



