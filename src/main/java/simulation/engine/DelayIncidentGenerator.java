package simulation.engine;

import service.interfaces.internal.CreateDelayIncidentService;
import service.interfaces.internal.CreateSuggestedActionService;
import simulation.model.SimulationConfig;

import java.util.Random;

class DelayIncidentGenerator {
    private final CreateDelayIncidentService createDelayIncidentService;
    private final CreateSuggestedActionService createSuggestedActionService;
    private final Random random;

    DelayIncidentGenerator(CreateDelayIncidentService createDelayIncidentService,
                           CreateSuggestedActionService createSuggestedActionService,
                           Random random) {
        this.createDelayIncidentService = createDelayIncidentService;
        this.createSuggestedActionService = createSuggestedActionService;
        this.random = random;
    }

    boolean tryGenerate(int customerId, SimulationConfig config) {
        if (random.nextDouble() >= config.getPreFlightDelayProbability()) {
            return false;
        }

        int delayMinutes = randomDelayMinutes(config);
        var createdIncident = createDelayIncidentService.createDelayIncident(customerId, delayMinutes, null);
        if (createdIncident == null) {
            return false;
        }

        createSuggestedActionService.createSuggestedAction(createdIncident);
        return true;
    }

    private int randomDelayMinutes(SimulationConfig config) {
        int range = config.getMaxDelayMinutes() - config.getMinDelayMinutes() + 1;
        return config.getMinDelayMinutes() + random.nextInt(range);
    }
}
