/**
 * MapViewManager (C06)
 * Business Logic Layer - Map visualization logic using Leaflet
 */
class MapViewManager {
    constructor() {
        this.map = null;
        this.markers = [];
        this.incidentMarkers = new Map();
    }

    /**
     * Initialize map view
     * @param {string} containerId - Map container ID
     * @param {Object} options - Map options
     * @returns {Promise<boolean>}
     */
    async initializeMap(containerId, options = {}) {
        try {
            if (typeof L === 'undefined') {
                console.error('Leaflet.js is not loaded');
                return false;
            }

            const defaultOptions = {
                center: [43.6532, -79.3832], // Toronto default
                zoom: 13,
                ...options
            };

            this.map = L.map(containerId).setView(defaultOptions.center, defaultOptions.zoom);
            
            // Add OpenStreetMap tiles
            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: '© OpenStreetMap contributors',
                maxZoom: 19
            }).addTo(this.map);

            return true;
        } catch (error) {
            console.error('MapViewManager.initializeMap error:', error);
            return false;
        }
    }

    /**
     * Display incidents on map
     * @param {Array<Incident>} incidents - Incidents to display
     * @returns {Promise<boolean>}
     */
    async displayIncidents(incidents) {
        try {
            // Clear existing markers
            this.clearIncidents();

            // Add markers for each incident
            for (const incident of incidents) {
                if (incident.latitude && incident.longitude) {
                    const popupContent = this.createPopupContent(incident);
                    const marker = this.addMarker(
                        incident.latitude,
                        incident.longitude,
                        {
                            severity: incident.severity,
                            popup: popupContent
                        }
                    );

                    if (marker) {
                        this.incidentMarkers.set(incident.id, marker);
                    }
                }
            }

            // Fit bounds to show all markers
            if (this.markers.length > 0) {
                const group = new L.featureGroup(this.markers);
                this.map.fitBounds(group.getBounds().pad(0.1));
            }

            return true;
        } catch (error) {
            console.error('MapViewManager.displayIncidents error:', error);
            return false;
        }
    }

    /**
     * Add marker to map
     * @param {number} lat - Latitude
     * @param {number} lng - Longitude
     * @param {Object} options - Marker options
     * @returns {L.Marker|null}
     */
    addMarker(lat, lng, options = {}) {
        try {
            if (!this.map) {
                console.error('Map not initialized');
                return null;
            }

            const icon = this.getIconForSeverity(options.severity || 'low');
            const marker = L.marker([lat, lng], { icon: icon }).addTo(this.map);

            if (options.popup) {
                marker.bindPopup(options.popup);
            }

            this.markers.push(marker);
            return marker;
        } catch (error) {
            console.error('MapViewManager.addMarker error:', error);
            return null;
        }
    }

    /**
     * Clear all markers
     */
    clearMarkers() {
        this.markers.forEach(marker => {
            this.map.removeLayer(marker);
        });
        this.markers = [];
        this.incidentMarkers.clear();
    }

    /**
     * Clear incident markers
     */
    clearIncidents() {
        this.clearMarkers();
    }

    /**
     * Get icon based on severity
     * @param {string} severity - Incident severity
     * @returns {L.Icon} Leaflet icon
     */
    getIconForSeverity(severity) {
        const colors = {
            'low': 'green',
            'medium': 'yellow',
            'high': 'orange',
            'critical': 'red'
        };

        const color = colors[severity] || 'blue';
        
        return L.divIcon({
            className: 'incident-marker',
            html: `<div style="background-color: ${color}; width: 20px; height: 20px; border-radius: 50%; border: 2px solid white; box-shadow: 0 2px 4px rgba(0,0,0,0.3);"></div>`,
            iconSize: [20, 20],
            popupAnchor: [0, -10]
        });
    }

    /**
     * Create popup content for incident
     * @param {Incident} incident - Incident data
     * @returns {string} HTML popup content
     */
    createPopupContent(incident) {
        return `
            <div style="min-width: 200px;">
                <h4 style="margin: 0 0 8px 0; color: #333;">${this.capitalizeFirst(incident.type)}</h4>
                <p style="margin: 4px 0;"><strong>Location:</strong> ${incident.location}</p>
                <p style="margin: 4px 0;"><strong>Severity:</strong> <span style="color: ${this.getSeverityColor(incident.severity)};">${this.capitalizeFirst(incident.severity)}</span></p>
                ${incident.description ? `<p style="margin: 4px 0;"><strong>Description:</strong> ${incident.description}</p>` : ''}
                <p style="margin: 4px 0; font-size: 0.85em; color: #666;">${new Date(incident.timestamp).toLocaleString()}</p>
            </div>
        `;
    }

    /**
     * Get severity color
     * @param {string} severity - Severity level
     * @returns {string} Color code
     */
    getSeverityColor(severity) {
        const colors = {
            'low': '#28a745',
            'medium': '#ffc107',
            'high': '#fd7e14',
            'critical': '#dc3545'
        };
        return colors[severity] || '#007bff';
    }

    /**
     * Capitalize first letter
     * @param {string} str - String to capitalize
     * @returns {string}
     */
    capitalizeFirst(str) {
        return str.charAt(0).toUpperCase() + str.slice(1);
    }

    /**
     * Fit bounds to show all markers
     */
    fitBounds() {
        if (!this.map || this.markers.length === 0) {
            return;
        }
        const group = new L.featureGroup(this.markers);
        this.map.fitBounds(group.getBounds().pad(0.1));
    }
}












