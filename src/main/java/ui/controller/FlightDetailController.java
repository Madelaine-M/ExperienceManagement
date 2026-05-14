package ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.domain.Feedback;
import model.domain.FeedbackItem;
import ui.model.FlightDetailData;
import ui.navigation.JourneyDetailNavigator;
import ui.service.FlightDetailService;
import ui.view.DashboardFormatters;
import ui.view.render.DashboardNodeFactory;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

// AI used
public class FlightDetailController {
    private static final DateTimeFormatter FEEDBACK_TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    private Label titleLabel;
    @FXML
    private Label subtitleLabel;
    @FXML
    private ScrollPane rootScrollPane;
    @FXML
    private VBox flightBox;
    @FXML
    private VBox feedbackBox;

    private final DashboardNodeFactory nodeFactory = new DashboardNodeFactory();
    private JourneyDetailNavigator navigator;
    private FlightDetailService flightDetailService;

    public void initializeFlightDetail(FlightDetailService flightDetailService,
                                       JourneyDetailNavigator navigator,
                                       int flightId) {
        this.flightDetailService = flightDetailService;
        this.navigator = navigator;
        populate(flightDetailService.loadFlightDetail(flightId));
    }

    @FXML
    private void goBack() {
        if (navigator != null) {
            navigator.goBack();
        }
    }

    private void populate(FlightDetailData data) {
        if (data == null || data.getFlight() == null) {
            titleLabel.setText("Flight Details");
            subtitleLabel.setText("Flight details could not be loaded.");
            flightBox.getChildren().setAll(nodeFactory.createEmptyStateCard("No flight data available."));
            feedbackBox.getChildren().setAll(nodeFactory.createEmptyStateCard("No feedback data available."));
            return;
        }

        titleLabel.setText("Flight Detail: " + data.getFlight().getFlightNumber());
        subtitleLabel.setText(data.getCustomerName() + " | " + DashboardFormatters.formatValue(data.getFlight().getFlightDate()));

        VBox flightCard = nodeFactory.createCard("detail-card");
        flightCard.getChildren().addAll(
                nodeFactory.createMetricLine("Customer", data.getCustomerName()),
                nodeFactory.createMetricLine("Flight number", DashboardFormatters.defaultText(data.getFlight().getFlightNumber(), "Unknown")),
                nodeFactory.createMetricLine("Flight date", DashboardFormatters.formatValue(data.getFlight().getFlightDate())),
                nodeFactory.createMetricLine("Booking date", DashboardFormatters.formatValue(data.getFlight().getBookingDate())),
                nodeFactory.createMetricLine("Package", DashboardFormatters.formatPackage(data.getFlight().getBookingPackage())),
                nodeFactory.createMetricLine("Flight status", DashboardFormatters.defaultText(data.getFlight().getStatus(), "Unknown"))
        );
        flightBox.getChildren().setAll(nodeFactory.createSectionTitle("Flight Summary"), flightCard);

        feedbackBox.getChildren().setAll(nodeFactory.createSectionTitle("Flight Feedback"));
        if (data.getFeedbacks().isEmpty()) {
            feedbackBox.getChildren().add(nodeFactory.createEmptyStateCard("No feedback recorded for this flight."));
        } else {
            for (Feedback feedback : data.getFeedbacks()) {
                feedbackBox.getChildren().add(createFeedbackCard(feedback));
            }
        }

        rootScrollPane.setVvalue(0);
    }

    private VBox createFeedbackCard(Feedback feedback) {
        VBox card = nodeFactory.createCard("detail-card");
        card.getChildren().addAll(
                nodeFactory.createMetricLine("Submitted", feedback.getCreatedAt() == null
                        ? "Unknown"
                        : FEEDBACK_TIMESTAMP.format(feedback.getCreatedAt())),
                nodeFactory.createMetricLine("Overall score", String.format(Locale.ENGLISH, "%.1f", (double) feedback.getOverallScore())),
                nodeFactory.createMetricLine("Customer satisfaction", String.valueOf(feedback.getCustomerSatScore())),
                nodeFactory.createMetricLine("Referral score", String.valueOf(feedback.getReferralScore()))
        );

        if (feedback.getItems() != null && !feedback.getItems().isEmpty()) {
            VBox itemsCard = nodeFactory.createCard("inner-card");
            itemsCard.getChildren().add(nodeFactory.createSectionLabel("Feedback Items"));
            for (FeedbackItem item : feedback.getItems()) {
                itemsCard.getChildren().add(
                        nodeFactory.createMetricLine(
                                item.getCategory().name(),
                                "Score " + item.getScore() + " | " + DashboardFormatters.defaultText(item.getComment(), "No comment recorded.")
                        )
                );
            }
            card.getChildren().add(itemsCard);
        }
        return card;
    }
}
