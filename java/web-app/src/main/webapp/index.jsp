<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Traffic News App - Real-Time Traffic Incident Management</title>
    <meta name="description" content="Professional traffic incident management system with real-time updates and interactive maps">
    
    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <!-- Leaflet CSS -->
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"
          integrity="sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY="
          crossorigin=""/>
    
    <!-- Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/main.css">
</head>
<body>
    <div class="app-container">
        <!-- Header -->
        <header class="app-header">
            <h1>🚦 Traffic News App</h1>
            <div class="header-controls">
                <form method="get" action="${pageContext.request.contextPath}/home" class="header-search-form">
                    <input type="text" name="keyword" class="header-search-input" placeholder="Search incidents..." value="${keyword}">
                    <button type="submit" class="btn btn-primary btn-sm">🔍 Search</button>
                </form>
                <div class="header-filters">
                    <form method="get" action="${pageContext.request.contextPath}/home" class="header-filter-form">
                        <select id="type-filter" name="type" class="form-select form-select-sm">
                            <option value="">All Types</option>
                            <option value="accident" ${typeFilter == 'accident' ? 'selected' : ''}>Accident</option>
                            <option value="construction" ${typeFilter == 'construction' ? 'selected' : ''}>Construction</option>
                            <option value="hazard" ${typeFilter == 'hazard' ? 'selected' : ''}>Hazard</option>
                            <option value="closure" ${typeFilter == 'closure' ? 'selected' : ''}>Closure</option>
                        </select>
                        <select id="severity-filter" name="severity" class="form-select form-select-sm">
                            <option value="">All Severities</option>
                            <option value="low" ${severityFilter == 'low' ? 'selected' : ''}>Low</option>
                            <option value="medium" ${severityFilter == 'medium' ? 'selected' : ''}>Medium</option>
                            <option value="high" ${severityFilter == 'high' ? 'selected' : ''}>High</option>
                            <option value="critical" ${severityFilter == 'critical' ? 'selected' : ''}>Critical</option>
                        </select>
                        <input type="hidden" name="keyword" value="${keyword}">
                        <button type="submit" class="btn btn-primary btn-sm">Apply</button>
                        <a href="${pageContext.request.contextPath}/home" class="btn btn-secondary btn-sm">Clear</a>
                    </form>
                </div>
                <label>Refresh (s):</label>
                <input type="number" id="refresh-interval" value="60" min="5" max="120">
            </div>
        </header>

        <!-- Main Content -->
        <main class="app-main">
            <!-- Two Column Layout -->
            <div class="two-column-layout">
                <!-- Left Column: Report Form -->
                <div class="left-column">
                    <div class="report-section">
                        <h3>📝 Report Incident</h3>
                        <form id="report-form">
                            <div class="mb-3">
                                <label for="report-type" class="form-label">Type:</label>
                                <select id="report-type" name="type" class="form-select" required>
                                    <option value="">Select Type</option>
                                    <option value="accident">🚗 Accident</option>
                                    <option value="construction">🚧 Construction</option>
                                    <option value="hazard">⚠️ Hazard</option>
                                    <option value="closure">🚫 Closure</option>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label for="report-location" class="form-label">Location:</label>
                                <div class="location-input-wrapper">
                                    <input type="text" id="report-location" name="location" class="form-control" placeholder="Enter address or location..." required autocomplete="off">
                                    <div id="address-suggestions" class="address-suggestions" style="display: none;"></div>
                                </div>
                                <label class="map-preview-label">📍 Location Preview:</label>
                                <div class="map-preview-container">
                                    <div id="map-preview" class="map-preview"></div>
                                    <div id="map-preview-loading" class="map-preview-loading" style="display: none;">
                                        <span>🔍 Loading location...</span>
                                    </div>
                                </div>
                            </div>
                            <div class="mb-3">
                                <label for="report-severity" class="form-label">Severity:</label>
                                <select id="report-severity" name="severity" class="form-select" required>
                                    <option value="">Select Severity</option>
                                    <option value="low">🟢 Low</option>
                                    <option value="medium">🟡 Medium</option>
                                    <option value="high">🟠 High</option>
                                    <option value="critical">🔴 Critical</option>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label for="report-description" class="form-label">Description:</label>
                                <textarea id="report-description" name="description" class="form-control" rows="3" placeholder="Provide details about the incident..." required></textarea>
                            </div>
                            <input type="hidden" id="report-latitude" name="latitude">
                            <input type="hidden" id="report-longitude" name="longitude">
                            <button type="submit" class="btn btn-primary w-100">📤 Submit Report</button>
                        </form>
                        <!-- Offline Queue Status -->
                        <div id="offline-queue-status" class="alert alert-warning mt-3" style="display:none;">
                            <strong>Offline Queue:</strong> <span id="queue-count">0</span> pending
                        </div>
                    </div>
                </div>

                <!-- Right Column: Incident List -->
                <div class="right-column">
                    <div class="incident-list-section">
                        <h2>📋 Incidents <small class="text-muted">(${incidents != null ? incidents.size() : 0} found)</small></h2>
                        <div id="incident-list">
                            <c:choose>
                                <c:when test="${incidents != null && !empty incidents}">
                                    <table class="incidents-table">
                                        <thead>
                                            <tr>
                                                <th>Type</th>
                                                <th>Severity</th>
                                                <th>Location</th>
                                                <th>Description</th>
                                                <th>Time</th>
                                                <th>Status</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="incident" items="${incidents}">
                                                <tr data-id="${incident.id}" 
                                                    data-latitude="${incident.latitude != null ? incident.latitude : ''}" 
                                                    data-longitude="${incident.longitude != null ? incident.longitude : ''}"
                                                    class="incident-row">
                                                    <td>
                                                        <div class="incident-type-cell">
                                                            <span class="incident-type-icon">
                                                                <c:choose>
                                                                    <c:when test="${incident.type == 'accident'}">&#128663;</c:when>
                                                                    <c:when test="${incident.type == 'construction'}">&#128679;</c:when>
                                                                    <c:when test="${incident.type == 'hazard'}">&#9888;&#65039;</c:when>
                                                                    <c:when test="${incident.type == 'closure'}">&#128683;</c:when>
                                                                    <c:otherwise>&#128205;</c:otherwise>
                                                                </c:choose>
                                                            </span>
                                                            <span><c:out value="${incident.type}"/></span>
                                                        </div>
                                                    </td>
                                                    <td>
                                                        <span class="severity-badge severity-${incident.severity}">
                                                            <c:out value="${incident.severity}"/>
                                                        </span>
                                                    </td>
                                                    <td class="incident-location-cell">
                                                        <c:out value="${incident.location}"/>
                                                    </td>
                                                    <td class="incident-description-cell">
                                                        <c:out value="${incident.description != null ? incident.description : 'No description'}"/>
                                                    </td>
                                                    <td class="incident-time-cell">
                                                        <c:out value="${incident.timestamp}"/>
                                                    </td>
                                                    <td>
                                                        <span class="status-badge status-${incident.status != null ? incident.status : 'pending'}">
                                                            <c:out value="${incident.status != null ? incident.status : 'pending'}"/>
                                                        </span>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </c:when>
                                <c:otherwise>
                                    <div class="empty-state">
                                        <div class="empty-state-icon">📋</div>
                                        <p>No incidents found.</p>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                            <c:if test="${error != null}">
                                <div class="alert alert-danger">${error}</div>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Map Section (Full Width Below Columns) -->
            <div class="map-section-full">
                <h3>🗺️ Incident Map</h3>
                <div id="map-container" class="map-container"></div>
            </div>
        </main>
    </div>

    <!-- Notification Container -->
    <div id="notification-container" class="notification-container"></div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- Leaflet JS -->
    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"
            integrity="sha256-20nQCchB9co0qIjJZRGuk2/Z9VM+kNiyxNV1lvTlZBo="
            crossorigin=""></script>
    
    <!-- Application JavaScript -->
    <script>
        // API Configuration
        const API_CONFIG = {
            incidentService: 'http://localhost:8080/incident-service-1.0.0/api',
            mapService: 'http://localhost:8080/map-service-1.0.0/api',
            userService: 'http://localhost:8080/user-service-1.0.0/api',
            schedulerService: 'http://localhost:8080/scheduler-service-1.0.0/api'
        };
        
        // Global variables
        let map = null;
        let markers = [];
        let selectedMarker = null;
        let refreshTimer = null;
        let refreshInterval = 60000; // 60 seconds
        let mapPreview = null;
        let mapPreviewMarker = null;
        let geocodeTimeout = null;
        let selectedIncidentId = null;
        let allIncidents = [];
        let addressSuggestions = [];
        let selectedSuggestionIndex = -1;
        let autocompleteTimeout = null;
        
        // Initialize application
        document.addEventListener('DOMContentLoaded', function() {
            initializeMap();
            initializeMapPreview();
            initializeAddressAutocomplete();
            setupEventListeners();
            setupTableRowClickHandlers();
            checkOfflineQueue();
            startAutoRefresh();
        });
        
        /**
         * Initialize map preview for report form
         */
        function initializeMapPreview() {
            if (typeof L === 'undefined') {
                return;
            }
            
            const previewContainer = document.getElementById('map-preview');
            if (!previewContainer) return;
            
            // Initialize preview map
            mapPreview = L.map('map-preview', {
                zoomControl: false,
                attributionControl: false
            }).setView([43.6532, -79.3832], 13);
            
            // Add OpenStreetMap tiles
            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: '',
                maxZoom: 19
            }).addTo(mapPreview);
            
            // Add location input listener with debouncing
            const locationInput = document.getElementById('report-location');
            if (locationInput) {
                locationInput.addEventListener('input', function(e) {
                    const location = e.target.value.trim();
                    if (location.length > 3) {
                        // Debounce geocoding
                        clearTimeout(geocodeTimeout);
                        geocodeTimeout = setTimeout(() => {
                            geocodeAndShowPreview(location);
                        }, 800);
                    } else {
                        // Clear preview if input is too short
                        if (mapPreviewMarker) {
                            mapPreview.removeLayer(mapPreviewMarker);
                            mapPreviewMarker = null;
                        }
                        mapPreview.setView([43.6532, -79.3832], 13);
                    }
                });
            }
        }
        
        /**
         * Initialize address autocomplete functionality
         */
        function initializeAddressAutocomplete() {
            const locationInput = document.getElementById('report-location');
            const suggestionsContainer = document.getElementById('address-suggestions');
            
            if (!locationInput || !suggestionsContainer) return;
            
            // Input event - search for addresses
            locationInput.addEventListener('input', function(e) {
                const query = e.target.value.trim();
                selectedSuggestionIndex = -1;
                
                if (query.length < 3) {
                    hideSuggestions();
                    clearMapPreview();
                    return;
                }
                
                // Debounce API calls
                clearTimeout(autocompleteTimeout);
                autocompleteTimeout = setTimeout(() => {
                    searchAddresses(query);
                }, 300);
            });
            
            // Focus event - show suggestions if available
            locationInput.addEventListener('focus', function() {
                if (addressSuggestions.length > 0) {
                    showSuggestions();
                }
            });
            
            // Keyboard navigation
            locationInput.addEventListener('keydown', function(e) {
                if (!suggestionsContainer.style.display || suggestionsContainer.style.display === 'none') {
                    return;
                }
                
                if (e.key === 'ArrowDown') {
                    e.preventDefault();
                    selectedSuggestionIndex = Math.min(selectedSuggestionIndex + 1, addressSuggestions.length - 1);
                    updateSelectedSuggestion();
                } else if (e.key === 'ArrowUp') {
                    e.preventDefault();
                    selectedSuggestionIndex = Math.max(selectedSuggestionIndex - 1, -1);
                    updateSelectedSuggestion();
                } else if (e.key === 'Enter') {
                    e.preventDefault();
                    if (selectedSuggestionIndex >= 0 && addressSuggestions[selectedSuggestionIndex]) {
                        selectSuggestion(addressSuggestions[selectedSuggestionIndex]);
                    }
                } else if (e.key === 'Escape') {
                    hideSuggestions();
                }
            });
            
            // Click outside to hide suggestions
            document.addEventListener('click', function(e) {
                if (!locationInput.contains(e.target) && !suggestionsContainer.contains(e.target)) {
                    hideSuggestions();
                }
            });
        }
        
        /**
         * Search addresses using Nominatim API
         */
        async function searchAddresses(query) {
            const suggestionsContainer = document.getElementById('address-suggestions');
            if (!suggestionsContainer) return;
            
            // Show loading state
            suggestionsContainer.innerHTML = '<div class="address-suggestions-loading">🔍 Searching addresses...</div>';
            showSuggestions();
            
            try {
                // Use Nominatim API directly (free, no API key needed)
                const encodedQuery = encodeURIComponent(query);
                const url = 'https://nominatim.openstreetmap.org/search?format=json&q=' +
                            encodedQuery +
                            '&limit=5&addressdetails=1' +
                            '&viewbox=-79.6392,43.5816,-79.1159,43.8554&bounded=1';
                const response = await fetch(url, {
                    headers: {
                        'User-Agent': 'TrafficNewsApp/1.0'
                    }
                });
                
                if (response.ok) {
                    const results = await response.json();
                    addressSuggestions = results;
                    
                    if (results.length === 0) {
                        suggestionsContainer.innerHTML = '<div class="address-suggestions-empty">No addresses found</div>';
                    } else {
                        renderSuggestions(results);
                        // Auto-geocode first result and show on map
                        if (results.length > 0 && mapPreview) {
                            geocodeAndShowPreview(results[0]);
                        }
                    }
                } else {
                    suggestionsContainer.innerHTML = '<div class="address-suggestions-empty">Error searching addresses</div>';
                }
            } catch (error) {
                console.warn('Address search failed:', error);
                suggestionsContainer.innerHTML = '<div class="address-suggestions-empty">Error connecting to geocoding service</div>';
            }
        }
        
        /**
         * Escape HTML to prevent XSS
         */
        function escapeHtml(text) {
            const div = document.createElement('div');
            div.textContent = text;
            return div.innerHTML;
        }
        
        /**
         * Render address suggestions in dropdown
         */
        function renderSuggestions(suggestions) {
            const suggestionsContainer = document.getElementById('address-suggestions');
            if (!suggestionsContainer) return;
            
            suggestionsContainer.innerHTML = '';
            
            suggestions.forEach((suggestion, index) => {
                const item = document.createElement('div');
                item.className = 'address-suggestions-item';
                item.dataset.index = index;
                
                const displayName = suggestion.display_name || suggestion.name || 'Unknown location';
                const parts = displayName.split(',');
                const mainAddress = parts[0] || displayName;
                const details = parts.slice(1, 3).join(', ').trim();
                
                // Build HTML using string concatenation to avoid JSP EL parsing issues
                let html = '<span class="suggestion-icon">📍</span>';
                html += '<div class="suggestion-text">';
                html += '<div class="suggestion-address">' + escapeHtml(mainAddress) + '</div>';
                if (details) {
                    html += '<div class="suggestion-details">' + escapeHtml(details) + '</div>';
                }
                html += '</div>';
                item.innerHTML = html;
                
                item.addEventListener('click', () => selectSuggestion(suggestion));
                item.addEventListener('mouseenter', () => {
                    selectedSuggestionIndex = index;
                    updateSelectedSuggestion();
                });
                
                suggestionsContainer.appendChild(item);
            });
        }
        
        /**
         * Update selected suggestion highlight
         */
        function updateSelectedSuggestion() {
            const items = document.querySelectorAll('.address-suggestions-item');
            items.forEach((item, index) => {
                if (index === selectedSuggestionIndex) {
                    item.classList.add('selected');
                    item.scrollIntoView({ block: 'nearest', behavior: 'smooth' });
                } else {
                    item.classList.remove('selected');
                }
            });
        }
        
        /**
         * Select a suggestion and update the form
         */
        function selectSuggestion(suggestion) {
            const locationInput = document.getElementById('report-location');
            if (locationInput) {
                locationInput.value = suggestion.display_name || suggestion.name || '';
            }
            
            hideSuggestions();
            geocodeAndShowPreview(suggestion);
        }
        
        /**
         * Show suggestions dropdown
         */
        function showSuggestions() {
            const suggestionsContainer = document.getElementById('address-suggestions');
            if (suggestionsContainer) {
                suggestionsContainer.style.display = 'block';
            }
        }
        
        /**
         * Hide suggestions dropdown
         */
        function hideSuggestions() {
            const suggestionsContainer = document.getElementById('address-suggestions');
            if (suggestionsContainer) {
                suggestionsContainer.style.display = 'none';
            }
        }
        
        /**
         * Clear map preview
         */
        function clearMapPreview() {
            if (mapPreviewMarker && mapPreview) {
                mapPreview.removeLayer(mapPreviewMarker);
                mapPreviewMarker = null;
            }
            if (mapPreview) {
                mapPreview.setView([43.6532, -79.3832], 13);
            }
            document.getElementById('report-latitude').value = '';
            document.getElementById('report-longitude').value = '';
        }
        
        /**
         * Geocode location and show on preview map
         */
        async function geocodeAndShowPreview(locationOrSuggestion) {
            const loadingEl = document.getElementById('map-preview-loading');
            if (loadingEl) {
                loadingEl.style.display = 'flex';
            }
            
            try {
                let lat, lng;
                
                // Check if it's a suggestion object (from Nominatim) or a string
                if (typeof locationOrSuggestion === 'object' && locationOrSuggestion.lat && locationOrSuggestion.lon) {
                    lat = parseFloat(locationOrSuggestion.lat);
                    lng = parseFloat(locationOrSuggestion.lon);
                } else {
                    // Fallback: try to geocode the string
                    const query = typeof locationOrSuggestion === 'string' ? locationOrSuggestion : locationOrSuggestion.display_name;
                    const encodedQuery = encodeURIComponent(query);
                    const url = 'https://nominatim.openstreetmap.org/search?format=json&q=' +
                                encodedQuery +
                                '&limit=1&viewbox=-79.6392,43.5816,-79.1159,43.8554&bounded=1';
                    const response = await fetch(url, {
                        headers: {
                            'User-Agent': 'TrafficNewsApp/1.0'
                        }
                    });
                    
                    if (response.ok) {
                        const results = await response.json();
                        if (results.length > 0) {
                            lat = parseFloat(results[0].lat);
                            lng = parseFloat(results[0].lon);
                        }
                    }
                }
                
                if (lat && lng) {
                    // Update hidden fields
                    document.getElementById('report-latitude').value = lat;
                    document.getElementById('report-longitude').value = lng;
                    
                    // Remove existing marker
                    if (mapPreviewMarker && mapPreview) {
                        mapPreview.removeLayer(mapPreviewMarker);
                    }
                    
                    // Add new marker
                    if (mapPreview) {
                        mapPreviewMarker = L.marker([lat, lng]).addTo(mapPreview);
                        mapPreview.setView([lat, lng], 15);
                    }
                    
                    if (loadingEl) {
                        loadingEl.style.display = 'none';
                    }
                } else {
                    if (loadingEl) {
                        loadingEl.style.display = 'none';
                    }
                }
            } catch (error) {
                console.warn('Geocoding failed for preview:', error);
                if (loadingEl) {
                    loadingEl.style.display = 'none';
                }
            }
        }
        
        /**
         * Initialize map with server-side data
         */
        function initializeMap() {
            if (typeof L === 'undefined') {
                console.error('Leaflet.js is not loaded');
                return;
            }
            
            const mapContainer = document.getElementById('map-container');
            if (!mapContainer) {
                console.error('Map container not found');
                return;
            }
            
            // Wait a bit for container to be ready
            setTimeout(function() {
                // Initialize map
                map = L.map('map-container').setView([43.6532, -79.3832], 13);
                
                // Add OpenStreetMap tiles
                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    attribution: '© OpenStreetMap contributors',
                    maxZoom: 19
                }).addTo(map);
                
                // Force map to invalidate size after initialization
                setTimeout(function() {
                    if (map) {
                        map.invalidateSize();
                    }
                }, 100);
                
                // Add markers for incidents from server
                allIncidents = [
                    <c:forEach var="incident" items="${incidents}" varStatus="status">
                    {
                        id: '<c:out value="${incident.id}"/>',
                        type: '<c:out value="${incident.type}"/>',
                        severity: '<c:out value="${incident.severity}"/>',
                        location: '<c:out value="${incident.location}" escapeXml="false"/>',
                        description: '<c:out value="${incident.description != null ? incident.description : ''}" escapeXml="false"/>',
                        latitude: ${incident.latitude != null ? incident.latitude : 0},
                        longitude: ${incident.longitude != null ? incident.longitude : 0},
                        timestamp: '<c:out value="${incident.timestamp}"/>',
                        status: '<c:out value="${incident.status != null ? incident.status : 'pending'}"/>'
                    }<c:if test="${!status.last}">,</c:if>
                    </c:forEach>
                ];
                
                displayIncidentsOnMap(allIncidents);
            }, 200);
        }
        
        /**
         * Display incidents on map
         */
        function displayIncidentsOnMap(incidents) {
            // Clear existing markers
            markers.forEach(marker => map.removeLayer(marker));
            markers = [];
            selectedMarker = null;
            
            incidents.forEach(function(incident) {
                if (incident.latitude && incident.longitude && incident.latitude != 0 && incident.longitude != 0) {
                    const color = incident.severity === 'low' ? 'green' : 
                                 incident.severity === 'medium' ? 'yellow' : 
                                 incident.severity === 'high' ? 'orange' : 'red';
                    
                    const icon = L.divIcon({
                        className: 'incident-marker',
                        html: '<div style="background-color: ' + color + '; width: 20px; height: 20px; border-radius: 50%; border: 2px solid white; box-shadow: 0 2px 4px rgba(0,0,0,0.3);"></div>',
                        iconSize: [20, 20]
                    });
                    
                    const marker = L.marker([incident.latitude, incident.longitude], { icon: icon }).addTo(map);
                    marker.incidentId = incident.id;
                    marker.bindPopup(
                        '<strong>' + capitalizeFirst(incident.type) + '</strong><br>' +
                        'Location: ' + incident.location + '<br>' +
                        'Severity: ' + capitalizeFirst(incident.severity) + '<br>' +
                        (incident.description ? 'Description: ' + incident.description + '<br>' : '') +
                        'Time: ' + new Date(incident.timestamp).toLocaleString()
                    );
                    
                    // Add click handler to marker
                    marker.on('click', function() {
                        highlightIncidentRow(incident.id);
                    });
                    
                    markers.push(marker);
                }
            });
            
            // Fit bounds to show all markers
            if (markers.length > 0) {
                const group = new L.featureGroup(markers);
                map.fitBounds(group.getBounds().pad(0.1));
            }
            
            // If an incident is selected, highlight it on map
            if (selectedIncidentId) {
                highlightIncidentOnMap(selectedIncidentId);
            }
        }
        
        /**
         * Setup table row click handlers
         */
        function setupTableRowClickHandlers() {
            const tableRows = document.querySelectorAll('.incident-row');
            tableRows.forEach(function(row) {
                row.addEventListener('click', function() {
                    const incidentId = row.getAttribute('data-id');
                    const latitude = parseFloat(row.getAttribute('data-latitude'));
                    const longitude = parseFloat(row.getAttribute('data-longitude'));
                    
                    if (incidentId) {
                        selectedIncidentId = incidentId;
                        highlightIncidentRow(incidentId);
                        
                        if (latitude && longitude && latitude != 0 && longitude != 0) {
                            highlightIncidentOnMap(incidentId);
                            map.setView([latitude, longitude], 15);
                        }
                    }
                });
            });
        }
        
        /**
         * Highlight incident row in table
         */
        function highlightIncidentRow(incidentId) {
            // Remove previous selection
            document.querySelectorAll('.incident-row').forEach(function(row) {
                row.classList.remove('selected');
            });
            
            // Add selection to clicked row
            const row = document.querySelector('.incident-row[data-id="' + incidentId + '"]');
            if (row) {
                row.classList.add('selected');
                row.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
            }
        }
        
        /**
         * Highlight incident on map
         */
        function highlightIncidentOnMap(incidentId) {
            // Remove previous selection
            if (selectedMarker) {
                const prevColor = selectedMarker.options.icon.options.html.match(/background-color: ([^;]+)/);
                if (prevColor) {
                    selectedMarker.setIcon(selectedMarker.options.icon);
                }
            }
            
            // Find and highlight selected marker
            const marker = markers.find(m => m.incidentId === incidentId);
            if (marker) {
                selectedMarker = marker;
                // Create highlighted icon
                const incident = allIncidents.find(i => i.id === incidentId);
                if (incident) {
                    const color = incident.severity === 'low' ? 'green' : 
                                 incident.severity === 'medium' ? 'yellow' : 
                                 incident.severity === 'high' ? 'orange' : 'red';
                    
                    const highlightedIcon = L.divIcon({
                        className: 'incident-marker selected',
                        html: '<div style="background-color: ' + color + '; width: 28px; height: 28px; border-radius: 50%; border: 3px solid #667eea; box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.3), 0 2px 8px rgba(0,0,0,0.4);"></div>',
                        iconSize: [28, 28]
                    });
                    
                    marker.setIcon(highlightedIcon);
                    marker.openPopup();
                    map.setView([incident.latitude, incident.longitude], 15);
                }
            }
        }
        
        /**
         * Initialize map preview for report form
         */
        function initializeMapPreview() {
            if (typeof L === 'undefined') {
                return;
            }
            
            const previewContainer = document.getElementById('map-preview');
            if (!previewContainer) return;
            
            // Initialize preview map
            mapPreview = L.map('map-preview', {
                zoomControl: false,
                attributionControl: false
            }).setView([43.6532, -79.3832], 13);
            
            // Add OpenStreetMap tiles
            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: '',
                maxZoom: 19
            }).addTo(mapPreview);
            
            // Add location input listener with debouncing
            const locationInput = document.getElementById('report-location');
            if (locationInput) {
                locationInput.addEventListener('input', function(e) {
                    const location = e.target.value.trim();
                    if (location.length > 3) {
                        // Debounce geocoding
                        clearTimeout(geocodeTimeout);
                        geocodeTimeout = setTimeout(() => {
                            geocodeAndShowPreview(location);
                        }, 800);
                    } else {
                        // Clear preview if input is too short
                        if (mapPreviewMarker) {
                            mapPreview.removeLayer(mapPreviewMarker);
                            mapPreviewMarker = null;
                        }
                        mapPreview.setView([43.6532, -79.3832], 13);
                    }
                });
            }
        }
        
        /**
         * Setup event listeners
         */
        function setupEventListeners() {
            // Report form submission
            const reportForm = document.getElementById('report-form');
            if (reportForm) {
                reportForm.addEventListener('submit', handleReportSubmit);
            }
            
            // Refresh interval change
            const refreshIntervalInput = document.getElementById('refresh-interval');
            if (refreshIntervalInput) {
                refreshIntervalInput.addEventListener('change', function(e) {
                    const interval = parseInt(e.target.value) * 1000;
                    if (interval >= 5000) {
                        refreshInterval = interval;
                        startAutoRefresh();
                        showBanner('Refresh interval set to ' + (interval / 1000) + ' seconds', 'info');
                    }
                });
            }
            
            // Update map when location is entered in report form
            const reportLocationInput = document.getElementById('report-location');
            if (reportLocationInput) {
                reportLocationInput.addEventListener('input', function(e) {
                    const location = e.target.value.trim();
                    if (location.length > 3) {
                        clearTimeout(geocodeTimeout);
                        geocodeTimeout = setTimeout(() => {
                            updateMainMapFromLocation(location);
                        }, 1000);
                    }
                });
            }
        }
        
        /**
         * Update main map when location is entered in report form
         */
        async function updateMainMapFromLocation(location) {
            try {
                const encodedLocation = encodeURIComponent(location);
                const response = await fetch(API_CONFIG.mapService + '/map/geocode?address=' + encodedLocation);
                if (response.ok) {
                    const coords = await response.json();
                    if (coords && coords.lat && coords.lng) {
                        // Center map on location
                        map.setView([coords.lat, coords.lng], 15);
                        
                        // Add temporary marker for new location
                        if (selectedMarker && selectedMarker.isTemporary) {
                            map.removeLayer(selectedMarker);
                        }
                        
                        const tempIcon = L.divIcon({
                            className: 'incident-marker temporary',
                            html: '<div style="background-color: #667eea; width: 24px; height: 24px; border-radius: 50%; border: 3px solid white; box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.3), 0 2px 8px rgba(0,0,0,0.4);"></div>',
                            iconSize: [24, 24]
                        });
                        
                        selectedMarker = L.marker([coords.lat, coords.lng], { icon: tempIcon }).addTo(map);
                        selectedMarker.isTemporary = true;
                        selectedMarker.bindPopup('<strong>New Location</strong><br>' + location).openPopup();
                    }
                }
            } catch (error) {
                console.warn('Geocoding failed for main map:', error);
            }
        }
        
        /**
         * Handle report form submission
         */
        async function handleReportSubmit(event) {
            event.preventDefault();
            
            const formData = new FormData(event.target);
            const location = formData.get('location');
            
            // Geocode location if coordinates not provided
            let latitude = parseFloat(formData.get('latitude')) || null;
            let longitude = parseFloat(formData.get('longitude')) || null;
            
            if (!latitude || !longitude) {
                try {
                    const encodedLocation = encodeURIComponent(location);
                    const response = await fetch(API_CONFIG.mapService + '/map/geocode?address=' + encodedLocation);
                    if (response.ok) {
                        const coords = await response.json();
                        if (coords) {
                            latitude = coords.lat;
                            longitude = coords.lng;
                        }
                    }
                } catch (error) {
                    console.warn('Geocoding failed:', error);
                }
            }
            
            const incidentData = {
                type: formData.get('type'),
                severity: formData.get('severity'),
                location: location,
                description: formData.get('description'),
                latitude: latitude,
                longitude: longitude,
                reporterId: 'user_' + Date.now()
            };
            
            try {
                const response = await fetch(API_CONFIG.incidentService + '/incidents', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(incidentData)
                });
                
                if (response.ok) {
                    const incident = await response.json();
                    if (incident) {
                        showBanner('Incident reported successfully!', 'success');
                        event.target.reset();
                        // Reload page to show new incident
                        setTimeout(() => {
                            window.location.reload();
                        }, 1500);
                    } else {
                        showBanner('Failed to report incident', 'error');
                    }
                } else {
                    throw new Error('HTTP ' + response.status);
                }
            } catch (error) {
                console.error('Error submitting incident:', error);
                // Try to queue for offline processing
                try {
                    const queueResponse = await fetch(API_CONFIG.schedulerService + '/scheduler/queue', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(incidentData)
                    });
                    if (queueResponse.ok) {
                        showBanner('Offline: Incident queued for submission', 'info');
                    } else {
                        showBanner('Failed to report incident: ' + error.message, 'error');
                    }
                } catch (queueError) {
                    showBanner('Failed to report incident: ' + error.message, 'error');
                }
            }
        }
        
        /**
         * Refresh incidents list via AJAX
         */
        async function refreshIncidents() {
            try {
                const response = await fetch(API_CONFIG.incidentService + '/incidents');
                if (response.ok) {
                    const incidents = await response.json();
                    updateIncidentList(incidents);
                    updateIncidentsOnMap(incidents);
                }
            } catch (error) {
                console.warn('Failed to refresh incidents:', error);
            }
        }
        
        /**
         * Update incident list in the table
         */
        function updateIncidentList(incidents) {
            const incidentListContainer = document.getElementById('incident-list');
            if (!incidentListContainer) return;
            
            if (!incidents || incidents.length === 0) {
                incidentListContainer.innerHTML = '<div class="empty-state"><div class="empty-state-icon">📋</div><p>No incidents found.</p></div>';
                return;
            }
            
            // Update count in header
            const header = document.querySelector('.incident-list-section h2');
            if (header) {
                header.innerHTML = '📋 Incidents <small class="text-muted">(' + incidents.length + ' found)</small>';
            }
            
            // Build table HTML
            let tableHTML = '<table class="incidents-table"><thead><tr><th>Type</th><th>Severity</th><th>Location</th><th>Description</th><th>Time</th><th>Status</th></tr></thead><tbody>';
            
            incidents.forEach(function(incident) {
                const typeEmoji = incident.type === 'accident' ? '&#128663;' :
                                 incident.type === 'construction' ? '&#128679;' :
                                 incident.type === 'hazard' ? '&#9888;&#65039;' :
                                 incident.type === 'closure' ? '&#128683;' : '&#128205;';
                
                const lat = incident.latitude || '';
                const lng = incident.longitude || '';
                const description = incident.description || 'No description';
                const status = incident.status || 'pending';
                
                tableHTML += '<tr data-id="' + escapeHtml(incident.id) + '" ' +
                            'data-latitude="' + lat + '" ' +
                            'data-longitude="' + lng + '" ' +
                            'class="incident-row">' +
                            '<td><div class="incident-type-cell">' +
                            '<span class="incident-type-icon">' + typeEmoji + '</span>' +
                            '<span>' + escapeHtml(incident.type) + '</span>' +
                            '</div></td>' +
                            '<td><span class="severity-badge severity-' + escapeHtml(incident.severity) + '">' +
                            escapeHtml(incident.severity) + '</span></td>' +
                            '<td class="incident-location-cell">' + escapeHtml(incident.location) + '</td>' +
                            '<td class="incident-description-cell">' + escapeHtml(description) + '</td>' +
                            '<td class="incident-time-cell">' + escapeHtml(incident.timestamp) + '</td>' +
                            '<td><span class="status-badge status-' + escapeHtml(status) + '">' +
                            escapeHtml(status) + '</span></td>' +
                            '</tr>';
            });
            
            tableHTML += '</tbody></table>';
            incidentListContainer.innerHTML = tableHTML;
            
            // Re-attach click handlers to new rows
            setupTableRowClickHandlers();
        }
        
        /**
         * Update incidents on map
         */
        function updateIncidentsOnMap(incidents) {
            allIncidents = incidents;
            if (map) {
                displayIncidentsOnMap(incidents);
            }
        }
        
        /**
         * Start auto-refresh (incidents only)
         */
        function startAutoRefresh() {
            if (refreshTimer) {
                clearInterval(refreshTimer);
            }
            refreshTimer = setInterval(() => {
                refreshIncidents();
            }, refreshInterval);
        }
        
        /**
         * Check offline queue status
         */
        async function checkOfflineQueue() {
            try {
                const response = await fetch(API_CONFIG.schedulerService + '/scheduler/queue');
                if (response.ok) {
                    const queue = await response.json();
                    if (queue && queue.length > 0) {
                        const statusEl = document.getElementById('offline-queue-status');
                        const countEl = document.getElementById('queue-count');
                        if (statusEl && countEl) {
                            statusEl.style.display = 'block';
                            countEl.textContent = queue.length;
                        }
                    }
                }
            } catch (error) {
                console.warn('Could not check offline queue:', error);
            }
        }
        
        /**
         * Show banner notification
         */
        function showBanner(message, type = 'info', duration = 3000) {
            const container = document.getElementById('notification-container');
            if (!container) return;
            
            const banner = document.createElement('div');
            banner.className = 'alert alert-' + (type === 'error' ? 'danger' : type) + ' alert-dismissible fade show';
            banner.style.position = 'fixed';
            banner.style.top = '20px';
            banner.style.right = '20px';
            banner.style.zIndex = '9999';
            banner.style.minWidth = '300px';
            banner.innerHTML = message + '<button type="button" class="btn-close" data-bs-dismiss="alert"></button>';
            
            container.appendChild(banner);
            
            setTimeout(() => {
                banner.remove();
            }, duration);
        }
        
        /**
         * Capitalize first letter
         */
        function capitalizeFirst(str) {
            if (!str) return '';
            return str.charAt(0).toUpperCase() + str.slice(1);
        }
    </script>
</body>
</html>



