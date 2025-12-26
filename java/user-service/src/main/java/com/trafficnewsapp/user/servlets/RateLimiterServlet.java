package com.trafficnewsapp.user.servlets;

import com.google.gson.Gson;
import com.trafficnewsapp.user.services.RateLimiterService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * RateLimiterServlet
 * REST API endpoint for rate limiting checks
 */
@WebServlet("/api/ratelimit/*")
public class RateLimiterServlet extends HttpServlet {
    private RateLimiterService rateLimiterService;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        this.rateLimiterService = new RateLimiterService();
        this.gson = new Gson();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        String userId = request.getParameter("userId");
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(Map.of("error", "userId parameter required")));
            return;
        }
        
        boolean canSubmit = rateLimiterService.canSubmit(userId);
        Map<String, Object> result = Map.of(
            "userId", userId,
            "canSubmit", canSubmit
        );
        
        out.print(gson.toJson(result));
        response.setStatus(HttpServletResponse.SC_OK);
    }
}













