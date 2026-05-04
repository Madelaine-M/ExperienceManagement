package simulation.engine;

import model.domain.Customer;
import model.domain.CustomerCvProfile;
import model.domain.Feedback;
import model.domain.FeedbackItem;
import model.domain.Flight;
import model.enums.CustomerStatus;
import model.enums.CustomerType;
import model.enums.FeedbackCategory;
import model.enums.Packages;
import model.enums.PaymentMethod;
import repository.interfaces.CustomerLookup;
import repository.interfaces.CustomerCvProfileUpdate;
import repository.interfaces.CustomerUpdate;
import repository.interfaces.FeedbackUpdate;
import repository.interfaces.FlightRepository;
import service.interfaces.internal.AdvisorAssignmentService;
import simulation.model.SimulationConfig;
import simulation.model.SimulationSnapshot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

class CustomerCreationProcessor {
    private static final String[] FIRST_NAMES = {"Alex", "Taylor", "Jordan", "Casey", "Morgan", "Sam", "Jamie", "Robin"};
    private static final String[] LAST_NAMES = {"Meyer", "Fischer", "Wagner", "Becker", "Hoffmann", "Keller", "Hartmann", "Schulz"};
    private static final double PREVIOUS_FLIGHT_PROBABILITY = 0.55;
    private static final double PREVIOUS_FEEDBACK_PROBABILITY = 0.7;

    private final CustomerUpdate customerUpdate;
    private final CustomerLookup customerLookup;
    private final CustomerCvProfileUpdate customerCvProfileUpdate;
    private final FlightRepository flightRepository;
    private final FeedbackUpdate feedbackUpdate;
    private final AdvisorAssignmentService advisorAssignmentService;
    private final Random random;

    CustomerCreationProcessor(CustomerUpdate customerUpdate,
                              CustomerLookup customerLookup,
                              CustomerCvProfileUpdate customerCvProfileUpdate,
                              FlightRepository flightRepository,
                              FeedbackUpdate feedbackUpdate,
                              AdvisorAssignmentService advisorAssignmentService,
                              Random random) {
        this.customerUpdate = customerUpdate;
        this.customerLookup = customerLookup;
        this.customerCvProfileUpdate = customerCvProfileUpdate;
        this.flightRepository = flightRepository;
        this.feedbackUpdate = feedbackUpdate;
        this.advisorAssignmentService = advisorAssignmentService;
        this.random = random;
    }

    int processDueCustomers(SimulationSnapshot snapshot,
                            SimulationRuntimeState runtimeState,
                            LocalDateTime now) {
        SimulationConfig config = snapshot.getConfig();
        if (config == null) {
            return 0;
        }

        int dueCustomers = countDueSteps(
                runtimeState.getLastCustomerCreationAt(),
                snapshot.getStartedAt(),
                now,
                config.getCustomerCreationIntervalSeconds()
        );

        if (dueCustomers <= 0) {
            initializeTimestampIfMissing(runtimeState, snapshot.getStartedAt(), now);
            return 0;
        }

        for (int index = 0; index < dueCustomers; index++) {
            createCustomer(config, runtimeState);
        }

        runtimeState.setLastCustomerCreationAt(
                advanceTimestamp(runtimeState.getLastCustomerCreationAt(), snapshot.getStartedAt(), dueCustomers, config.getCustomerCreationIntervalSeconds(), now)
        );
        return dueCustomers;
    }

