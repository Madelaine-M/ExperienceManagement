package database.initialization;

import model.ActionItem;
import model.Advisor;
import model.Customer;
import model.Feedback;
import model.FeedbackItem;
import model.Flight;
import model.Incident;
import model.enums.ActionStatus;
import model.enums.CustomerStatus;
import model.enums.CustomerType;
import model.enums.FeedbackCategory;
import model.enums.IncidentStatus;
import model.enums.IncidentType;
import model.enums.Packages;
import model.enums.PaymentMethod;
import repository.interfaces.ActionLookup;
import repository.interfaces.ActionUpdate;
import repository.interfaces.AdvisorRepository;
import repository.interfaces.CustomerLookup;
import repository.interfaces.CustomerUpdate;
import repository.interfaces.FeedbackUpdate;
import repository.interfaces.FlightRepository;
import repository.interfaces.IncidentLookup;
import repository.interfaces.IncidentUpdate;

import java.time.LocalDateTime;
import java.util.List;

public class DataSeeder {

    public static void seed(
            AdvisorRepository advisorRepo,
            CustomerLookup customerLookup,
            CustomerUpdate customerUpdate,
            FlightRepository flightRepository,
            FeedbackUpdate feedbackUpdate,
            IncidentLookup incidentLookup,
            IncidentUpdate incidentUpdate,
            ActionLookup actionLookup,
            ActionUpdate actionUpdate
    ) {
        if (!customerLookup.findAll().isEmpty()) {
            return;
        }

        Advisor advisorOne = createAdvisor("Anna", "Schmidt", "anna.schmidt@test.de", "Premium Support", 42.5, advisorRepo);
        Advisor advisorTwo = createAdvisor("Lukas", "Weber", "lukas.weber@test.de", "Flight Recovery", 27.0, advisorRepo);

        Customer customerOne = createCustomer(
                "Max", "Mustermann", "max@test.de", "1989-04-17",
                "2026-05-20", true, advisorOne.getId(), 84.0f, "Window seat preferred",
                "Quiet cabin", CustomerType.SCIENTIST, PaymentMethod.IMMEDIATE, customerUpdate
        );
        Customer customerTwo = createCustomer(
                "Erika", "Musterfrau", "erika@web.de", "1993-09-02",
                "2026-06-11", false, advisorTwo.getId(), 58.0f, "Vegetarian meals",
                "Aisle seat", CustomerType.NORMAL, PaymentMethod.MONTHS, customerUpdate
        );

        Flight customerOnePreviousFlight = createFlight(customerOne.getId(), "EM1001", "2025-11-03", Packages.GOLD, "COMPLETED", false, flightRepository);
        Flight customerOneCurrentFlight = createFlight(customerOne.getId(), "EM2001", "2026-05-22", Packages.VIP, "BOOKED", true, flightRepository);
        Flight customerTwoCurrentFlight = createFlight(customerTwo.getId(), "EM2104", "2026-06-14", Packages.STANDARD, "BOOKED", true, flightRepository);

        createFeedback(
                customerOne.getId(),
                customerOnePreviousFlight.getId(),
                LocalDateTime.now().minusMonths(3),
                9,
                List.of(
                        feedbackItem(FeedbackCategory.FLIGHT, 9, "Smooth boarding and attentive crew."),
                        feedbackItem(FeedbackCategory.SERVICE, 10, "Advisor follow-up was excellent.")
                ),
                feedbackUpdate
        );
        createFeedback(
                customerTwo.getId(),
                customerTwoCurrentFlight.getId(),
                LocalDateTime.now().minusDays(2),
                5,
                List.of(
                        feedbackItem(FeedbackCategory.FLIGHT, 4, "Delay communication was weak."),
                        feedbackItem(FeedbackCategory.ORGANIZATION, 6, "Transfer details arrived too late.")
                ),
                feedbackUpdate
        );

        Incident firstIncident = createIncident(
                customerOne.getId(),
                customerOneCurrentFlight.getId(),
                IncidentType.FEEDBACK,
                FeedbackCategory.SERVICE,
                "VIP passenger requested proactive concierge updates before departure.",
                8.7,
                1.4,
                12000,
                IncidentStatus.OPEN,
                advisorOne.getId(),
                incidentUpdate
        );
        Incident secondIncident = createIncident(
                customerTwo.getId(),
                customerTwoCurrentFlight.getId(),
                IncidentType.DELAY,
                FeedbackCategory.FLIGHT,
                "Customer reported missed assistance during a schedule change.",
                9.4,
                2.1,
                18500,
                IncidentStatus.OPEN,
                advisorTwo.getId(),
                incidentUpdate
        );

        createAction(
                firstIncident.getId(),
                "Schedule a same-day concierge call before boarding.",
                "Offer lounge access as part of the recovery plan.",
                "Send a tailored travel brief 24h before departure.",
                8,
                0.9,
                0.15,
                0.22,
                actionUpdate
        );
        createAction(
                secondIncident.getId(),
                "Proactively confirm the revised transfer and boarding timeline.",
                "Provide a goodwill service credit for the disruption.",
                "Escalate future schedule changes to the assigned advisor immediately.",
                10,
                1.8,
                0.28,
                0.35,
                actionUpdate
        );
    }

