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

    //idea from AI as lower case is needed for ShortDelayTeamMailPolicy
    static String normalizedSuggestion(RecommendationMailContext context) {
        return context.selectedSuggestion() == null ? "" : context.selectedSuggestion().toLowerCase();
    }
}
