package simulation.model;

import java.time.LocalDateTime;

public class SimulationTickResult {
    private final int createdCustomers;
    private final int advancedJourneys;
    private final int generatedDelayIncidents;
    private final int generatedFeedbacks;
    private final int generatedFeedbackIncidents;
    private final LocalDateTime processedAt;

    public SimulationTickResult(int createdCustomers,
                                int advancedJourneys,
                                int generatedDelayIncidents,
                                int generatedFeedbacks,
                                int generatedFeedbackIncidents,
                                LocalDateTime processedAt) {
        this.createdCustomers = createdCustomers;
        this.advancedJourneys = advancedJourneys;
        this.generatedDelayIncidents = generatedDelayIncidents;
        this.generatedFeedbacks = generatedFeedbacks;
        this.generatedFeedbackIncidents = generatedFeedbackIncidents;
        this.processedAt = processedAt;
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

    public int getGeneratedFeedbacks() {
        return generatedFeedbacks;
    }

    public int getGeneratedFeedbackIncidents() {
        return generatedFeedbackIncidents;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
}