    private static Advisor createAdvisor(String firstName, String lastName, String email, String speciality,
                                         double workloadScore, AdvisorRepository advisorRepo) {
        Advisor advisor = new Advisor(0, firstName, lastName, email, speciality, workloadScore);
        advisorRepo.save(advisor);
        return advisor;
    }

    private static Customer createCustomer(String firstName, String lastName, String email, String birthDate,
                                           String bookingDate, boolean returning, Integer advisorId, float clvScore,
                                           String notes, String preferences, CustomerType customerType,
                                           PaymentMethod paymentMethod, CustomerUpdate customerUpdate) {
        Customer customer = new Customer(
                0,
                firstName,
                lastName,
                email,
                birthDate,
                CustomerStatus.BOOKED,
                bookingDate,
                returning,
                advisorId,
                clvScore,
                notes,
                preferences,
                "Priority follow-up",
                false,
                true,
                false,
                paymentMethod,
                false,
                customerType
        );
        customerUpdate.save(customer);
        return customer;
    }

    private static Flight createFlight(int customerId, String flightNumber, String flightDate, Packages bookingPackage,
                                       String status, boolean current, FlightRepository flightRepository) {
        Flight flight = new Flight(0, customerId, flightNumber, flightDate, bookingPackage, status, current);
        flightRepository.save(flight);
        return flight;
    }

    private static Feedback createFeedback(int customerId, int flightId, LocalDateTime createdAt,
                                           int customerSatScore, List<FeedbackItem> items,
                                           FeedbackUpdate feedbackUpdate) {
        Feedback feedback = new Feedback(0, customerId, createdAt, items, 0.0, customerSatScore, 0, flightId);
        feedback.setTotalScore(feedback.getOverallScore());
        feedbackUpdate.save(feedback);
        return feedback;
    }

    private static FeedbackItem feedbackItem(FeedbackCategory category, int score, String comment) {
        return new FeedbackItem(0, 0, category, score, comment);
    }

    private static Incident createIncident(int customerId, int flightId, IncidentType type,
                                           FeedbackCategory feedbackCategory, String description,
                                           double priorityScore, double scoreImpact, int revenueRisk,
                                           IncidentStatus status, Integer advisorId, IncidentUpdate incidentUpdate) {
        Incident incident = new Incident(
                0,
                customerId,
                type,
                feedbackCategory,
                description,
                priorityScore,
                scoreImpact,
                revenueRisk,
                status,
                advisorId,
                null,
                null,
                flightId,
                null
        );
        incidentUpdate.save(incident);
        return incident;
    }

    private static ActionItem createAction(int incidentId, String description, String suggestionOne,
                                           String suggestionTwo, int priority, double scoreImpact,
                                           double expectedRec, double expectedRebooking,
                                           ActionUpdate actionUpdate) {
        ActionItem actionItem = new ActionItem(
                0,
                incidentId,
                description,
                suggestionOne,
                suggestionTwo,
                ActionStatus.SUGGESTED,
                priority,
                scoreImpact,
                expectedRec,
                expectedRebooking
        );
        actionUpdate.save(actionItem);
        return actionItem;
    }
}
