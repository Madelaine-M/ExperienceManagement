package database.initialization;

import model.domain.ActionItem;
import model.domain.Advisor;
import model.domain.Customer;
import model.domain.CustomerCvProfile;
import model.domain.CustomerNote;
import model.domain.DelayIncident;
import model.domain.Feedback;
import model.domain.FeedbackIncident;
import model.domain.FeedbackItem;
import model.domain.Flight;
import model.domain.Incident;
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
import repository.interfaces.CustomerCvProfileUpdate;
import repository.interfaces.CustomerLookup;
import repository.interfaces.CustomerNoteUpdate;
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
            CustomerCvProfileUpdate customerCvProfileUpdate,
            CustomerNoteUpdate customerNoteUpdate,
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

        Advisor advisorOne = createAdvisor("Anna", "Schmidt", "anna.schmidt@test.de", "Premium Support", 43, advisorRepo);
        Advisor advisorTwo = createAdvisor("Lukas", "Weber", "lukas.weber@test.de", "Flight Recovery", 27, advisorRepo);

        Customer customerOne = createCustomer(
                "Max", "Mustermann", "max@test.de", "1989-04-17",
                true, advisorOne.getId(),
                "Quiet cabin", customerUpdate
        );
        Customer customerTwo = createCustomer(
                "Erika", "Musterfrau", "erika@web.de", "1993-09-02",
                false, advisorTwo.getId(),
                "Aisle seat", customerUpdate
        );
        createCustomerCvProfile(customerOne.getId(), false, true, false, PaymentMethod.IMMEDIATE, false,
                CustomerType.SCIENTIST, customerCvProfileUpdate);
        createCustomerCvProfile(customerTwo.getId(), false, true, false, PaymentMethod.MONTHS, false,
                CustomerType.NORMAL, customerCvProfileUpdate);

        createCustomerNote(
                customerOne.getId(),
                advisorOne.getId(),
                "Customer prefers proactive status updates before departure.",
                customerNoteUpdate
        );
        createCustomerNote(
                customerTwo.getId(),
                advisorTwo.getId(),
                "Customer is sensitive to transfer disruptions and wants early communication.",
                customerNoteUpdate
        );

        Flight customerOnePreviousFlight = createFlight(customerOne.getId(), "EM1001", "2025-10-10", "2025-11-03", Packages.GOLD, "COMPLETED", false, flightRepository);
        Flight customerOneCurrentFlight = createFlight(customerOne.getId(), "EM2001", "2026-05-20", "2026-05-22", Packages.VIP, "BOOKED", true, flightRepository);
        Flight customerTwoCurrentFlight = createFlight(customerTwo.getId(), "EM2104", "2026-06-11", "2026-06-14", Packages.STANDARD, "BOOKED", true, flightRepository);

        Feedback feedbackOne = createFeedback(
                customerOne.getId(),
                customerOnePreviousFlight.getId(),
                LocalDateTime.now().minusMonths(3),
                4,
                3,
                List.of(
                        feedbackItem(FeedbackCategory.FLIGHT, 9, "Smooth boarding and attentive crew."),
                        feedbackItem(FeedbackCategory.FOOD, 3, "In-flight meal quality was disappointing.")
                ),
                feedbackUpdate
        );
        Feedback feedbackTwo = createFeedback(
                customerTwo.getId(),
                customerTwoCurrentFlight.getId(),
                LocalDateTime.now().minusDays(2),
                5,
                4,
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
                feedbackOne.getId(),
                FeedbackCategory.FOOD,
                "VIP passenger reported disappointing in-flight meal quality.",
                9,
                1,
                12000,
                IncidentStatus.OPEN,
                advisorOne.getId(),
                null,
                incidentUpdate
        );
        Incident secondIncident = createIncident(
                customerTwo.getId(),
                customerTwoCurrentFlight.getId(),
                IncidentType.DELAY,
                null,
                null,
                "Customer reported missed assistance during a schedule change.",
                9,
                2,
                18500,
                IncidentStatus.OPEN,
                advisorTwo.getId(),
                95,
                incidentUpdate
        );

        createAction(
                firstIncident.getId(),
                "Offer a personalized meal replacement or special dining arrangement before departure.",
                "Provide a goodwill onboard dining credit as part of the recovery plan.",
                "Flag the customer's meal preferences for the next flight and confirm them in advance.",
                8,
                1,
                15,
                22,
                actionUpdate
        );
        createAction(
                secondIncident.getId(),
                "Proactively confirm the revised transfer and boarding timeline.",
                "Provide a goodwill service credit for the disruption.",
                "Escalate future schedule changes to the assigned advisor immediately.",
                10,
                2,
                28,
                35,
                actionUpdate
        );
    }

    private static Advisor createAdvisor(String firstName, String lastName, String email, String speciality,
                                         int workloadScore, AdvisorRepository advisorRepo) {
        Advisor advisor = new Advisor(firstName, lastName, email, speciality, workloadScore);
        advisorRepo.save(advisor);
        return advisor;
    }

    private static Customer createCustomer(String firstName, String lastName, String email, String birthDate,
                                           boolean returning, Integer advisorId,
                                           String preferences, CustomerUpdate customerUpdate) {
        Customer customer = new Customer(
                firstName,
                lastName,
                email,
                birthDate,
                CustomerStatus.BOOKED,
                returning,
                advisorId,
                preferences,
                "Priority follow-up"
        );
        customerUpdate.save(customer);
        return customer;
    }

    private static CustomerCvProfile createCustomerCvProfile(int customerId, boolean marketingPurpose,
                                                             boolean newsletterSubscription, boolean referralCode,
                                                             PaymentMethod paymentMethod, boolean publicPerson,
                                                             CustomerType customerType,
                                                             CustomerCvProfileUpdate customerCvProfileUpdate) {
        CustomerCvProfile profile = new CustomerCvProfile(
                customerId,
                marketingPurpose,
                newsletterSubscription,
                referralCode,
                paymentMethod,
                publicPerson,
                customerType
        );
        customerCvProfileUpdate.save(profile);
        return profile;
    }

    private static CustomerNote createCustomerNote(int customerId, int advisorId, String noteText,
                                                   CustomerNoteUpdate customerNoteUpdate) {
        LocalDateTime now = LocalDateTime.now();
        CustomerNote customerNote = new CustomerNote(customerId, advisorId, noteText, now, now);
        customerNoteUpdate.save(customerNote);
        return customerNote;
    }

    private static Flight createFlight(int customerId, String flightNumber, String bookingDate, String flightDate,
                                       Packages bookingPackage, String status, boolean current,
                                       FlightRepository flightRepository) {
        Flight flight = new Flight(customerId, flightNumber, bookingDate, flightDate, bookingPackage, status, current);
        flightRepository.save(flight);
        return flight;
    }

    private static Feedback createFeedback(int customerId, int flightId, LocalDateTime createdAt,
                                           int customerSatScore, int referralScore, List<FeedbackItem> items,
                                           FeedbackUpdate feedbackUpdate) {
        Feedback feedback = new Feedback(customerId, createdAt, items, 0, customerSatScore, referralScore, flightId);
        feedback.setTotalScore(feedback.getOverallScore());
        feedbackUpdate.save(feedback);
        return feedback;
    }

    private static FeedbackItem feedbackItem(FeedbackCategory category, int score, String comment) {
        return new FeedbackItem(category, score, comment);
    }

    private static Incident createIncident(int customerId, int flightId, IncidentType type, Integer feedbackId,
                                           FeedbackCategory feedbackCategory, String description,
                                           double priorityScore, int scoreImpact, int revenueRisk,
                                           IncidentStatus status, Integer advisorId, Integer delayMinutes,
                                           IncidentUpdate incidentUpdate) {
        Incident incident;
        if (type == IncidentType.FEEDBACK) {
            incident = new FeedbackIncident(
                    customerId,
                    description,
                    scoreImpact,
                    revenueRisk,
                    status,
                    advisorId,
                    feedbackId,
                    feedbackCategory,
                    null,
                    null,
                    flightId,
                    null
            );
        } else if (type == IncidentType.DELAY) {
            incident = new DelayIncident(
                    customerId,
                    description,
                    scoreImpact,
                    revenueRisk,
                    status,
                    advisorId,
                    delayMinutes,
                    null,
                    flightId,
                    null
            );
        } else {
            throw new IllegalArgumentException("Unsupported incident type for seed data: " + type);
        }
        incidentUpdate.save(incident);
        return incident;
    }

    private static ActionItem createAction(int incidentId, String description, String suggestionOne,
                                           String suggestionTwo, int priority, int scoreImpact,
                                           int expectedRec, int expectedRebooking,
                                           ActionUpdate actionUpdate) {
        ActionItem actionItem = new ActionItem(
                incidentId,
                description,
                suggestionOne,
                suggestionTwo,
                ActionStatus.SUGGESTED,
                scoreImpact,
                expectedRec,
                expectedRebooking
        );
        actionUpdate.save(actionItem);
        return actionItem;
    }
}
