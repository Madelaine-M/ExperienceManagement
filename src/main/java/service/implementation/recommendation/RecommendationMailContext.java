package service.implementation.recommendation;

import model.domain.Advisor;
import model.domain.Customer;
import model.domain.Incident;

public record RecommendationMailContext(
        Incident incident,
        Customer customer,
        Advisor advisor,
        String selectedSuggestion,
        String customerName,
        String advisorName
) {
}
