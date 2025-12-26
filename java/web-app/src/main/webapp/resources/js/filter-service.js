/**
 * FilterService (C04)
 * Business Logic Layer - Filtering logic (client-side)
 */
class FilterService {
    constructor() {
        this.activeFilters = {
            type: null,
            severity: null,
            status: null
        };
    }

    /**
     * Apply filters to incidents
     * @param {Array<Incident>} incidents - Incidents to filter
     * @param {Object} filters - Filter criteria
     * @returns {Array<Incident>} Filtered incidents
     */
    filterIncidents(incidents, filters = {}) {
        this.activeFilters = { ...this.activeFilters, ...filters };
        
        return incidents.filter(incident => {
            // Type filter
            if (this.activeFilters.type && incident.type !== this.activeFilters.type) {
                return false;
            }

            // Severity filter
            if (this.activeFilters.severity && incident.severity !== this.activeFilters.severity) {
                return false;
            }

            // Status filter
            if (this.activeFilters.status && incident.status !== this.activeFilters.status) {
                return false;
            }

            return true;
        });
    }

    /**
     * Clear all filters
     */
    clearFilters() {
        this.activeFilters = {
            type: null,
            severity: null,
            status: null
        };
    }

    /**
     * Get active filters
     * @returns {Object} Active filter state
     */
    getActiveFilters() {
        return { ...this.activeFilters };
    }

    /**
     * Set filter
     * @param {string} filterType - Filter type ('type', 'severity', 'status')
     * @param {string} value - Filter value
     */
    setFilter(filterType, value) {
        if (['type', 'severity', 'status'].includes(filterType)) {
            this.activeFilters[filterType] = value || null;
        }
    }
}












