package ui.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.view.CustomerDetailView;
import model.domain.CustomerNote;
import model.view.CustomerOverview;
import model.domain.Flight;
import model.workflow.RecoveryActionSummary;
import ui.navigation.JourneyDetailNavigator;
import ui.view.DashboardFormatters;
import ui.view.render.DashboardNodeFactory;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class CustomerDetailController {
    private static final DateTimeFormatter RECOVERY_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    private VBox root;
    @FXML
    private VBox contentBox;

    private final DashboardNodeFactory nodeFactory = new DashboardNodeFactory();
    private JourneyDetailNavigator journeyDetailNavigator;
    private ui.service.DashboardDataService dashboardDataService;
    private int advisorId;
    private Runnable refreshDashboardAction;
    private CustomerOverview currentOverview;

    public void configure(JourneyDetailNavigator journeyDetailNavigator,
                          ui.service.DashboardDataService dashboardDataService,
                          int advisorId,
                          Runnable refreshDashboardAction) {
        this.journeyDetailNavigator = journeyDetailNavigator;
        this.dashboardDataService = dashboardDataService;
        this.advisorId = advisorId;
        this.refreshDashboardAction = refreshDashboardAction;
    }

    public void setData(CustomerDetailView detail, CustomerOverview overview) {
        this.currentOverview = overview;
        VBox section = new VBox(18);
        section.getChildren().addAll(
                nodeFactory.createSectionTitle("Customer Detail: " + DashboardFormatters.formatName(detail.getCustomerFirstName(), detail.getCustomerLastName())),
                nodeFactory.createHeroCard(
                        DashboardFormatters.formatName(detail.getCustomerFirstName(), detail.getCustomerLastName()),
                        buildInitials(detail.getCustomerFirstName(), detail.getCustomerLastName()),
                        "CLIENT VIEW",
                        nodeFactory.createDetailLine("Package type", DashboardFormatters.formatPackage(detail.getBookingPackage())),
                        nodeFactory.createDetailLine("Status", DashboardFormatters.formatValue(detail.getStatus())),
                        nodeFactory.createDetailLine("Customer type", DashboardFormatters.formatValue(detail.getCustomerType())),
                        nodeFactory.createDetailLine("CV Score", String.format(Locale.ENGLISH, "%.1f", detail.getCvScore())),
                        nodeFactory.createDetailLine("Flight", detail.getCurrentFlight() == null
                                ? "No active flight"
                                : detail.getCurrentFlight().getFlightNumber() + " on " + DashboardFormatters.formatValue(detail.getCurrentFlight().getFlightDate()))
                )
        );

        Node journeyButton = createJourneyButton(detail);
        if (journeyButton != null) {
            section.getChildren().add(journeyButton);
        }

        section.getChildren().addAll(
                nodeFactory.createInfoCard(
                        "Customer Profile",
                        "Preferences: " + DashboardFormatters.defaultText(detail.getPreferences(), "No preferences recorded.") + "\n"
                                + "Next booking note: " + DashboardFormatters.defaultText(detail.getApplyToNextBooking(), "No follow-up note.") + "\n"
                                + "Payment method: " + DashboardFormatters.formatValue(detail.getPaymentMethod()) + "\n"
                                + "Public person: " + DashboardFormatters.yesNo(detail.isPublicPerson())
                )
        );

        Node recoveryActionCard = createRecoveryActionCard(detail.getLatestRecoveryAction());
        if (recoveryActionCard != null) {
            section.getChildren().add(recoveryActionCard);
        }

        section.getChildren().addAll(
                createAddNoteCard(detail),
                nodeFactory.createInfoCard("Advisor Notes", formatNotes(detail.getNotes())),
                createCustomerIncidentCard(detail),
                createPreviousFlightsCard(detail.getPreviousFlightsList(), detail.getPreviousFlights())
        );
        contentBox.getChildren().setAll(section);
    }

    public VBox getRoot() {
        return root;
    }

    private VBox createCustomerIncidentCard(CustomerDetailView detail) {
        if (!detail.hasOpenIncident() || detail.getOpenIncidentId() == null) {
            VBox wrapper = nodeFactory.createCard("detail-card");
            wrapper.getChildren().add(nodeFactory.createInfoCard("Open Incidents", "No active incidents for this customer."));
            return wrapper;
        }

        VBox card = nodeFactory.createCard("issue-card");
        card.getChildren().addAll(
                nodeFactory.createSectionLabel("Open Incident"),
                nodeFactory.createMetricLine("Incident Summary", DashboardFormatters.defaultText(detail.getIncidentDescription(), "Open incident recorded.")),
                nodeFactory.createMetricLine("Priority Score", detail.getHighestPriorityScore() == null
                        ? "n/a"
                        : String.format(Locale.ENGLISH, "%.1f", detail.getHighestPriorityScore())),
                nodeFactory.createMetricLine("Incident Id", String.valueOf(detail.getOpenIncidentId()))
        );
        return card;
    }

    private String formatNotes(List<CustomerNote> notes) {
        if (notes == null || notes.isEmpty()) {
            return "No advisor notes recorded.";
        }

        StringBuilder builder = new StringBuilder();
        for (CustomerNote note : notes) {
            if (builder.length() > 0) {
                builder.append("\n\n");
            }
            builder.append("- ").append(note.getNoteText());
        }
        return builder.toString();
    }

    private Node createRecoveryActionCard(RecoveryActionSummary recoveryAction) {
        if (recoveryAction == null) {
            return null;
        }

        VBox card = nodeFactory.createCard("detail-card");
        card.getChildren().addAll(
                nodeFactory.createSectionLabel("Latest Recovery Action"),
                nodeFactory.createMetricLine("Status", "Recovery mail sent"),
                nodeFactory.createMetricLine("Resolved by", "Recommendation option " + recoveryAction.getOptionNumber()),
                nodeFactory.createMetricLine("Advisor", DashboardFormatters.defaultText(recoveryAction.getAdvisorName(), "Advisor #" + recoveryAction.getAdvisorId())),
                nodeFactory.createMetricLine("Sent at", formatRecoveryTimestamp(recoveryAction)),
                nodeFactory.createMetricLine("Recommendation", DashboardFormatters.defaultText(recoveryAction.getSelectedRecommendation(), "n/a")),
                nodeFactory.createMetricLine("Subject", DashboardFormatters.defaultText(recoveryAction.getSubject(), "n/a")),
                nodeFactory.createInfoCard("Mail Preview", DashboardFormatters.defaultText(recoveryAction.getMailBody(), "No mail body recorded."))
        );
        return card;
    }

    private Node createAddNoteCard(CustomerDetailView detail) {
        VBox card = nodeFactory.createCard("detail-card");
        card.getChildren().add(nodeFactory.createSectionLabel("Add Advisor Note"));

        TextArea noteInput = new TextArea();
        noteInput.getStyleClass().add("journey-note-input");
        noteInput.setPromptText("Add a note for this customer...");
        noteInput.setWrapText(true);
        noteInput.setPrefRowCount(4);

        Label statusLabel = new Label();
        statusLabel.getStyleClass().add("journey-save-status");

        Button saveButton = nodeFactory.createActionButton("Save Note", "secondary-button", () -> {
            try {
                dashboardDataService.saveCustomerAdvisorNote(detail.getCustomerId(), advisorId, noteInput.getText());
                noteInput.clear();
                statusLabel.setText("Note saved.");
                CustomerDetailView refreshed = dashboardDataService.loadCustomerDetail(detail.getCustomerId());
                if (refreshed != null) {
                    setData(refreshed, currentOverview);
                }
                if (refreshDashboardAction != null) {
                    refreshDashboardAction.run();
                }
            } catch (RuntimeException exception) {
                statusLabel.setText(exception.getMessage() == null ? "Note could not be saved." : exception.getMessage());
            }
        });

        card.getChildren().addAll(noteInput, saveButton, statusLabel);
        return card;
    }

    private String buildInitials(String firstName, String lastName) {
        String first = DashboardFormatters.defaultText(firstName, " ").trim();
        String last = DashboardFormatters.defaultText(lastName, " ").trim();
        String firstInitial = first.isEmpty() ? "" : first.substring(0, 1).toUpperCase(Locale.ENGLISH);
        String lastInitial = last.isEmpty() ? "" : last.substring(0, 1).toUpperCase(Locale.ENGLISH);
        return (firstInitial + lastInitial).isBlank() ? "AD" : firstInitial + lastInitial;
    }

    private Node createJourneyButton(CustomerDetailView detail) {
        if (journeyDetailNavigator == null || detail.getCurrentFlight() == null) {
            return null;
        }

        return nodeFactory.createActionButton(
                "Open Current Flight Journey",
                "secondary-button",
                () -> journeyDetailNavigator.openJourney(detail.getCustomerId(), detail.getOpenIncidentId())
        );
    }

    private Node createPreviousFlightsCard(List<Flight> previousFlights, String previousFlightsSummary) {
        VBox card = nodeFactory.createCard("detail-card");
        card.getChildren().add(nodeFactory.createSectionLabel("Previous Flights"));
        if (previousFlights == null || previousFlights.isEmpty()) {
            card.getChildren().add(new Label(DashboardFormatters.defaultText(previousFlightsSummary, "No previous flights recorded.")));
            return card;
        }

        for (Flight flight : previousFlights) {
            card.getChildren().add(createFlightRow(flight));
        }
        return card;
    }

    private Node createFlightRow(Flight flight) {
        HBox row = new HBox(12);
        row.getStyleClass().add("flight-link-row");
        Label label = new Label(flight.getFlightNumber() + " | " + DashboardFormatters.formatValue(flight.getFlightDate()));
        label.getStyleClass().add("journey-step-label");
        HBox.setHgrow(label, Priority.ALWAYS);
        row.getChildren().addAll(label, nodeFactory.createActionButton("View Details", "secondary-button",
                () -> journeyDetailNavigator.openFlightDetail(flight.getId())));
        return row;
    }

    private String formatRecoveryTimestamp(RecoveryActionSummary recoveryAction) {
        if (recoveryAction.getSentAt() == null) {
            return "n/a";
        }
        return RECOVERY_TIMESTAMP_FORMATTER.format(recoveryAction.getSentAt());
    }
}
