/**
 * API Client
 * Handles AJAX calls to Java Servlet microservices
 */

const API_CONFIG = {
    incidentService: 'http://localhost:8080/incident-service-1.0.0/api',
    mapService: 'http://localhost:8080/map-service-1.0.0/api',
    userService: 'http://localhost:8080/user-service-1.0.0/api',
    schedulerService: 'http://localhost:8080/scheduler-service-1.0.0/api'
};

class APIClient {
    /**
     * Make HTTP request
     */
    async request(url, options = {}) {
        try {
            const response = await fetch(url, {
                headers: {
                    'Content-Type': 'application/json',
                    ...options.headers
                },
                ...options
            });
            
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            return await response.json();
        } catch (error) {
            console.error('API request error:', error);
            throw error;
        }
    }
    
    // Incident Service Methods
    async getAllIncidents(filters = {}) {
        const params = new URLSearchParams();
        if (filters.type) params.append('type', filters.type);
        if (filters.severity) params.append('severity', filters.severity);
        if (filters.status) params.append('status', filters.status);
        if (filters.keyword) params.append('keyword', filters.keyword);
        if (filters.sortBy) params.append('sortBy', filters.sortBy);
        if (filters.order) params.append('order', filters.order);
        
        const url = `${API_CONFIG.incidentService}/incidents${params.toString() ? '?' + params : ''}`;
        return await this.request(url);
    }
    
    async getIncidentById(id) {
        return await this.request(`${API_CONFIG.incidentService}/incidents/${id}`);
    }
    
    async createIncident(incidentData) {
        return await this.request(`${API_CONFIG.incidentService}/incidents`, {
            method: 'POST',
            body: JSON.stringify(incidentData)
        });
    }
    
    async updateIncident(id, updates) {
        return await this.request(`${API_CONFIG.incidentService}/incidents/${id}`, {
            method: 'PUT',
            body: JSON.stringify(updates)
        });
    }
    
    async deleteIncident(id) {
        return await this.request(`${API_CONFIG.incidentService}/incidents/${id}`, {
            method: 'DELETE'
        });
    }
    
    // Map Service Methods
    async geocode(address) {
        return await this.request(`${API_CONFIG.mapService}/map/geocode?address=${encodeURIComponent(address)}`);
    }
    
    async reverseGeocode(lat, lng) {
        return await this.request(`${API_CONFIG.mapService}/map/reverse?lat=${lat}&lng=${lng}`);
    }
    
    // User Service Methods
    async getRoutes(userId = 'default') {
        return await this.request(`${API_CONFIG.userService}/routes?userId=${userId}`);
    }
    
    async createRoute(routeData) {
        return await this.request(`${API_CONFIG.userService}/routes`, {
            method: 'POST',
            body: JSON.stringify(routeData)
        });
    }
    
    async deleteRoute(routeId) {
        return await this.request(`${API_CONFIG.userService}/routes/${routeId}`, {
            method: 'DELETE'
        });
    }
    
    async checkRateLimit(userId) {
        return await this.request(`${API_CONFIG.userService}/ratelimit?userId=${userId}`);
    }
    
    // Scheduler Service Methods
    async getQueue() {
        return await this.request(`${API_CONFIG.schedulerService}/scheduler/queue`);
    }
    
    async addToQueue(incidentData) {
        return await this.request(`${API_CONFIG.schedulerService}/scheduler/queue`, {
            method: 'POST',
            body: JSON.stringify(incidentData)
        });
    }
    
    async processQueue() {
        return await this.request(`${API_CONFIG.schedulerService}/scheduler/process`, {
            method: 'POST'
        });
    }
}

// Create global instance
const apiClient = new APIClient();













