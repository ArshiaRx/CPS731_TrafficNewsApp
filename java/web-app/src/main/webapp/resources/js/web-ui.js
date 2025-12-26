/**
 * WebUI (C01)
 * Presentation Layer - Main user interface component
 * Adapted to work with Java Servlet microservices via API client
 */
class WebUI {
    constructor() {
        // Services
        this.filterService = new FilterService();
        this.searchService = new SearchService();
        this.mapViewManager = new MapViewManager();
        
        // State
        this.currentIncidents = [];
        this.displayedIncidents = [];
        this.currentSort = { field: 'time', order: 'desc' };
        this.currentUser = 'user_' + Date.now();
        this.refreshInterval = 30000; // 30 seconds
        this.refreshTimer = null;
    }

    /**
     * Initialize UI
     */
    async initialize() {
        // Setup event listeners
        this.setupEventListeners();
        
        // Initialize map
        await this.mapViewManager.initializeMap('map-container');
        
        // Load initial data
        await this.loadIncidents();
        
        // Start auto-refresh
        this.startAutoRefresh();
        
        // Check offline queue
        await this.checkOfflineQueue();
    }

    /**
     * Setup event listeners
     */
    setupEventListeners() {
        // Search
        const searchInput = document.getElementById('search-input');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => this.handleSearch(e.target.value));
        }

        // Filters
        const typeFilter = document.getElementById('type-filter');
        const severityFilter = document.getElementById('severity-filter');
        if (typeFilter) {
            typeFilter.addEventListener('change', (e) => this.handleFilter('type', e.target.value));
        }
        if (severityFilter) {
            severityFilter.addEventListener('change', (e) => this.handleFilter('severity', e.target.value));
        }

        // Sort
        const sortSelect = document.getElementById('sort-select');
        if (sortSelect) {
            sortSelect.addEventListener('change', (e) => this.handleSort(e.target.value));
        }

        // Report form
        const reportForm = document.getElementById('report-form');
        if (reportForm) {
            reportForm.addEventListener('submit', (e) => this.handleReportSubmit(e));
        }

        // Clear filters
        const clearFiltersBtn = document.getElementById('clear-filters');
        if (clearFiltersBtn) {
            clearFiltersBtn.addEventListener('click', () => this.clearFilters());
        }

        // Refresh interval
        const refreshIntervalInput = document.getElementById('refresh-interval');
        if (refreshIntervalInput) {
            refreshIntervalInput.addEventListener('change', (e) => this.handleRefreshIntervalChange(e.target.value));
        }
    }

    /**
     * Load incidents from API
     */
    async loadIncidents() {
        try {
            const filters = this.filterService.getActiveFilters();
            const sortBy = this.currentSort.field;
            const order = this.currentSort.order;
            
            this.currentIncidents = await apiClient.getAllIncidents({
                type: filters.type || '',
                severity: filters.severity || '',
                keyword: document.getElementById('search-input')?.value || '',
                sortBy: sortBy,
                order: order
            });
            
            this.displayedIncidents = this.currentIncidents;
            this.applyFiltersAndSearch();
            this.updateIncidentList();
            await this.mapViewManager.displayIncidents(this.displayedIncidents);
        } catch (error) {
            console.error('WebUI.loadIncidents error:', error);
            this.showBanner('Error loading incidents: ' + error.message, 'error');
        }
    }

    /**
     * Apply filters and search
     */
    applyFiltersAndSearch() {
        let filtered = this.filterService.filterIncidents(this.currentIncidents);
        
        const searchInput = document.getElementById('search-input');
        if (searchInput && searchInput.value.trim()) {
            filtered = this.searchService.searchIncidents(filtered, searchInput.value);
        }

        this.displayedIncidents = filtered;
    }

    /**
     * Update incident list display
     */
    updateIncidentList() {
        const listContainer = document.getElementById('incident-list');
        if (!listContainer) return;

        if (this.displayedIncidents.length === 0) {
            listContainer.innerHTML = '<p class="text-muted">No incidents found</p>';
            return;
        }

        listContainer.innerHTML = this.displayedIncidents.map(incident => 
            this.createIncidentCard(incident)
        ).join('');
    }

    /**
     * Create incident card HTML
     */
    createIncidentCard(incident) {
        const severityClass = `severity-${incident.severity}`;
        const date = new Date(incident.timestamp).toLocaleString();
        
        return `
            <div class="incident-card ${severityClass}" data-id="${incident.id}">
                <div class="incident-header">
                    <span class="incident-type">${this.capitalizeFirst(incident.type)}</span>
                    <span class="incident-severity badge bg-${this.getSeverityBadgeColor(incident.severity)}">${this.capitalizeFirst(incident.severity)}</span>
                </div>
                <div class="incident-location"><strong>Location:</strong> ${incident.location}</div>
                ${incident.description ? `<div class="incident-description">${incident.description}</div>` : ''}
                <div class="incident-footer">
                    <span class="incident-time">${date}</span>
                    <span class="incident-status">${incident.status || 'pending'}</span>
                </div>
            </div>
        `;
    }

    /**
     * Handle search
     */
    handleSearch(keyword) {
        this.applyFiltersAndSearch();
        this.updateIncidentList();
        this.mapViewManager.displayIncidents(this.displayedIncidents);
    }

    /**
     * Handle filter change
     */
    handleFilter(filterType, value) {
        this.filterService.setFilter(filterType, value || null);
        this.applyFiltersAndSearch();
        this.updateIncidentList();
        this.mapViewManager.displayIncidents(this.displayedIncidents);
    }

    /**
     * Handle sort change
     */
    handleSort(sortValue) {
        const [field, order] = sortValue.split('-');
        this.currentSort = { field, order };
        this.loadIncidents(); // Reload with new sort
    }

    /**
     * Clear all filters
     */
    clearFilters() {
        this.filterService.clearFilters();
        document.getElementById('type-filter').value = '';
        document.getElementById('severity-filter').value = '';
        document.getElementById('search-input').value = '';
        this.applyFiltersAndSearch();
        this.updateIncidentList();
        this.mapViewManager.displayIncidents(this.displayedIncidents);
    }

    /**
     * Handle report form submission
     */
    async handleReportSubmit(event) {
        event.preventDefault();
        
        const formData = new FormData(event.target);
        const location = formData.get('location');
        
        // Geocode location if coordinates not provided
        let latitude = parseFloat(formData.get('latitude')) || null;
        let longitude = parseFloat(formData.get('longitude')) || null;
        
        if (!latitude || !longitude) {
            try {
                const coords = await apiClient.geocode(location);
                if (coords) {
                    latitude = coords.lat;
                    longitude = coords.lng;
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
            reporterId: this.currentUser
        };

        try {
            // Check rate limit
            const rateLimit = await apiClient.checkRateLimit(this.currentUser);
            if (!rateLimit.canSubmit) {
                this.showBanner('Rate limit exceeded. Please wait before submitting again.', 'error');
                return;
            }

            // Create incident
            const incident = await apiClient.createIncident(incidentData);
            if (incident) {
                this.showBanner('Incident reported successfully!', 'success');
                event.target.reset();
                await this.loadIncidents();
            } else {
                this.showBanner('Failed to report incident', 'error');
            }
        } catch (error) {
            console.error('Error submitting incident:', error);
            // Try to queue for offline processing
            try {
                await apiClient.addToQueue(JSON.stringify(incidentData));
                this.showBanner('Offline: Incident queued for submission', 'info');
            } catch (queueError) {
                this.showBanner('Failed to report incident: ' + error.message, 'error');
            }
        }
    }

    /**
     * Handle refresh interval change
     */
    handleRefreshIntervalChange(interval) {
        const intervalMs = parseInt(interval) * 1000;
        if (intervalMs >= 5000) {
            this.refreshInterval = intervalMs;
            this.startAutoRefresh();
            this.showBanner(`Refresh interval set to ${interval} seconds`, 'info');
        }
    }

    /**
     * Start auto-refresh
     */
    startAutoRefresh() {
        if (this.refreshTimer) {
            clearInterval(this.refreshTimer);
        }
        this.refreshTimer = setInterval(() => {
            this.loadIncidents();
        }, this.refreshInterval);
    }

    /**
     * Check offline queue status
     */
    async checkOfflineQueue() {
        try {
            const queue = await apiClient.getQueue();
            if (queue && queue.length > 0) {
                const statusEl = document.getElementById('offline-queue-status');
                const countEl = document.getElementById('queue-count');
                if (statusEl && countEl) {
                    statusEl.style.display = 'block';
                    countEl.textContent = queue.length;
                }
            }
        } catch (error) {
            console.warn('Could not check offline queue:', error);
        }
    }

    /**
     * Show banner notification
     */
    showBanner(message, type = 'info', duration = 3000) {
        const container = document.getElementById('notification-container');
        if (!container) return;

        const banner = document.createElement('div');
        banner.className = `alert alert-${type === 'error' ? 'danger' : type} alert-dismissible fade show`;
        banner.style.position = 'fixed';
        banner.style.top = '20px';
        banner.style.right = '20px';
        banner.style.zIndex = '9999';
        banner.style.minWidth = '300px';
        banner.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        
        container.appendChild(banner);
        
        setTimeout(() => {
            banner.remove();
        }, duration);
    }

    /**
     * Capitalize first letter
     */
    capitalizeFirst(str) {
        if (!str) return '';
        return str.charAt(0).toUpperCase() + str.slice(1);
    }

    /**
     * Get severity badge color for Bootstrap
     */
    getSeverityBadgeColor(severity) {
        const colors = {
            'low': 'success',
            'medium': 'warning',
            'high': 'danger',
            'critical': 'danger'
        };
        return colors[severity] || 'secondary';
    }
}

// Create global instance
const webUI = new WebUI();











