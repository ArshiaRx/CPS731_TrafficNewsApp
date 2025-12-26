package com.trafficnewsapp.map.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * MapService
 * Business Logic Layer - Map operations and geocoding
 */
public class MapService {
    
    private static final String NOMINATIM_BASE_URL = "https://nominatim.openstreetmap.org";
    
    /**
     * Geocode address to coordinates
     * @param address Address to geocode
     * @return Map with "lat" and "lng" keys, or null if failed
     */
    public Map<String, Double> geocode(String address) {
        try {
            String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8.toString());
            String urlString = NOMINATIM_BASE_URL + "/search?format=json&q=" + encodedAddress + "&limit=1";
            
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "TrafficNewsApp/1.0");
            
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                
                // Simple JSON parsing (in production, use Gson)
                String json = response.toString();
                if (json.contains("\"lat\"") && json.contains("\"lon\"")) {
                    // Extract lat and lon from JSON
                    int latStart = json.indexOf("\"lat\":\"") + 7;
                    int latEnd = json.indexOf("\"", latStart);
                    int lonStart = json.indexOf("\"lon\":\"") + 7;
                    int lonEnd = json.indexOf("\"", lonStart);
                    
                    if (latStart > 6 && lonStart > 6) {
                        String latStr = json.substring(latStart, latEnd);
                        String lonStr = json.substring(lonStart, lonEnd);
                        
                        Map<String, Double> result = new HashMap<>();
                        result.put("lat", Double.parseDouble(latStr));
                        result.put("lng", Double.parseDouble(lonStr));
                        return result;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("MapService.geocode error: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Reverse geocode coordinates to address
     * @param lat Latitude
     * @param lng Longitude
     * @return Address string or null if failed
     */
    public String reverseGeocode(double lat, double lng) {
        try {
            String urlString = NOMINATIM_BASE_URL + "/reverse?format=json&lat=" + lat + "&lon=" + lng;
            
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "TrafficNewsApp/1.0");
            
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                
                String json = response.toString();
                if (json.contains("\"display_name\"")) {
                    int nameStart = json.indexOf("\"display_name\":\"") + 16;
                    int nameEnd = json.indexOf("\"", nameStart);
                    if (nameStart > 15) {
                        return json.substring(nameStart, nameEnd);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("MapService.reverseGeocode error: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Get map tile URL (for frontend use)
     * @param z Zoom level
     * @param x Tile X coordinate
     * @param y Tile Y coordinate
     * @return Tile URL
     */
    public String getTileUrl(int z, int x, int y) {
        return "https://tile.openstreetmap.org/" + z + "/" + x + "/" + y + ".png";
    }
}













