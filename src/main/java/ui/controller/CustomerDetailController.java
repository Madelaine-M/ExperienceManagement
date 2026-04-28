package ui.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.CustomerDetailView;
import model.CustomerNote;
import model.CustomerOverview;
import model.Flight;
import ui.navigation.JourneyDetailNavigator;
import ui.view.DashboardFormatters;
import ui.view.render.DashboardNodeFactory;

import java.util.List;
import java.util.Locale;

public class CustomerDetailController {
    @FXML
    private VBox root;
    @FXML
    private VBox contentBox;

    private final DashboardNodeFactory nodeFactory = new DashboardNodeFactory();
    private JourneyDetailNavigator journeyDetailNavigator;

    public void configure(JourneyDetailNavigator journeyDetailNavigator) {
        this.journeyDetailNavigator = journeyDetailNavigator;
    }

    public void setData(CustomerDetailView detail, CustomerOverview overview) {
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
                ),
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
}
