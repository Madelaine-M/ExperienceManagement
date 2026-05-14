package service.implementation.recommendation;

import model.enums.IncidentType;

public class OnboardingTeamMailPolicy implements RecommendationMailPolicy {

    // AI used
    @Override
    public boolean supports(RecommendationMailContext context) {
        String suggestion = RecommendationMailTextSupport.normalizedSuggestion(context);
        return context.incident().getType() == IncidentType.ONBOARDING
                && (suggestion.contains("onboarding team") || suggestion.contains("review"));
    }

    @Override
    public boolean isInternalTeamMail() {
        return true;
    }

    @Override
    public String recipientName(RecommendationMailContext context) {
        return "Onboarding Team";
    }

    @Override
    public String subject(RecommendationMailContext context) {
        return "Onboarding support review needed";
    }

    @Override
    public String body(RecommendationMailContext context, String recipientName) {
        return "Hello " + recipientName + ",\n\n"
                + context.customerName() + " appears to be having trouble with the onboarding process.\n"
                + "Please review the customer case and provide targeted support where needed.\n\n"
                + "Suggested action:\n"
                + context.selectedSuggestion() + "\n\n"
                + "Incident context: " + RecommendationMailTextSupport.normalizeIncidentDescription(context.incident().getDescription()) + "\n\n"
                + "Please document the follow-up once this has been handled.\n\n"
                + "Best regards,\n"
                + context.advisorName() + "\n"
                + "Experience Management Advisor";
    }
}
