package com.trafficnewsapp.scheduler.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trafficnewsapp.scheduler.dao.SubmissionDAO;
import com.trafficnewsapp.scheduler.models.Submission;
import com.trafficnewsapp.scheduler.services.OfflineSubmissionQueue;

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
 * SchedulerServlet
 * REST API endpoint for scheduler and queue operations
 */
public class SchedulerServlet extends HttpServlet {
    private OfflineSubmissionQueue offlineQueue;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        SubmissionDAO submissionDAO = new SubmissionDAO();
        this.offlineQueue = new OfflineSubmissionQueue(submissionDAO);
        
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
            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson(Map.of("error", "Invalid endpoint")));
                return;
            }
            
            String[] pathParts = pathInfo.substring(1).split("/");
            
            if (pathParts.length > 0) {
                switch (pathParts[0]) {
                    case "queue":
                        handleGetQueue(request, response, out);
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
                    case "queue":
                        handleAddToQueue(request, response, out);
                        break;
                    case "process":
                        handleProcessQueue(request, response, out);
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
    
    private void handleGetQueue(HttpServletRequest request, HttpServletResponse response,
                                PrintWriter out) {
        var submissions = offlineQueue.getPendingSubmissions();
        out.print(gson.toJson(submissions));
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
    private void handleAddToQueue(HttpServletRequest request, HttpServletResponse response,
                                 PrintWriter out) throws IOException {
        StringBuilder json = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            json.append(line);
        }
        
        String incidentData = json.toString();
        Submission submission = offlineQueue.addSubmission(incidentData);
        
        if (submission != null) {
            out.print(gson.toJson(submission));
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Map.of("error", "Failed to add to queue")));
        }
    }
    
    private void handleProcessQueue(HttpServletRequest request, HttpServletResponse response,
                                   PrintWriter out) {
        // Process all pending submissions
        var submissions = offlineQueue.getPendingSubmissions();
        int processed = 0;
        
        for (Submission submission : submissions) {
            if (offlineQueue.processSubmission(submission.getId())) {
                processed++;
            }
        }
        
        Map<String, Object> result = Map.of(
            "processed", processed,
            "total", submissions.size()
        );
        
        out.print(gson.toJson(result));
        response.setStatus(HttpServletResponse.SC_OK);
    }
}



