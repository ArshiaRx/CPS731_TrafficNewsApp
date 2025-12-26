/**
 * SearchService (C05)
 * Business Logic Layer - Search functionality (client-side)
 */
class SearchService {
    constructor() {
        this.searchHistory = [];
    }

    /**
     * Search incidents by keyword
     * @param {Array<Incident>} incidents - Incidents to search
     * @param {string} keyword - Search keyword
     * @returns {Array<Incident>} Matching incidents
     */
    searchIncidents(incidents, keyword) {
        if (!keyword || keyword.trim().length === 0) {
            return incidents;
        }

        const searchTerm = keyword.toLowerCase().trim();
        this.addToHistory(searchTerm);

        return incidents.filter(incident => {
            // Search in location
            if (incident.location && incident.location.toLowerCase().includes(searchTerm)) {
                return true;
            }

            // Search in description
            if (incident.description && incident.description.toLowerCase().includes(searchTerm)) {
                return true;
            }

            // Search in type
            if (incident.type && incident.type.toLowerCase().includes(searchTerm)) {
                return true;
            }

            return false;
        });
    }

    /**
     * Add search term to history
     * @param {string} term - Search term
     */
    addToHistory(term) {
        if (term && !this.searchHistory.includes(term)) {
            this.searchHistory.unshift(term);
            // Keep only last 10 searches
            if (this.searchHistory.length > 10) {
                this.searchHistory = this.searchHistory.slice(0, 10);
            }
        }
    }
}












