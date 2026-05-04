package simulation.engine;

import model.domain.Customer;
import model.enums.CustomerStatus;
import service.interfaces.internal.CreateOnboardingIncidentService;
import service.interfaces.internal.CreateSuggestedActionService;
import simulation.model.SimulationConfig;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

class OnboardingIncidentGenerator {
    private final CreateOnboardingIncidentService createOnboardingIncidentService;
    private final CreateSuggestedActionService createSuggestedActionService;
    private final Random random;

    OnboardingIncidentGenerator(CreateOnboardingIncidentService createOnboardingIncidentService,
                                CreateSuggestedActionService createSuggestedActionService,
                                Random random) {
        this.createOnboardingIncidentService = createOnboardingIncidentService;
        this.createSuggestedActionService = createSuggestedActionService;
        this.random = random;
    }

    boolean isStuck(Customer customer, SimulationConfig config, LocalDateTime now) {
        if (customer.getStatus() != CustomerStatus.ONBOARDING || customer.getStatusUpdatedAt() == null) {
            return false;
        }
        long secondsInStatus = Duration.between(customer.getStatusUpdatedAt(), now).getSeconds();
        return secondsInStatus >= config.getOnboardingStuckAfterSeconds();
    }

    boolean tryGenerate(Customer customer, SimulationConfig config) {
        if (random.nextDouble() >= config.getOnboardingIncidentProbability()) {
            return false;
        }

        var createdIncident = createOnboardingIncidentService.createOnboardingIncident(customer.getId());
        if (createdIncident == null) {
            return false;
        }

        createSuggestedActionService.createSuggestedAction(createdIncident);
        return true;
    }
}
