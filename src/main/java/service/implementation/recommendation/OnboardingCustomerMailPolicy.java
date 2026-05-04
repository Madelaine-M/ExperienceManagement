package service.implementation.recommendation;

import model.enums.IncidentType;

public class OnboardingCustomerMailPolicy implements RecommendationMailPolicy {
    @Override
    public boolean supports(RecommendationMailContext context) {
        return context.incident().getType() == IncidentType.ONBOARDING;
    }

    @Override
    public boolean isInternalTeamMail() {
        return false;
    }

    @Override
    public String recipientName(RecommendationMailContext context) {
        return context.customerName();
    }

    @Override
    public String subject(RecommendationMailContext context) {
        return "Support with your onboarding, " + context.customer().getFirstName();
    }

    @Override
    public String body(RecommendationMailContext context, String recipientName) {
        return "Dear " + context.customerName() + ",\n\n"
                + "I noticed that your onboarding process may be taking longer than expected.\n"
                + "If anything is unclear or if you would like additional support, please reply to this message and I will help you directly.\n\n"
                + "Suggested next step:\n"
                + context.selectedSuggestion() + "\n\n"
                + "Best regards,\n"
                + context.advisorName() + "\n"
                + "Experience Management Advisor";
    }
}
