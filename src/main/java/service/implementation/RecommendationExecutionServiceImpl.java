package service.implementation;

import database.connection.ConnectionProvider;
import database.connection.DatabaseConnectionProvider;
import model.domain.ActionItem;
import model.domain.Advisor;
import model.domain.Customer;
import model.domain.CustomerNote;
import model.domain.Incident;
import model.workflow.RecommendationEmailDraft;
import model.enums.ActionStatus;
import model.enums.IncidentStatus;
import repository.interfaces.AdvisorRepository;
import service.interfaces.frontend.ActionService;
import service.interfaces.frontend.CustomerService;
import service.interfaces.frontend.IncidentService;
import service.interfaces.frontend.RecommendationExecutionService;
import support.RecoveryActionNoteCodec;

import java.time.LocalDateTime;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class RecommendationExecutionServiceImpl implements RecommendationExecutionService {
    private final ActionService actionService;
    private final IncidentService incidentService;
    private final CustomerService customerService;
    private final AdvisorRepository advisorRepository;
    private final ConnectionProvider connectionProvider;

    public RecommendationExecutionServiceImpl(ActionService actionService,
                                              IncidentService incidentService,
                                              CustomerService customerService,
                                              AdvisorRepository advisorRepository) {
        this(
                actionService,
                incidentService,
                customerService,
                advisorRepository,
                new DatabaseConnectionProvider()
        );
    }

    public RecommendationExecutionServiceImpl(ActionService actionService,
                                              IncidentService incidentService,
                                              CustomerService customerService,
                                              AdvisorRepository advisorRepository,
                                              ConnectionProvider connectionProvider) {
        this.actionService = actionService;
        this.incidentService = incidentService;
        this.customerService = customerService;
        this.advisorRepository = advisorRepository;
        this.connectionProvider = connectionProvider;
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

        executeRecommendationTransaction(
                incident.getId(),
                customer.getId(),
                advisorId,
                optionNumber,
                selectedSuggestion,
                trimmedSubject,
                trimmedBody
        );
    }

    private void executeRecommendationTransaction(int incidentId,
                                                  int customerId,
                                                  int advisorId,
                                                  int optionNumber,
                                                  String selectedSuggestion,
                                                  String subject,
                                                  String emailBody) {
        String updateActionsSql = "UPDATE action_items SET status = ? WHERE incident_id = ?;";
        String updateIncidentSql = "UPDATE incidents SET status = ? WHERE id = ?;";
        String insertNoteSql = """
                INSERT INTO customer_notes (customer_id, advisor_id, note_text, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?);
                """;

        try (Connection connection = connectionProvider.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement actionStatement = connection.prepareStatement(updateActionsSql);
                 PreparedStatement incidentStatement = connection.prepareStatement(updateIncidentSql);
                 PreparedStatement noteStatement = connection.prepareStatement(insertNoteSql)) {

                actionStatement.setString(1, ActionStatus.COMPLETED.name());
                actionStatement.setInt(2, incidentId);
                int updatedActions = actionStatement.executeUpdate();
                if (updatedActions <= 0) {
                    throw new IllegalStateException("No action items were completed for incident " + incidentId + ".");
                }

                incidentStatement.setString(1, IncidentStatus.CLOSED.name());
                incidentStatement.setInt(2, incidentId);
                int updatedIncidents = incidentStatement.executeUpdate();
                if (updatedIncidents != 1) {
                    throw new IllegalStateException("Incident " + incidentId + " could not be closed.");
                }

                CustomerNote note = buildRecommendationNote(customerId, advisorId, optionNumber, selectedSuggestion, subject, emailBody);
                noteStatement.setInt(1, note.getCustomerId());
                noteStatement.setInt(2, note.getAdvisorId());
                noteStatement.setString(3, note.getNoteText());
                noteStatement.setTimestamp(4, Timestamp.valueOf(note.getCreatedAt()));
                noteStatement.setTimestamp(5, Timestamp.valueOf(note.getUpdatedAt()));
                int insertedNotes = noteStatement.executeUpdate();
                if (insertedNotes != 1) {
                    throw new IllegalStateException("Recommendation note could not be saved.");
                }

                connection.commit();
            } catch (Exception exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Recommendation could not be completed.", exception);
        }
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
