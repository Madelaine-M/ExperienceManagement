package ui.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.Flight;
import model.IncidentDetailView;
import model.IncidentOverview;
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

    public void configure(JourneyDetailNavigator journeyDetailNavigator) {
        this.journeyDetailNavigator = journeyDetailNavigator;
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
                        nodeFactory.createDetailLine("Customer type", detail.isReturning() ? "Returning" : "New"),
                        nodeFactory.createDetailLine("Customer segment", DashboardFormatters.formatValue(detail.getCustomerType())),
                        nodeFactory.createDetailLine("Flight", formatFlight(detail))
                )
        );

        Node journeyButton = createJourneyButton(detail);
        if (journeyButton != null) {
            section.getChildren().add(journeyButton);
        }

        section.getChildren().addAll(
                createIssueCard(detail, overview),
                createPreviousFlightsCard(detail.getPreviousFlightsList(), detail.getPreviousFlights())
        );
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

        var why = new javafx.scene.control.Label(buildIncidentReason(detail, overview));
        why.getStyleClass().add("issue-body");
        why.setWrapText(true);

        VBox facts = nodeFactory.createCard("inner-card");
        facts.getChildren().addAll(
                nodeFactory.createMetricLine("Revenue Risk", DashboardFormatters.formatCurrency(overview.getRevenueRisk())),
                nodeFactory.createMetricLine("Score Impact", String.format(Locale.ENGLISH, "%.1f", overview.getScoreImpact())),
                nodeFactory.createMetricLine("Priority Score", String.format(Locale.ENGLISH, "%.1f", overview.getPriorityScore()))
        );

        card.getChildren().addAll(title, headline, why, facts);
        return card;
    }

    private String formatIncidentHeadline(IncidentDetailView detail, IncidentOverview overview) {
        String type = DashboardFormatters.formatValue(detail.getIncidentType());
        if (detail.getDelayMinutes() != null) {
            return type + " (" + detail.getDelayMinutes() + " min)";
        }
        return type + " | " + DashboardFormatters.defaultText(overview.getDescription(), "Incident recorded");
    }

    private String buildIncidentReason(IncidentDetailView detail, IncidentOverview overview) {
        if (detail.getDelayMinutes() != null) {
            return "Why: " + DashboardFormatters.defaultText(overview.getDescription(), "Delay incident reported for this customer.");
        }
        return "Why: " + DashboardFormatters.defaultText(overview.getDescription(), "Feedback-related incident reported.");
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
