package service.implementation.recommendation;

public class CustomerRecoveryMailPolicy implements RecommendationMailPolicy {
    @Override
    public boolean supports(RecommendationMailContext context) {
        return true;
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
        return "Support regarding your trip, " + context.customer().getFirstName();
    }

    @Override
    public String body(RecommendationMailContext context, String recipientName) {
        return "Dear " + context.customerName() + ",\n\n"
                + "I am sorry for the inconvenience regarding "
                + RecommendationMailTextSupport.normalizeIncidentDescription(context.incident().getDescription()) + ".\n"
                + "To support you, I would like to offer the following next step:\n"
                + context.selectedSuggestion() + "\n\n"
                + "If you have any questions, please reply and I will help you directly.\n\n"
                + "Best regards,\n"
                + context.advisorName() + "\n"
                + "Experience Management Advisor";
    }
}
