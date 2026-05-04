package service.implementation;

import model.domain.ActionItem;
import model.domain.Advisor;
import model.domain.Customer;
import model.domain.CustomerNote;
import model.domain.DelayIncident;
import model.domain.Incident;
import model.enums.IncidentType;
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
        boolean internalTeamMail = isInternalTeamMail(incident, selectedSuggestion);
        String recipientName = internalTeamMail ? resolveInternalRecipient(selectedSuggestion) : customerName;
        boolean onboardingRequiresBothMails = incident.getType() == IncidentType.ONBOARDING;
        boolean closesIncident = !onboardingRequiresBothMails || recommendationResolutionStore.hasRecommendationStep(
                incident.getId(),
                otherOptionNumber(optionNumber)
        );

        return new RecommendationEmailDraft(
                actionItem.getId(),
                optionNumber,
                incident.getId(),
                customer.getId(),
                advisorId,
                customerName,
                advisorName,
                selectedSuggestion,
                internalTeamMail ? "Team Mail" : "Recovery Mail",
                internalTeamMail ? "Recipient:" : "Customer:",
                recipientName,
                internalTeamMail ? "Advisor:" : "Advisor:",
                sendButtonText(internalTeamMail, onboardingRequiresBothMails, closesIncident),
                internalTeamMail ? buildInternalSubject(selectedSuggestion) : buildSubject(customer, incident),
                internalTeamMail
                        ? buildInternalBody(recipientName, customerName, advisorName, selectedSuggestion, incident)
                        : buildBody(customerName, advisorName, selectedSuggestion, incident)
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
                incident.getId(),
                selectedSuggestion,
                trimmedSubject,
                trimmedBody
        );
        boolean closeIncident = shouldCloseIncidentAfterSend(incident, optionNumber);
        recommendationResolutionStore.saveRecommendationStep(incident.getId(), note, closeIncident);
    }

    @Override
    public boolean isRecommendationStepSent(int incidentId, int optionNumber) {
        return recommendationResolutionStore.hasRecommendationStep(incidentId, optionNumber);
    }

    private CustomerNote buildRecommendationNote(int customerId,
                                                 int advisorId,
                                                 int optionNumber,
                                                 int incidentId,
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
                incidentId,
                optionNumber,
                selectedSuggestion,
                subject,
                emailBody
        ));
        return note;
    }

    private boolean shouldCloseIncidentAfterSend(Incident incident, int optionNumber) {
        if (incident.getType() != IncidentType.ONBOARDING) {
            return true;
        }
        return recommendationResolutionStore.hasRecommendationStep(incident.getId(), otherOptionNumber(optionNumber));
    }

    private int otherOptionNumber(int optionNumber) {
        return optionNumber == 1 ? 2 : 1;
    }

    private String sendButtonText(boolean internalTeamMail, boolean onboardingRequiresBothMails, boolean closesIncident) {
        if (onboardingRequiresBothMails && !closesIncident) {
            return internalTeamMail ? "Send Team Mail" : "Send Customer Mail";
        }
        return internalTeamMail ? "Send Team Mail and Close Incident" : "Send and Close Incident";
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

    private String buildSubject(Customer customer, Incident incident) {
        if (incident.getType() == IncidentType.ONBOARDING) {
            return "Support with your onboarding, " + customer.getFirstName();
        }
        return "Support for your upcoming trip, " + customer.getFirstName();
    }

    private boolean isInternalTeamMail(Incident incident, String selectedSuggestion) {
        String normalizedSuggestion = selectedSuggestion == null ? "" : selectedSuggestion.toLowerCase();
        if (incident.getType() == IncidentType.ONBOARDING) {
            return normalizedSuggestion.contains("onboarding team")
                    || normalizedSuggestion.contains("review");
        }
        if (!(incident instanceof DelayIncident delayIncident)
                || delayIncident.getDelayMinutes() == null
                || delayIncident.getDelayMinutes() > 60) {
            return false;
        }
        return normalizedSuggestion.contains("driver")
                || normalizedSuggestion.contains("detour")
                || normalizedSuggestion.contains("lunch");
    }

    private String resolveInternalRecipient(String selectedSuggestion) {
        String normalizedSuggestion = selectedSuggestion == null ? "" : selectedSuggestion.toLowerCase();
        if (normalizedSuggestion.contains("onboarding")) {
            return "Onboarding Team";
        }
        if (normalizedSuggestion.contains("driver") || normalizedSuggestion.contains("detour")) {
            return "Pick-up Driver / Transport Team";
        }
        if (normalizedSuggestion.contains("lunch")) {
            return "Service Personnel / Catering Team";
        }
        return "Responsible Operations Team";
    }

    private String buildInternalSubject(String selectedSuggestion) {
        String normalizedSuggestion = selectedSuggestion == null ? "" : selectedSuggestion.toLowerCase();
        if (normalizedSuggestion.contains("onboarding")) {
            return "Onboarding support review needed";
        }
        if (normalizedSuggestion.contains("driver") || normalizedSuggestion.contains("detour")) {
            return "Operational support needed: adjust pick-up route";
        }
        if (normalizedSuggestion.contains("lunch")) {
            return "Operational support needed: quick lunch preparation";
        }
        return "Operational support needed for short delay";
    }

    private String buildInternalBody(String recipientName,
                                     String customerName,
                                     String advisorName,
                                     String selectedSuggestion,
                                     Incident incident) {
        if (incident.getType() == IncidentType.ONBOARDING) {
            return "Hello " + recipientName + ",\n\n"
                    + customerName + " appears to be having trouble with the onboarding process.\n"
                    + "Please review the customer case and provide targeted support where needed.\n\n"
                    + "Suggested action:\n"
                    + selectedSuggestion + "\n\n"
                    + "Incident context: " + normalizeIncidentDescription(incident.getDescription()) + "\n\n"
                    + "Please document the follow-up once this has been handled.\n\n"
                    + "Best regards,\n"
                    + advisorName + "\n"
                    + "Experience Management Advisor";
        }
        return "Hello " + recipientName + ",\n\n"
                + "A short delay has been detected for " + customerName + ".\n"
                + "Please take the following operational action:\n"
                + selectedSuggestion + "\n\n"
                + "Incident context: " + normalizeIncidentDescription(incident.getDescription()) + "\n\n"
                + "Please confirm once this has been handled.\n\n"
                + "Best regards,\n"
                + advisorName + "\n"
                + "Experience Management Advisor";
    }

    private String buildBody(String customerName, String advisorName, String selectedSuggestion, Incident incident) {
        if (incident.getType() == IncidentType.ONBOARDING) {
            return "Dear " + customerName + ",\n\n"
                    + "I noticed that your onboarding process may be taking longer than expected.\n"
                    + "If anything is unclear or if you would like additional support, please reply to this message and I will help you directly.\n\n"
                    + "Suggested next step:\n"
                    + selectedSuggestion + "\n\n"
                    + "Best regards,\n"
                    + advisorName + "\n"
                    + "Experience Management Advisor";
        }
        return "Dear " + customerName + ",\n\n"
                + "I am sorry for the inconvenience regarding " + normalizeIncidentDescription(incident.getDescription()) + ".\n"
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
