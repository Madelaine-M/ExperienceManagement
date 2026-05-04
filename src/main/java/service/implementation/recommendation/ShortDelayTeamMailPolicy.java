package service.implementation.recommendation;

import model.domain.DelayIncident;

public class ShortDelayTeamMailPolicy implements RecommendationMailPolicy {
    @Override
    public boolean supports(RecommendationMailContext context) {
        String suggestion = RecommendationMailTextSupport.normalizedSuggestion(context);
        return context.incident() instanceof DelayIncident delayIncident
                && delayIncident.getDelayMinutes() != null
                && delayIncident.getDelayMinutes() <= 60
                && (suggestion.contains("driver")
                || suggestion.contains("detour")
                || suggestion.contains("lunch"));
    }

    @Override
    public boolean isInternalTeamMail() {
        return true;
    }

    @Override
    public String recipientName(RecommendationMailContext context) {
        String suggestion = RecommendationMailTextSupport.normalizedSuggestion(context);
        if (suggestion.contains("driver") || suggestion.contains("detour")) {
            return "Pick-up Driver / Transport Team";
        }
        if (suggestion.contains("lunch")) {
            return "Service Personnel / Catering Team";
        }
        return "Responsible Operations Team";
    }

    @Override
    public String subject(RecommendationMailContext context) {
        String suggestion = RecommendationMailTextSupport.normalizedSuggestion(context);
        if (suggestion.contains("driver") || suggestion.contains("detour")) {
            return "Operational support needed: adjust pick-up route";
        }
        if (suggestion.contains("lunch")) {
            return "Operational support needed: quick lunch preparation";
        }
        return "Operational support needed for short delay";
    }

    @Override
    public String body(RecommendationMailContext context, String recipientName) {
        return "Hello " + recipientName + ",\n\n"
                + "A short delay has been detected for " + context.customerName() + ".\n"
                + "Please take the following operational action:\n"
                + context.selectedSuggestion() + "\n\n"
                + "Incident context: " + RecommendationMailTextSupport.normalizeIncidentDescription(context.incident().getDescription()) + "\n\n"
                + "Please confirm once this has been handled.\n\n"
                + "Best regards,\n"
                + context.advisorName() + "\n"
                + "Experience Management Advisor";
    }
}
