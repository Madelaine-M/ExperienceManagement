package simulation.engine;

import model.domain.Customer;
import model.enums.CustomerStatus;
import repository.interfaces.CustomerLookup;
import repository.interfaces.CustomerUpdate;
import repository.interfaces.FeedbackLookup;
import repository.interfaces.FeedbackUpdate;
import repository.interfaces.FlightRepository;
import repository.interfaces.IncidentLookup;
import service.interfaces.internal.CreateDelayIncidentService;
import service.interfaces.internal.CreateFeedbackIncidentService;
import service.interfaces.internal.CreateOnboardingIncidentService;
import service.interfaces.internal.CreateSuggestedActionService;
import simulation.model.SimulationConfig;
import simulation.model.SimulationSnapshot;
import simulation.model.SimulationTickResult;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

// AI used to help with implementation
class JourneyAdvancementProcessor {
    private final CustomerLookup customerLookup;
    private final CustomerUpdate customerUpdate;
    private final OpenIncidentBlockPolicy openIncidentBlockPolicy;
    private final OnboardingIncidentGenerator onboardingIncidentGenerator;
    private final DelayIncidentGenerator delayIncidentGenerator;
    private final SimulationFeedbackGenerator feedbackGenerator;
    private final JourneyStatusTransitionPolicy transitionPolicy;

    JourneyAdvancementProcessor(CustomerLookup customerLookup,
                                CustomerUpdate customerUpdate,
                                FlightRepository flightRepository,
                                FeedbackLookup feedbackLookup,
                                FeedbackUpdate feedbackUpdate,
                                IncidentLookup incidentLookup,
                                CreateDelayIncidentService createDelayIncidentService,
                                CreateFeedbackIncidentService createFeedbackIncidentService,
                                CreateOnboardingIncidentService createOnboardingIncidentService,
                                CreateSuggestedActionService createSuggestedActionService,
                                Random random) {
        this.customerLookup = customerLookup;
        this.customerUpdate = customerUpdate;
        this.openIncidentBlockPolicy = new OpenIncidentBlockPolicy(incidentLookup);
        this.onboardingIncidentGenerator = new OnboardingIncidentGenerator(
                createOnboardingIncidentService,
                createSuggestedActionService,
                random
        );
        this.delayIncidentGenerator = new DelayIncidentGenerator(
                createDelayIncidentService,
                createSuggestedActionService,
                random
        );
        this.feedbackGenerator = new SimulationFeedbackGenerator(
                flightRepository,
                feedbackLookup,
                feedbackUpdate,
                createFeedbackIncidentService,
                createSuggestedActionService,
                random
        );
        this.transitionPolicy = new JourneyStatusTransitionPolicy();
    }

    SimulationTickResult processJourneyAdvancement(SimulationSnapshot snapshot,
                                                   SimulationRuntimeState runtimeState,
                                                   LocalDateTime now) {
        SimulationConfig config = snapshot.getConfig();
        if (config == null) {
            return emptyTick(now);
        }

        int dueAdvances = countDueSteps(
                runtimeState.getLastJourneyAdvanceAt(),
                snapshot.getStartedAt(),
                now,
                config.getJourneyAdvanceIntervalSeconds()
        );

        if (dueAdvances <= 0) {
            initializeTimestampIfMissing(runtimeState, snapshot.getStartedAt(), now);
            return emptyTick(now);
        }

        int advancedJourneys = 0;
        int generatedDelayIncidents = 0;
        int generatedOnboardingIncidents = 0;
        int generatedFeedbacks = 0;
        int generatedFeedbackIncidents = 0;

        for (int cycle = 0; cycle < dueAdvances; cycle++) {
            List<Customer> customers = customerLookup.findAll();
            customers.sort(Comparator.comparingInt(Customer::getId));

            for (Customer customer : customers) {
                if (customer.getStatus() == CustomerStatus.COMPLETED) {
                    continue;
                }

                boolean hasOpenIncident = openIncidentBlockPolicy.hasOpenIncident(customer.getId());
                boolean onboardingStuck = onboardingIncidentGenerator.isStuck(customer, config, now);
                if (!hasOpenIncident && onboardingStuck && onboardingIncidentGenerator.tryGenerate(customer, config)) {
                    generatedOnboardingIncidents += 1;
                    hasOpenIncident = true;
                }

                if (hasOpenIncident) {
                    continue;
                }

                if (customer.getStatus() == CustomerStatus.ONBOARDING && !onboardingStuck) {
                    continue;
                }

                CustomerStatus nextStatus = transitionPolicy.nextStatus(customer.getStatus());
                if (nextStatus == customer.getStatus()) {
                    continue;
                }

                customer.setStatus(nextStatus);
                customer.setStatusUpdatedAt(now);
                customerUpdate.update(customer);
                advancedJourneys += 1;

                if (nextStatus == CustomerStatus.PRE_FLIGHT && delayIncidentGenerator.tryGenerate(customer.getId(), config)) {
                    generatedDelayIncidents += 1;
                }

                if (nextStatus == CustomerStatus.FEEDBACK) {
                    FeedbackGenerationResult feedbackResult = feedbackGenerator.generateIfMissing(customer, config);
                    generatedFeedbacks += feedbackResult.generatedFeedbacks();
                    generatedFeedbackIncidents += feedbackResult.generatedFeedbackIncidents();
                }
            }
        }

        runtimeState.setLastJourneyAdvanceAt(
                advanceTimestamp(runtimeState.getLastJourneyAdvanceAt(), snapshot.getStartedAt(), dueAdvances, config.getJourneyAdvanceIntervalSeconds(), now)
        );

        return new SimulationTickResult(
                0,
                advancedJourneys,
                generatedDelayIncidents,
                generatedOnboardingIncidents,
                generatedFeedbacks,
                generatedFeedbackIncidents,
                now
        );
    }

    private SimulationTickResult emptyTick(LocalDateTime now) {
        return new SimulationTickResult(0, 0, 0, 0, 0, 0, now);
    }

    private int countDueSteps(LocalDateTime lastProcessedAt,
                              LocalDateTime startedAt,
                              LocalDateTime now,
                              int intervalSeconds) {
        LocalDateTime baseTime = lastProcessedAt != null ? lastProcessedAt : startedAt;
        if (baseTime == null) {
            return 0;
        }
        long elapsedSeconds = Duration.between(baseTime, now).getSeconds();
        if (elapsedSeconds < intervalSeconds) {
            return 0;
        }
        return (int) (elapsedSeconds / intervalSeconds);
    }

    private void initializeTimestampIfMissing(SimulationRuntimeState runtimeState, LocalDateTime startedAt, LocalDateTime now) {
        if (runtimeState.getLastJourneyAdvanceAt() == null) {
            runtimeState.setLastJourneyAdvanceAt(startedAt != null ? startedAt : now);
        }
    }

    private LocalDateTime advanceTimestamp(LocalDateTime lastProcessedAt,
                                           LocalDateTime startedAt,
                                           int dueSteps,
                                           int intervalSeconds,
                                           LocalDateTime fallbackNow) {
        LocalDateTime baseTime = lastProcessedAt != null ? lastProcessedAt : startedAt;
        if (baseTime == null) {
            return fallbackNow;
        }
        return baseTime.plusSeconds((long) dueSteps * intervalSeconds);
    }
}
