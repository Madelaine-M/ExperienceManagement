package simulation.engine;

import simulation.model.SimulationConfig;

import java.time.LocalDateTime;

class SimulationRuntimeState {
    private SimulationConfig config;
    private LocalDateTime lastCustomerCreationAt;
    private LocalDateTime lastJourneyAdvanceAt;
    private int generatedCustomerSequence;

    boolean matches(SimulationConfig other) {
        if (config == null || other == null) {
            return false;
        }
        return config.getCustomerCreationIntervalSeconds() == other.getCustomerCreationIntervalSeconds()
                && config.getJourneyAdvanceIntervalSeconds() == other.getJourneyAdvanceIntervalSeconds()
                && Double.compare(config.getPreFlightDelayProbability(), other.getPreFlightDelayProbability()) == 0
                && config.getMinDelayMinutes() == other.getMinDelayMinutes()
                && config.getMaxDelayMinutes() == other.getMaxDelayMinutes()
                && Double.compare(config.getLowScoreFeedbackProbability(), other.getLowScoreFeedbackProbability()) == 0
                && config.getLowScoreThreshold() == other.getLowScoreThreshold();
    }

    void reset(SimulationConfig config) {
        this.config = config;
        this.lastCustomerCreationAt = null;
        this.lastJourneyAdvanceAt = null;
        this.generatedCustomerSequence = 0;
    }

    SimulationConfig getConfig() {
        return config;
    }

    LocalDateTime getLastCustomerCreationAt() {
        return lastCustomerCreationAt;
    }

    void setLastCustomerCreationAt(LocalDateTime lastCustomerCreationAt) {
        this.lastCustomerCreationAt = lastCustomerCreationAt;
    }

    LocalDateTime getLastJourneyAdvanceAt() {
        return lastJourneyAdvanceAt;
    }

    void setLastJourneyAdvanceAt(LocalDateTime lastJourneyAdvanceAt) {
        this.lastJourneyAdvanceAt = lastJourneyAdvanceAt;
    }

    int nextCustomerSequence() {
        generatedCustomerSequence += 1;
        return generatedCustomerSequence;
    }
}
