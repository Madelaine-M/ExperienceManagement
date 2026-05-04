package simulation.model;

public class SimulationMetrics {
    private final int createdCustomers;
    private final int advancedJourneys;
    private final int generatedDelayIncidents;
    private final int generatedOnboardingIncidents;
    private final int generatedFeedbacks;
    private final int generatedFeedbackIncidents;

    public SimulationMetrics(int createdCustomers,
                             int advancedJourneys,
                             int generatedDelayIncidents,
                             int generatedOnboardingIncidents,
                             int generatedFeedbacks,
                             int generatedFeedbackIncidents) {
        this.createdCustomers = createdCustomers;
        this.advancedJourneys = advancedJourneys;
        this.generatedDelayIncidents = generatedDelayIncidents;
        this.generatedOnboardingIncidents = generatedOnboardingIncidents;
        this.generatedFeedbacks = generatedFeedbacks;
        this.generatedFeedbackIncidents = generatedFeedbackIncidents;
    }

    public int getCreatedCustomers() {
        return createdCustomers;
    }

    public int getAdvancedJourneys() {
        return advancedJourneys;
    }

    public int getGeneratedDelayIncidents() {
        return generatedDelayIncidents;
    }

    public int getGeneratedOnboardingIncidents() {
        return generatedOnboardingIncidents;
    }

    public int getGeneratedFeedbacks() {
        return generatedFeedbacks;
    }

    public int getGeneratedFeedbackIncidents() {
        return generatedFeedbackIncidents;
    }
}
