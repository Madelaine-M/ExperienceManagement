package simulation.model;

public class SimulationConfig {
    private final int customerCreationIntervalSeconds;
    private final int journeyAdvanceIntervalSeconds;
    private final double preFlightDelayProbability;
    private final int minDelayMinutes;
    private final int maxDelayMinutes;
    private final double lowScoreFeedbackProbability;
    private final int lowScoreThreshold;
    private final int onboardingStuckAfterSeconds;
    private final double onboardingIncidentProbability;

    public SimulationConfig(int customerCreationIntervalSeconds,
                            int journeyAdvanceIntervalSeconds,
                            double preFlightDelayProbability,
                            int minDelayMinutes,
                            int maxDelayMinutes,
                            double lowScoreFeedbackProbability,
                            int lowScoreThreshold,
                            int onboardingStuckAfterSeconds,
                            double onboardingIncidentProbability) {
        this.customerCreationIntervalSeconds = customerCreationIntervalSeconds;
        this.journeyAdvanceIntervalSeconds = journeyAdvanceIntervalSeconds;
        this.preFlightDelayProbability = preFlightDelayProbability;
        this.minDelayMinutes = minDelayMinutes;
        this.maxDelayMinutes = maxDelayMinutes;
        this.lowScoreFeedbackProbability = lowScoreFeedbackProbability;
        this.lowScoreThreshold = lowScoreThreshold;
        this.onboardingStuckAfterSeconds = onboardingStuckAfterSeconds;
        this.onboardingIncidentProbability = onboardingIncidentProbability;
    }

    public int getCustomerCreationIntervalSeconds() {
        return customerCreationIntervalSeconds;
    }

    public int getJourneyAdvanceIntervalSeconds() {
        return journeyAdvanceIntervalSeconds;
    }

    public double getPreFlightDelayProbability() {
        return preFlightDelayProbability;
    }

    public int getMinDelayMinutes() {
        return minDelayMinutes;
    }

    public int getMaxDelayMinutes() {
        return maxDelayMinutes;
    }

    public double getLowScoreFeedbackProbability() {
        return lowScoreFeedbackProbability;
    }

    public int getLowScoreThreshold() {
        return lowScoreThreshold;
    }

    public int getOnboardingStuckAfterSeconds() {
        return onboardingStuckAfterSeconds;
    }

    public double getOnboardingIncidentProbability() {
        return onboardingIncidentProbability;
    }
}
