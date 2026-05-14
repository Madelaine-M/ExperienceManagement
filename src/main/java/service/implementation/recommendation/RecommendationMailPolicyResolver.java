package service.implementation.recommendation;

import java.util.List;

//AI used
public class RecommendationMailPolicyResolver {
    private final List<RecommendationMailPolicy> policies;

    public RecommendationMailPolicyResolver() {
        this(List.of(
                new OnboardingTeamMailPolicy(),
                new OnboardingCustomerMailPolicy(),
                new ShortDelayTeamMailPolicy(),
                new CustomerRecoveryMailPolicy()
        ));
    }

    RecommendationMailPolicyResolver(List<RecommendationMailPolicy> policies) {
        this.policies = policies;
    }

    public RecommendationMailPolicy resolve(RecommendationMailContext context) {
        return policies.stream()
                .filter(policy -> policy.supports(context))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No recommendation mail policy available."));
    }
}
