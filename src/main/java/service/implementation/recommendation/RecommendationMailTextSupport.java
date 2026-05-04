package service.implementation.recommendation;

final class RecommendationMailTextSupport {
    private RecommendationMailTextSupport() {
    }

    static String normalizeIncidentDescription(String incidentDescription) {
        if (incidentDescription == null || incidentDescription.isBlank()) {
            return "your recent experience";
        }
        return incidentDescription.trim();
    }

    static String normalizedSuggestion(RecommendationMailContext context) {
        return context.selectedSuggestion() == null ? "" : context.selectedSuggestion().toLowerCase();
    }
}
