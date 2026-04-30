package simulation.validation;

import simulation.model.SimulationConfig;

public class DefaultSimulationConfigValidator implements SimulationConfigValidator {
    private static final int MAX_FEEDBACK_SCORE = 10;

    @Override
    public void validate(SimulationConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Simulation config must not be null.");
        }
        if (config.getCustomerCreationIntervalSeconds() <= 0) {
            throw new IllegalArgumentException("Customer creation interval must be greater than 0.");
        }
        if (config.getJourneyAdvanceIntervalSeconds() <= 0) {
            throw new IllegalArgumentException("Journey advance interval must be greater than 0.");
        }
        validateProbability(config.getPreFlightDelayProbability(), "Pre-flight delay probability");
        validateProbability(config.getLowScoreFeedbackProbability(), "Low-score feedback probability");
        if (config.getMinDelayMinutes() <= 0) {
            throw new IllegalArgumentException("Minimum delay minutes must be greater than 0.");
        }
        if (config.getMaxDelayMinutes() < config.getMinDelayMinutes()) {
            throw new IllegalArgumentException("Maximum delay minutes must be greater than or equal to minimum delay minutes.");
        }
        if (config.getLowScoreThreshold() < 0 || config.getLowScoreThreshold() > MAX_FEEDBACK_SCORE) {
            throw new IllegalArgumentException("Low-score threshold must be between 0 and " + MAX_FEEDBACK_SCORE + ".");
        }
    }

    private void validateProbability(double probability, String label) {
        if (probability < 0.0 || probability > 1.0) {
            throw new IllegalArgumentException(label + " must be between 0.0 and 1.0.");
        }
    }
}
