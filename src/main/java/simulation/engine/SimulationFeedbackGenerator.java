package simulation.engine;

import model.domain.Customer;
import model.domain.Feedback;
import model.domain.FeedbackItem;
import model.domain.Flight;
import model.enums.FeedbackCategory;
import repository.interfaces.FeedbackLookup;
import repository.interfaces.FeedbackUpdate;
import repository.interfaces.FlightRepository;
import service.interfaces.internal.CreateFeedbackIncidentService;
import service.interfaces.internal.CreateSuggestedActionService;
import simulation.model.SimulationConfig;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

// AI used
class SimulationFeedbackGenerator {
    private final FlightRepository flightRepository;
    private final FeedbackLookup feedbackLookup;
    private final FeedbackUpdate feedbackUpdate;
    private final CreateFeedbackIncidentService createFeedbackIncidentService;
    private final CreateSuggestedActionService createSuggestedActionService;
    private final Random random;

    SimulationFeedbackGenerator(FlightRepository flightRepository,
                                FeedbackLookup feedbackLookup,
                                FeedbackUpdate feedbackUpdate,
                                CreateFeedbackIncidentService createFeedbackIncidentService,
                                CreateSuggestedActionService createSuggestedActionService,
                                Random random) {
        this.flightRepository = flightRepository;
        this.feedbackLookup = feedbackLookup;
        this.feedbackUpdate = feedbackUpdate;
        this.createFeedbackIncidentService = createFeedbackIncidentService;
        this.createSuggestedActionService = createSuggestedActionService;
        this.random = random;
    }

    FeedbackGenerationResult generateIfMissing(Customer customer, SimulationConfig config) {
        Feedback feedback = generateFeedbackIfMissing(customer, config);
        if (feedback == null) {
            return FeedbackGenerationResult.none();
        }

        int generatedFeedbackIncidents = 0;
        if (hasLowScoreItem(feedback, config.getLowScoreThreshold())) {
            List<FeedbackItem> lowItems = findLowItems(feedback, config.getLowScoreThreshold());
            var createdIncidents = createFeedbackIncidentService.createFeedbackIncident(lowItems);
            for (var incident : createdIncidents) {
                createSuggestedActionService.createSuggestedAction(incident);
            }
            generatedFeedbackIncidents = createdIncidents.size();
        }

        return new FeedbackGenerationResult(1, generatedFeedbackIncidents);
    }

    private Feedback generateFeedbackIfMissing(Customer customer, SimulationConfig config) {
        Flight currentFlight = flightRepository.findCurrentByCustomerId(customer.getId());
        if (currentFlight == null) {
            return null;
        }

        if (!feedbackLookup.findAllByFlightId(currentFlight.getId()).isEmpty()) {
            return null;
        }

        boolean generateLowScore = random.nextDouble() < config.getLowScoreFeedbackProbability();
        List<FeedbackItem> items = generateFeedbackItems(generateLowScore, config.getLowScoreThreshold());

        Feedback feedback = new Feedback(
                customer.getId(),
                LocalDateTime.now(),
                items,
                0,
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
            items.add(new FeedbackItem(category, score, commentFor(category, score)));
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
}
