package service.implementation;

import model.domain.ActionItem;
import model.domain.Advisor;
import model.domain.Customer;
import model.domain.CustomerNote;
import model.domain.Incident;
import repository.interfaces.AdvisorRepository;
import repository.interfaces.RecommendationResolutionStore;
import model.workflow.RecommendationEmailDraft;
import service.interfaces.frontend.ActionService;
import service.interfaces.frontend.CustomerService;
import service.interfaces.frontend.IncidentService;
import service.interfaces.frontend.RecommendationExecutionService;
import support.RecoveryActionNoteCodec;

import java.time.LocalDateTime;

public class RecommendationExecutionServiceImpl implements RecommendationExecutionService {
    private final ActionService actionService;
    private final IncidentService incidentService;
    private final CustomerService customerService;
    private final AdvisorRepository advisorRepository;
    private final RecommendationResolutionStore recommendationResolutionStore;

    public RecommendationExecutionServiceImpl(ActionService actionService,
                                              IncidentService incidentService,
                                              CustomerService customerService,
                                              AdvisorRepository advisorRepository,
                                              RecommendationResolutionStore recommendationResolutionStore) {
        this.actionService = actionService;
        this.incidentService = incidentService;
        this.customerService = customerService;
        this.advisorRepository = advisorRepository;
        this.recommendationResolutionStore = recommendationResolutionStore;
    }

    @Override
    public RecommendationEmailDraft prepareDraft(int actionId, int optionNumber) {
        ActionItem actionItem = requireActionItem(actionId);
        Incident incident = requireIncident(actionItem.getIncidentId());
        Customer customer = requireCustomer(incident.getCustomerId());
        int advisorId = resolveAdvisorId(incident, customer);
        Advisor advisor = advisorRepository.findById(advisorId);
        String selectedSuggestion = resolveSuggestion(actionItem, optionNumber);
        String customerName = buildCustomerName(customer);
        String advisorName = buildAdvisorName(advisor);

        return new RecommendationEmailDraft(
                actionItem.getId(),
                optionNumber,
                incident.getId(),
                customer.getId(),
                advisorId,
                customerName,
                advisorName,
                selectedSuggestion,
                buildSubject(customer),
                buildBody(customerName, advisorName, selectedSuggestion, incident.getDescription())
        );
    }

    @Override
    public void sendRecommendation(int actionId, int optionNumber, String subject, String emailBody) {
        String trimmedSubject = normalizeRequiredText(subject, "Email subject");
        String trimmedBody = normalizeRequiredText(emailBody, "Email body");

        ActionItem actionItem = requireActionItem(actionId);
        Incident incident = requireIncident(actionItem.getIncidentId());
        Customer customer = requireCustomer(incident.getCustomerId());
        int advisorId = resolveAdvisorId(incident, customer);
        String selectedSuggestion = resolveSuggestion(actionItem, optionNumber);

        CustomerNote note = buildRecommendationNote(
                customer.getId(),
                advisorId,
                optionNumber,
                selectedSuggestion,
                trimmedSubject,
                trimmedBody
        );
        recommendationResolutionStore.completeRecommendationResolution(incident.getId(), note);
    }

    private CustomerNote buildRecommendationNote(int customerId,
                                                 int advisorId,
                                                 int optionNumber,
                                                 String selectedSuggestion,
                                                 String subject,
                                                 String emailBody) {
        LocalDateTime now = LocalDateTime.now();
        CustomerNote note = new CustomerNote();
        note.setCustomerId(customerId);
        note.setAdvisorId(advisorId);
        note.setCreatedAt(now);
        note.setUpdatedAt(now);
        note.setNoteText(RecoveryActionNoteCodec.formatRecommendationResolution(
                optionNumber,
                selectedSuggestion,
                subject,
                emailBody
        ));
        return note;
    }

    private ActionItem requireActionItem(int actionId) {
        ActionItem actionItem = actionService.findById(actionId);
        if (actionItem == null) {
            throw new IllegalArgumentException("Action item " + actionId + " could not be found.");
        }
        return actionItem;
    }

    private Incident requireIncident(int incidentId) {
        Incident incident = incidentService.findById(incidentId);
        if (incident == null) {
            throw new IllegalArgumentException("Incident " + incidentId + " could not be found.");
        }
        return incident;
    }

    private Customer requireCustomer(int customerId) {
        Customer customer = customerService.findById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer " + customerId + " could not be found.");
        }
        return customer;
    }

    private int resolveAdvisorId(Incident incident, Customer customer) {
        if (incident.getAssignedAdvisorId() != null) {
            return incident.getAssignedAdvisorId();
        }
        if (customer.getAssignedAdvisorId() != null) {
            return customer.getAssignedAdvisorId();
        }
        throw new IllegalStateException("No advisor is assigned to customer " + customer.getId() + ".");
    }

    private String resolveSuggestion(ActionItem actionItem, int optionNumber) {
        String suggestion = optionNumber == 1 ? actionItem.getSuggestion1() : actionItem.getSuggestion2();
        if (suggestion == null || suggestion.isBlank()) {
            throw new IllegalArgumentException("Recommendation option " + optionNumber + " is not available.");
        }
        return suggestion.trim();
    }

    private String buildCustomerName(Customer customer) {
        return (customer.getFirstName() + " " + customer.getLastName()).trim();
    }

    private String buildAdvisorName(Advisor advisor) {
        if (advisor == null) {
            return "Your advisor";
        }
        return (advisor.getFirstName() + " " + advisor.getLastName()).trim();
    }

    private String buildSubject(Customer customer) {
        return "Support for your upcoming trip, " + customer.getFirstName();
    }

    private String buildBody(String customerName, String advisorName, String selectedSuggestion, String incidentDescription) {
        return "Dear " + customerName + ",\n\n"
                + "I am sorry for the inconvenience regarding " + normalizeIncidentDescription(incidentDescription) + ".\n"
                + "To support you, I would like to offer the following next step:\n"
                + selectedSuggestion + "\n\n"
                + "If you have any questions, please reply and I will help you directly.\n\n"
                + "Best regards,\n"
                + advisorName + "\n"
                + "Experience Management Advisor";
    }

    private String normalizeIncidentDescription(String incidentDescription) {
        if (incidentDescription == null || incidentDescription.isBlank()) {
            return "your recent experience";
        }
        return incidentDescription.trim();
    }

    private String normalizeRequiredText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        return value.trim();
    }
}