    private void createCustomer(SimulationConfig config, SimulationRuntimeState runtimeState) {
        int sequence = nextAvailableCustomerSequence(runtimeState);
        int assignedAdvisorId = advisorAssignmentService.assignAdvisorIdForNewCustomer();
        String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
        String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)] + sequence;
        String email = ("sim.customer." + sequence + "@example.com").toLowerCase();

        Customer customer = new Customer(
                firstName,
                lastName,
                email,
                randomBirthDate(),
                CustomerStatus.INTERESTED,
                random.nextBoolean(),
                assignedAdvisorId,
                randomPreference(),
                "Simulation follow-up"
        );
        customerUpdate.save(customer);

        customerCvProfileUpdate.save(randomProfile(customer.getId()));
        createPreviousFlights(customer.getId(), sequence, customer.isReturning());
        flightRepository.save(createCurrentFlight(customer.getId(), sequence));
    }

    private int nextAvailableCustomerSequence(SimulationRuntimeState runtimeState) {
        int sequence;
        do {
            sequence = runtimeState.nextCustomerSequence();
        } while (simulationEmailExists(sequence));
        return sequence;
    }

    private boolean simulationEmailExists(int sequence) {
        String email = ("sim.customer." + sequence + "@example.com").toLowerCase();
        for (Customer customer : customerLookup.findAll()) {
            if (email.equalsIgnoreCase(customer.getEmail())) {
                return true;
            }
        }
        return false;
    }

    private void createPreviousFlights(int customerId, int sequence, boolean returningCustomer) {
        if (!returningCustomer || random.nextDouble() > PREVIOUS_FLIGHT_PROBABILITY) {
            return;
        }

        int previousFlightCount = 1 + random.nextInt(2);
        LocalDate baseDate = LocalDate.now().minusDays(30 + random.nextInt(180));

        for (int index = 0; index < previousFlightCount; index++) {
            LocalDate bookingDate = baseDate.minusDays(14 + random.nextInt(45));
            LocalDate flightDate = baseDate.plusDays(index * (20 + random.nextInt(40)));
            Flight previousFlight = new Flight(
                    customerId,
                    "SIM-PREV-" + String.format("%04d", sequence) + "-" + (index + 1),
                    bookingDate.toString(),
                    flightDate.toString(),
                    randomEnum(Packages.values()),
                    "COMPLETED",
                    false
            );
            flightRepository.save(previousFlight);
            createHistoricalFeedbackIfNeeded(customerId, previousFlight);
        }
    }

    private void createHistoricalFeedbackIfNeeded(int customerId, Flight previousFlight) {
        if (random.nextDouble() > PREVIOUS_FEEDBACK_PROBABILITY) {
            return;
        }

        List<FeedbackItem> items = new ArrayList<>();
        List<FeedbackCategory> categories = Arrays.asList(
                FeedbackCategory.FLIGHT,
                FeedbackCategory.FOOD,
                FeedbackCategory.HOTEL
        );

        for (FeedbackCategory category : categories) {
            int score = 5 + random.nextInt(6);
            items.add(new FeedbackItem(
                    category,
                    score,
                    "Historical simulation feedback for " + category + "."
            ));
        }

        Feedback feedback = new Feedback(
                customerId,
                LocalDateTime.now().minusDays(15 + random.nextInt(120)),
                items,
                0,
                6 + random.nextInt(5),
                6 + random.nextInt(5),
                previousFlight.getId()
        );
        feedback.setTotalScore(feedback.getOverallScore());
        feedbackUpdate.save(feedback);
    }

    private CustomerCvProfile randomProfile(int customerId) {
        return new CustomerCvProfile(
                customerId,
                random.nextBoolean(),
                random.nextBoolean(),
                random.nextBoolean(),
                randomEnum(PaymentMethod.values()),
                random.nextDouble() < 0.1,
                randomEnum(CustomerType.values())
        );
    }

    private Flight createCurrentFlight(int customerId, int sequence) {
        LocalDate today = LocalDate.now();
        Packages bookingPackage = randomEnum(Packages.values());
        return new Flight(
                customerId,
                "SIM-" + String.format("%04d", sequence),
                today.toString(),
                today.plusDays(7 + random.nextInt(60)).toString(),
                bookingPackage,
                "BOOKED",
                true
        );
    }

    private String randomBirthDate() {
        int year = 1970 + random.nextInt(31);
        int month = 1 + random.nextInt(12);
        int day = 1 + random.nextInt(28);
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    private String randomPreference() {
        String[] preferences = {
                "Window seat",
                "Quiet cabin",
                "Vegetarian meal",
                "Proactive updates",
                "Aisle seat"
        };
        return preferences[random.nextInt(preferences.length)];
    }

    private <T> T randomEnum(T[] values) {
        return values[random.nextInt(values.length)];
    }

    private int countDueSteps(LocalDateTime lastProcessedAt,
                              LocalDateTime startedAt,
                              LocalDateTime now,
                              int intervalSeconds) {
        LocalDateTime baseTime = lastProcessedAt != null ? lastProcessedAt : startedAt;
        if (baseTime == null) {
            return 0;
        }
        long elapsedSeconds = java.time.Duration.between(baseTime, now).getSeconds();
        if (elapsedSeconds < intervalSeconds) {
            return 0;
        }
        return (int) (elapsedSeconds / intervalSeconds);
    }

    private void initializeTimestampIfMissing(SimulationRuntimeState runtimeState, LocalDateTime startedAt, LocalDateTime now) {
        if (runtimeState.getLastCustomerCreationAt() == null) {
            runtimeState.setLastCustomerCreationAt(startedAt != null ? startedAt : now);
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
