package ui.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.domain.Flight;
import model.view.IncidentDetailView;
import model.view.IncidentOverview;
import model.enums.IncidentType;
import ui.navigation.JourneyDetailNavigator;
import ui.view.DashboardFormatters;
import ui.view.render.DashboardNodeFactory;

import java.util.List;
import java.util.Locale;

public class IncidentDetailController {
    @FXML
    private VBox root;
    @FXML
    private VBox contentBox;

    private final DashboardNodeFactory nodeFactory = new DashboardNodeFactory();
    private JourneyDetailNavigator journeyDetailNavigator;
    private ui.service.DashboardDataService dashboardDataService;
    private int advisorId;
    private Runnable refreshDashboardAction;

    public void configure(JourneyDetailNavigator journeyDetailNavigator,
                          ui.service.DashboardDataService dashboardDataService,
                          int advisorId,
                          Runnable refreshDashboardAction) {
        this.journeyDetailNavigator = journeyDetailNavigator;
        this.dashboardDataService = dashboardDataService;
        this.advisorId = advisorId;
        this.refreshDashboardAction = refreshDashboardAction;
    }

    public void setData(IncidentDetailView detail, IncidentOverview overview) {
        VBox section = new VBox(18);
        section.getChildren().addAll(
                nodeFactory.createSectionTitle("Case Detail: " + DashboardFormatters.formatName(detail.getCustomerFirstName(), detail.getCustomerLastName())),
                nodeFactory.createHeroCard(
                        DashboardFormatters.formatName(detail.getCustomerFirstName(), detail.getCustomerLastName()),
                        buildInitials(detail.getCustomerFirstName(), detail.getCustomerLastName()),
                        "INCIDENT VIEW",
                        nodeFactory.createDetailLine("Package type", DashboardFormatters.formatPackage(detail.getBookingPackage())),
                        nodeFactory.createDetailLine("Status", DashboardFormatters.formatValue(detail.getCustomerStatus())),
                        nodeFactory.createDetailLine("Customer type", detail.isReturning() ? "Returning" : "New"),
                        nodeFactory.createDetailLine("Customer segment", DashboardFormatters.formatValue(detail.getCustomerType())),
                        nodeFactory.createDetailLine("Flight", formatFlight(detail))
                )
        );

        Node journeyButton = createJourneyButton(detail);
        section.getChildren().add(createIssueCard(detail, overview));
        if (journeyButton != null) {
            section.getChildren().add(journeyButton);
        }
        section.getChildren().add(createAddNoteCard(detail));
        section.getChildren().add(createPreviousFlightsCard(detail.getPreviousFlightsList(), detail.getPreviousFlights()));
        contentBox.getChildren().setAll(section);
    }

    public VBox getRoot() {
        return root;
    }

    private VBox createIssueCard(IncidentDetailView detail, IncidentOverview overview) {
        VBox card = nodeFactory.createCard("issue-card");
        var title = new javafx.scene.control.Label("Current Issue");
        title.getStyleClass().add("issue-title");
        title.setWrapText(true);

        var headline = new javafx.scene.control.Label(formatIncidentHeadline(detail, overview));
        headline.getStyleClass().add("issue-body");
        headline.setWrapText(true);

        VBox facts = nodeFactory.createCard("inner-card");
        facts.getChildren().addAll(
                nodeFactory.createMetricLine("Revenue Risk", DashboardFormatters.formatCurrency(overview.getRevenueRisk())),
                nodeFactory.createMetricLine("Score Impact", String.valueOf(overview.getScoreImpact())),
                nodeFactory.createMetricLine("Priority Score", String.format(Locale.ENGLISH, "%.1f", overview.getPriorityScore()))
        );

        card.getChildren().addAll(title, headline, facts);
        return card;
    }

    private String formatIncidentHeadline(IncidentDetailView detail, IncidentOverview overview) {
        String type = DashboardFormatters.formatValue(detail.getIncidentType());
        if (detail.getIncidentType() == IncidentType.DELAY && detail.getDelayMinutes() != null) {
            return type + " (" + detail.getDelayMinutes() + " min)";
        }
        return type + " | " + DashboardFormatters.defaultText(overview.getDescription(), "Incident recorded");
    }

    private String formatFlight(IncidentDetailView detail) {
        if (detail.getCurrentFlightNumber() == null || detail.getCurrentFlightNumber().isBlank()) {
            return "No current flight";
        }
        return detail.getCurrentFlightNumber() + " on " + DashboardFormatters.formatValue(detail.getCurrentFlightDate());
    }

    private Node createJourneyButton(IncidentDetailView detail) {
        if (journeyDetailNavigator == null || detail.getCurrentFlightNumber() == null || detail.getCurrentFlightNumber().isBlank()) {
            return null;
        }

        return nodeFactory.createActionButton(
                "Open Current Flight Journey",
                "secondary-button",
                () -> journeyDetailNavigator.openJourney(detail.getCustomerId(), detail.getIncidentId())
        );
    }

    private Node createAddNoteCard(IncidentDetailView detail) {
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

    private String buildInitials(String firstName, String lastName) {
        String first = DashboardFormatters.defaultText(firstName, " ").trim();
        String last = DashboardFormatters.defaultText(lastName, " ").trim();
        String firstInitial = first.isEmpty() ? "" : first.substring(0, 1).toUpperCase(Locale.ENGLISH);
        String lastInitial = last.isEmpty() ? "" : last.substring(0, 1).toUpperCase(Locale.ENGLISH);
        return (firstInitial + lastInitial).isBlank() ? "AD" : firstInitial + lastInitial;
    }
}
