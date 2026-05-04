package ui.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.domain.CustomerNote;
import model.domain.Feedback;
import model.domain.FeedbackItem;
import model.domain.Flight;
import ui.navigation.JourneyDetailNavigator;
import ui.model.JourneyDetailData;
import ui.model.JourneyStepState;
import ui.model.JourneyStepView;
import ui.service.JourneyDetailService;
import ui.view.DashboardFormatters;
import ui.view.render.DashboardNodeFactory;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class JourneyDetailController {
    private static final DateTimeFormatter NOTE_TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    private Button backButton;
    @FXML
    private Label titleLabel;
    @FXML
    private Label subtitleLabel;
    @FXML
    private ScrollPane rootScrollPane;
    @FXML
    private VBox statusBox;
    @FXML
    private VBox contextBox;
    @FXML
    private VBox feedbackBox;
    @FXML
    private VBox previousFlightsBox;
    @FXML
    private VBox notesHistoryBox;
    @FXML
    private TextArea noteInput;
    @FXML
    private Label saveStatusLabel;

    private final DashboardNodeFactory nodeFactory = new DashboardNodeFactory();

    private JourneyDetailService journeyDetailService;
    private JourneyDetailNavigator journeyDetailNavigator;
    private int customerId;
    private Integer incidentId;
    private int advisorId;

    public void initializeJourney(JourneyDetailService journeyDetailService,
                                  JourneyDetailNavigator journeyDetailNavigator,
                                  int customerId,
                                  Integer incidentId,
                                  int advisorId) {
        this.journeyDetailService = journeyDetailService;
        this.journeyDetailNavigator = journeyDetailNavigator;
        this.customerId = customerId;
        this.incidentId = incidentId;
        this.advisorId = advisorId;
        refresh();
    }

    @FXML
    private void goBack() {
        if (journeyDetailNavigator != null) {
            journeyDetailNavigator.goBack();
        }
    }

    @FXML
    private void saveNote() {
        JourneyDetailData refreshed = journeyDetailService.saveAdvisorNote(customerId, incidentId, advisorId, noteInput.getText());
        noteInput.clear();
        saveStatusLabel.setText("Note saved.");
        populate(refreshed);
    }

    private void refresh() {
        populate(journeyDetailService.loadJourneyDetail(customerId, incidentId));
    }

    private void populate(JourneyDetailData data) {
        if (data == null || data.getCustomerDetail() == null) {
            titleLabel.setText("Customer Journey");
            subtitleLabel.setText("Journey details could not be loaded.");
            statusBox.getChildren().setAll(nodeFactory.createEmptyStateCard("No journey status available."));
            contextBox.getChildren().setAll(nodeFactory.createEmptyStateCard("No customer context available."));
            feedbackBox.getChildren().setAll(nodeFactory.createEmptyStateCard("No feedback context available."));
            notesHistoryBox.getChildren().setAll(nodeFactory.createEmptyStateCard("No notes available."));
            return;
        }

        Flight currentFlight = data.getCurrentFlight();
        titleLabel.setText("Customer Journey: " + data.getCustomerName());
        subtitleLabel.setText(buildSubtitle(data, currentFlight));

        populateStatus(data.getJourneySteps());
        populateContext(data, currentFlight);
        populateFeedback(data);
        populatePreviousFlights(data.getCustomerDetail().getPreviousFlightsList());
        populateNotes(data.getNotes());
        Platform.runLater(() -> {
            rootScrollPane.setVvalue(0);
            saveStatusLabel.setText(saveStatusLabel.getText());
            titleLabel.requestFocus();
        });
    }

    private void populateStatus(List<JourneyStepView> steps) {
        statusBox.getChildren().setAll(nodeFactory.createSectionTitle("Journey Status"));
        VBox card = nodeFactory.createCard("detail-card");
        VBox stepList = new VBox(10);
        for (JourneyStepView step : steps) {
            stepList.getChildren().add(createJourneyStep(step));
        }
        card.getChildren().add(stepList);
        statusBox.getChildren().add(card);
    }

    private void populateContext(JourneyDetailData data, Flight currentFlight) {
        contextBox.getChildren().setAll(nodeFactory.createSectionTitle("Customer Context"));
        VBox card = nodeFactory.createCard("detail-card");
        card.getChildren().addAll(
                nodeFactory.createMetricLine("Current flight", formatFlight(currentFlight)),
                nodeFactory.createMetricLine("Preferences", DashboardFormatters.defaultText(data.getCustomerDetail().getPreferences(), "No preferences recorded.")),
                nodeFactory.createMetricLine("Next-booking note", DashboardFormatters.defaultText(data.getCustomerDetail().getApplyToNextBooking(), "No follow-up note.")),
                nodeFactory.createMetricLine("Open incident", data.getCustomerDetail().hasOpenIncident()
                        ? DashboardFormatters.defaultText(data.getCustomerDetail().getIncidentDescription(), "Open incident recorded.")
                        : "No active incident")
        );
        contextBox.getChildren().add(card);
    }

    private void populateFeedback(JourneyDetailData data) {
        feedbackBox.getChildren().setAll(nodeFactory.createSectionTitle("Feedback Context"));
        if (!data.hasFeedbackContext()) {
            feedbackBox.getChildren().add(nodeFactory.createEmptyStateCard("No feedback context for this journey step."));
            return;
        }

        Feedback feedback = data.getFeedback();
        VBox card = nodeFactory.createCard("detail-card");
        card.getChildren().addAll(
                nodeFactory.createMetricLine("Overall score", String.format(Locale.ENGLISH, "%.1f", feedback.getOverallScore())),
                nodeFactory.createMetricLine("Customer satisfaction", String.valueOf(feedback.getCustomerSatScore())),
                nodeFactory.createMetricLine("Referral score", String.valueOf(feedback.getReferralScore()))
        );

        FeedbackItem highlighted = data.getHighlightedFeedbackItem();
        if (highlighted != null) {
            card.getChildren().add(nodeFactory.createInfoCard(
                    "Triggered Feedback Item",
                    highlighted.getCategory() + " | Score " + highlighted.getScore() + "\n"
                            + DashboardFormatters.defaultText(highlighted.getComment(), "No comment recorded.")
            ));
        }

        if (feedback.getItems() != null && !feedback.getItems().isEmpty()) {
            VBox itemsCard = nodeFactory.createCard("inner-card");
            itemsCard.getChildren().add(nodeFactory.createSectionLabel("All Feedback Items"));
            for (FeedbackItem item : feedback.getItems()) {
                itemsCard.getChildren().add(nodeFactory.createMetricLine(
                        item.getCategory().name(),
                        "Score " + item.getScore() + " | " + DashboardFormatters.defaultText(item.getComment(), "No comment recorded.")
                ));
            }
            card.getChildren().add(itemsCard);
        }

        feedbackBox.getChildren().add(card);
    }

    private void populateNotes(List<CustomerNote> notes) {
        notesHistoryBox.getChildren().setAll(nodeFactory.createSectionTitle("Advisor Notes"));
        if (notes == null || notes.isEmpty()) {
            notesHistoryBox.getChildren().add(nodeFactory.createEmptyStateCard("No advisor notes recorded yet."));
            return;
        }

        for (CustomerNote note : notes) {
            VBox noteCard = nodeFactory.createCard("detail-card");
            noteCard.getStyleClass().add("note-card");
            noteCard.getChildren().addAll(
                    nodeFactory.createSectionLabel("Advisor #" + note.getAdvisorId()),
                    nodeFactory.createMetricLine("Saved", note.getCreatedAt() == null ? "Unknown" : NOTE_TIMESTAMP.format(note.getCreatedAt())),
                    nodeFactory.createInfoCard("Note", DashboardFormatters.defaultText(note.getNoteText(), "No note text."))
            );
            notesHistoryBox.getChildren().add(noteCard);
        }
    }

    private void populatePreviousFlights(List<Flight> previousFlights) {
        previousFlightsBox.getChildren().setAll(nodeFactory.createSectionTitle("Previous Flights"));
        if (previousFlights == null || previousFlights.isEmpty()) {
            previousFlightsBox.getChildren().add(nodeFactory.createEmptyStateCard("No previous flights recorded."));
            return;
        }

        VBox card = nodeFactory.createCard("detail-card");
        for (Flight flight : previousFlights) {
            HBox row = new HBox(12);
            row.getStyleClass().add("flight-link-row");
            Label label = new Label(flight.getFlightNumber() + " | " + DashboardFormatters.formatValue(flight.getFlightDate()));
            label.getStyleClass().add("journey-step-label");
            HBox.setHgrow(label, Priority.ALWAYS);
            row.getChildren().addAll(
                    label,
                    nodeFactory.createActionButton("View Flight", "secondary-button", () -> journeyDetailNavigator.openFlightDetail(flight.getId()))
            );
            card.getChildren().add(row);
        }
        previousFlightsBox.getChildren().add(card);
    }

    private HBox createJourneyStep(JourneyStepView step) {
        HBox line = new HBox(14);
        line.getStyleClass().addAll("journey-step", styleClassFor(step.getState()));
        Label stateLabel = new Label(stateText(step.getState()));
        stateLabel.getStyleClass().add("journey-step-state");

        Label label = new Label(step.getLabel());
        label.getStyleClass().add("journey-step-label");
        HBox.setHgrow(label, Priority.ALWAYS);

        line.getChildren().addAll(stateLabel, label);
        return line;
    }

    private String styleClassFor(JourneyStepState state) {
        return switch (state) {
            case COMPLETED -> "journey-step-completed";
            case CURRENT -> "journey-step-current";
            case UPCOMING -> "journey-step-upcoming";
        };
    }

    private String stateText(JourneyStepState state) {
        return switch (state) {
            case COMPLETED -> "Done";
            case CURRENT -> "Current";
            case UPCOMING -> "Upcoming";
        };
    }

    private String buildSubtitle(JourneyDetailData data, Flight currentFlight) {
        String flightText = formatFlight(currentFlight);
        if (data.getIncidentDetail() == null) {
            return flightText;
        }
        return DashboardFormatters.formatValue(data.getIncidentDetail().getIncidentType()) + " | " + flightText;
    }

    private String formatFlight(Flight currentFlight) {
        if (currentFlight == null) {
            return "No current flight";
        }
        return currentFlight.getFlightNumber() + " on " + DashboardFormatters.formatValue(currentFlight.getFlightDate());
    }
}
