package simulation.engine;

import model.domain.Customer;
import model.domain.Feedback;
import model.domain.FeedbackItem;
import model.domain.Flight;
import model.enums.CustomerStatus;
import model.enums.FeedbackCategory;
import repository.interfaces.CustomerLookup;
import repository.interfaces.CustomerUpdate;
import repository.interfaces.FeedbackLookup;
import repository.interfaces.FeedbackUpdate;
import repository.interfaces.FlightRepository;
import service.interfaces.internal.CreateDelayIncidentService;
import service.interfaces.internal.CreateFeedbackIncidentService;
import service.interfaces.internal.CreateSuggestedActionService;
import simulation.model.SimulationConfig;
import simulation.model.SimulationSnapshot;
import simulation.model.SimulationTickResult;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

class JourneyAdvancementProcessor {
    private final CustomerLookup customerLookup;
    private final CustomerUpdate customerUpdate;
    private final FlightRepository flightRepository;
    private final FeedbackLookup feedbackLookup;
    private final FeedbackUpdate feedbackUpdate;
    private final CreateDelayIncidentService createDelayIncidentService;
    private final CreateFeedbackIncidentService createFeedbackIncidentService;
    private final CreateSuggestedActionService createSuggestedActionService;
    private final Random random;

    JourneyAdvancementProcessor(CustomerLookup customerLookup,
                                CustomerUpdate customerUpdate,
                                FlightRepository flightRepository,
                                FeedbackLookup feedbackLookup,
                                FeedbackUpdate feedbackUpdate,
                                CreateDelayIncidentService createDelayIncidentService,
                                CreateFeedbackIncidentService createFeedbackIncidentService,
                                CreateSuggestedActionService createSuggestedActionService,
                                Random random) {
        this.customerLookup = customerLookup;
        this.customerUpdate = customerUpdate;
        this.flightRepository = flightRepository;
        this.feedbackLookup = feedbackLookup;
        this.feedbackUpdate = feedbackUpdate;
        this.createDelayIncidentService = createDelayIncidentService;
        this.createFeedbackIncidentService = createFeedbackIncidentService;
        this.createSuggestedActionService = createSuggestedActionService;
        this.random = random;
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
        int generatedFeedbacks = 0;
        int generatedFeedbackIncidents = 0;

        for (int cycle = 0; cycle < dueAdvances; cycle++) {
            List<Customer> customers = customerLookup.findAll();
            customers.sort(Comparator.comparingInt(Customer::getId));

            for (Customer customer : customers) {
                if (customer.getStatus() == CustomerStatus.COMPLETED) {
                    continue;
                }

                CustomerStatus nextStatus = nextStatus(customer.getStatus());
                if (nextStatus == customer.getStatus()) {
                    continue;
                }

                customer.setStatus(nextStatus);
                customerUpdate.update(customer);
                advancedJourneys += 1;

                if (nextStatus == CustomerStatus.PRE_FLIGHT && shouldGenerate(config.getPreFlightDelayProbability())) {
                    int delayMinutes = randomDelayMinutes(config);
                    var createdDelayIncident = createDelayIncidentService.createDelayIncident(customer.getId(), delayMinutes, null);
                    if (createdDelayIncident != null) {
                        createSuggestedActionService.createSuggestedAction(createdDelayIncident);
                        generatedDelayIncidents += 1;
                    }
                }

                if (nextStatus == CustomerStatus.FEEDBACK) {
                    Feedback feedback = generateFeedbackIfMissing(customer, config);
                    if (feedback != null) {
                        generatedFeedbacks += 1;
                        if (hasLowScoreItem(feedback, config.getLowScoreThreshold())) {
                            List<FeedbackItem> lowItems = findLowItems(feedback, config.getLowScoreThreshold());
                            var createdFeedbackIncidents = createFeedbackIncidentService.createFeedbackIncident(lowItems);
                            if (!createdFeedbackIncidents.isEmpty()) {
                                for (var incident : createdFeedbackIncidents) {
                                    createSuggestedActionService.createSuggestedAction(incident);
                                }
                                generatedFeedbackIncidents += createdFeedbackIncidents.size();
                            }
                        }
                    }
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
                generatedFeedbacks,
                generatedFeedbackIncidents,
                now
        );
    }

    private Feedback generateFeedbackIfMissing(Customer customer, SimulationConfig config) {
        Flight currentFlight = flightRepository.findCurrentByCustomerId(customer.getId());
        if (currentFlight == null) {
            return null;
        }

        if (!feedbackLookup.findAllByFlightId(currentFlight.getId()).isEmpty()) {
            return null;
        }

        boolean generateLowScore = shouldGenerate(config.getLowScoreFeedbackProbability());
        List<FeedbackItem> items = generateFeedbackItems(generateLowScore, config.getLowScoreThreshold());

        Feedback feedback = new Feedback(
                0,
                customer.getId(),
                LocalDateTime.now(),
                items,
                0.0,
                6 + random.nextInt(5),
                6 + random.nextInt(5),
                currentFlight.getId()
        );
        feedback.setTotalScore(feedback.getOverallScore());
        feedbackUpdate.save(feedback);
        return feedback;
    }

    private List<FeedbackItem> generateFeedbackItems(boolean generateLowScore, int lowScoreThreshold) {
        List<FeedbackCategory> categories = new ArrayList<>(Arrays.asList(
                FeedbackCategory.FLIGHT,
                FeedbackCategory.FOOD,
                FeedbackCategory.HOTEL
        ));

        List<FeedbackItem> items = new ArrayList<>();
        int lowScoreCategoryIndex = generateLowScore ? random.nextInt(categories.size()) : -1;

        for (int index = 0; index < categories.size(); index++) {
            FeedbackCategory category = categories.get(index);
            int score;
            if (index == lowScoreCategoryIndex) {
                score = 1 + random.nextInt(Math.max(1, lowScoreThreshold));
            } else {
                score = 4 + random.nextInt(7);
            }
            items.add(new FeedbackItem(0, 0, category, score, commentFor(category, score)));
        }

        return items;
    }

    private String commentFor(FeedbackCategory category, int score) {
        if (score <= 3) {
            return "Low score recorded for " + category + " during simulation.";
        }
        return "Positive simulation feedback for " + category + ".";
    }

    private boolean hasLowScoreItem(Feedback feedback, int threshold) {
        return feedback.getItems().stream().anyMatch(item -> item.getScore() <= threshold);
    }

    private List<FeedbackItem> findLowItems(Feedback feedback, int threshold) {
        List<FeedbackItem> lowItems = new ArrayList<>();
        for (FeedbackItem item : feedback.getItems()) {
            if (item.getScore() <= threshold) {
                lowItems.add(item);
            }
        }
        return lowItems;
    }

    private boolean shouldGenerate(double probability) {
        return random.nextDouble() < probability;
    }

    private int randomDelayMinutes(SimulationConfig config) {
        int range = config.getMaxDelayMinutes() - config.getMinDelayMinutes() + 1;
        return config.getMinDelayMinutes() + random.nextInt(range);
    }

    private CustomerStatus nextStatus(CustomerStatus status) {
        if (status == null) {
            return CustomerStatus.INTERESTED;
        }
        CustomerStatus[] values = CustomerStatus.values();
        int nextIndex = Math.min(status.ordinal() + 1, values.length - 1);
        return values[nextIndex];
    }

    private SimulationTickResult emptyTick(LocalDateTime now) {
        return new SimulationTickResult(0, 0, 0, 0, 0, now);
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
