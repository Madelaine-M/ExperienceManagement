package service.implementation.recommendation;

public interface RecommendationMailPolicy {
    boolean supports(RecommendationMailContext context);

    boolean isInternalTeamMail();

    String recipientName(RecommendationMailContext context);

    String subject(RecommendationMailContext context);

    String body(RecommendationMailContext context, String recipientName);
}
