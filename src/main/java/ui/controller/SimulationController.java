package ui.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import javafx.util.StringConverter;
import model.domain.Advisor;
import simulation.model.SimulationConfig;
import simulation.model.SimulationSnapshot;
import simulation.model.SimulationStatus;
import ui.service.SimulationControlService;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Consumer;

//AI used
public class SimulationController {
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    private ComboBox<Advisor> advisorComboBox;
    @FXML
    private TextField customerIntervalField;
    @FXML
    private TextField journeyIntervalField;
    @FXML
    private TextField delayProbabilityField;
    @FXML
    private TextField minDelayField;
    @FXML
    private TextField maxDelayField;
    @FXML
    private TextField feedbackProbabilityField;
    @FXML
    private TextField lowScoreThresholdField;
    @FXML
    private TextField onboardingStuckThresholdField;
    @FXML
    private TextField onboardingProbabilityField;
    @FXML
    private Label statusValueLabel;
    @FXML
    private Label startedAtValueLabel;
    @FXML
    private Label stoppedAtValueLabel;
    @FXML
    private Label updatedAtValueLabel;
    @FXML
    private Label customersMetricLabel;
    @FXML
    private Label journeysMetricLabel;
    @FXML
    private Label delayMetricLabel;
    @FXML
    private Label onboardingMetricLabel;
    @FXML
    private Label feedbackMetricLabel;
    @FXML
    private Label feedbackIncidentMetricLabel;
    @FXML
    private Label messageLabel;

    private final Timeline simulationTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> advanceSimulation()));

    private SimulationControlService simulationControlService;
    private Consumer<Advisor> dashboardOpener;

    @FXML
    private void initialize() {
        advisorComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Advisor advisor) {
                if (advisor == null) {
                    return "";
                }
                return advisor.getFirstName() + " " + advisor.getLastName() + " | " + advisor.getSpeciality();
            }

            @Override
            public Advisor fromString(String string) {
                return null;
            }
        });

        simulationTimeline.setCycleCount(Timeline.INDEFINITE);
        simulationTimeline.play();
    }

    public void initializeSimulation(SimulationControlService simulationControlService,
                                     Consumer<Advisor> dashboardOpener) {
        this.simulationControlService = simulationControlService;
        this.dashboardOpener = dashboardOpener;

        try {
            advisorComboBox.setItems(FXCollections.observableArrayList(simulationControlService.loadAdvisors()));
            if (!advisorComboBox.getItems().isEmpty()) {
                advisorComboBox.getSelectionModel().selectFirst();
            }
            refreshSnapshot("Configure a simulation run or open the dashboard.");
        } catch (RuntimeException exception) {
            messageLabel.setText("Simulation data could not be initialized.");
        }
    }

    @FXML
    private void startSimulation() {
        try {
            SimulationConfig desiredConfig = buildConfig();
            SimulationSnapshot currentSnapshot = simulationControlService.getSnapshot();

            if (shouldPrepareNewRun(currentSnapshot, desiredConfig)) {
                simulationControlService.configure(desiredConfig);
            }

            simulationControlService.start();
            refreshSnapshot("Simulation running.");
        } catch (RuntimeException exception) {
            messageLabel.setText(exception.getMessage());
        }
    }

    @FXML
    private void stopSimulation() {
        if (simulationControlService == null) {
            return;
        }
        try {
            simulationControlService.stop();
            refreshSnapshot("Simulation paused.");
        } catch (RuntimeException exception) {
            messageLabel.setText("Simulation could not be paused right now.");
        }
    }

    @FXML
    private void clearSimulationData() {
        if (simulationControlService == null) {
            return;
        }
        try {
            simulationControlService.clearSimulationData();
            refreshSnapshot("Simulation data cleared.");
        } catch (RuntimeException exception) {
            messageLabel.setText("Simulation data could not be cleared right now.");
        }
    }

    @FXML
    private void openDashboard() {
        Advisor selectedAdvisor = advisorComboBox.getSelectionModel().getSelectedItem();
        if (selectedAdvisor == null) {
            messageLabel.setText("Select an advisor first.");
            return;
        }
        if (dashboardOpener != null) {
            try {
                dashboardOpener.accept(selectedAdvisor);
            } catch (RuntimeException exception) {
                messageLabel.setText("Dashboard could not be opened right now.");
            }
        }
    }

    private void advanceSimulation() {
        if (simulationControlService == null || !simulationControlService.isRunning()) {
            return;
        }
        try {
            simulationControlService.advanceOneTick();
            refreshSnapshot(null);
        } catch (RuntimeException exception) {
            messageLabel.setText("Simulation could not advance right now.");
            simulationControlService.stop();
        }
    }

    private void refreshSnapshot(String message) {
        if (simulationControlService == null) {
            return;
        }

        try {
            SimulationSnapshot snapshot = simulationControlService.getSnapshot();
            statusValueLabel.setText(snapshot.getStatus() == null ? SimulationStatus.STOPPED.name() : snapshot.getStatus().name());
            startedAtValueLabel.setText(formatTimestamp(snapshot.getStartedAt()));
            stoppedAtValueLabel.setText(formatTimestamp(snapshot.getStoppedAt()));
            updatedAtValueLabel.setText(formatTimestamp(snapshot.getLastUpdatedAt()));

            customersMetricLabel.setText(String.valueOf(snapshot.getMetrics().getCreatedCustomers()));
            journeysMetricLabel.setText(String.valueOf(snapshot.getMetrics().getAdvancedJourneys()));
            delayMetricLabel.setText(String.valueOf(snapshot.getMetrics().getGeneratedDelayIncidents()));
            onboardingMetricLabel.setText(String.valueOf(snapshot.getMetrics().getGeneratedOnboardingIncidents()));
            feedbackMetricLabel.setText(String.valueOf(snapshot.getMetrics().getGeneratedFeedbacks()));
            feedbackIncidentMetricLabel.setText(String.valueOf(snapshot.getMetrics().getGeneratedFeedbackIncidents()));

            if (message != null) {
                messageLabel.setText(message);
            }
        } catch (RuntimeException exception) {
            if (message != null) {
                messageLabel.setText(message);
            } else {
                messageLabel.setText("Simulation state could not be loaded right now.");
            }
        }
    }

    private SimulationConfig buildConfig() {
        return new SimulationConfig(
                parseInteger(customerIntervalField.getText(), "Customer creation interval"),
                parseInteger(journeyIntervalField.getText(), "Journey advancement interval"),
                parseDouble(delayProbabilityField.getText(), "Pre-flight delay probability"),
                parseInteger(minDelayField.getText(), "Minimum delay minutes"),
                parseInteger(maxDelayField.getText(), "Maximum delay minutes"),
                parseDouble(feedbackProbabilityField.getText(), "Low-score feedback probability"),
                parseInteger(lowScoreThresholdField.getText(), "Low-score threshold"),
                parseInteger(onboardingStuckThresholdField.getText(), "Onboarding stuck threshold"),
                parseDouble(onboardingProbabilityField.getText(), "Onboarding incident probability")
        );
    }

    private boolean shouldPrepareNewRun(SimulationSnapshot snapshot, SimulationConfig desiredConfig) {
        return snapshot.getConfig() == null
                || snapshot.getStartedAt() == null
                || !sameConfig(snapshot.getConfig(), desiredConfig);
    }

    private boolean sameConfig(SimulationConfig left, SimulationConfig right) {
        return left.getCustomerCreationIntervalSeconds() == right.getCustomerCreationIntervalSeconds()
                && left.getJourneyAdvanceIntervalSeconds() == right.getJourneyAdvanceIntervalSeconds()
                && Double.compare(left.getPreFlightDelayProbability(), right.getPreFlightDelayProbability()) == 0
                && left.getMinDelayMinutes() == right.getMinDelayMinutes()
                && left.getMaxDelayMinutes() == right.getMaxDelayMinutes()
                && Double.compare(left.getLowScoreFeedbackProbability(), right.getLowScoreFeedbackProbability()) == 0
                && left.getLowScoreThreshold() == right.getLowScoreThreshold()
                && left.getOnboardingStuckAfterSeconds() == right.getOnboardingStuckAfterSeconds()
                && Double.compare(left.getOnboardingIncidentProbability(), right.getOnboardingIncidentProbability()) == 0;
    }

    private int parseInteger(String value, String label) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception exception) {
            throw new IllegalArgumentException(label + " must be a whole number.");
        }
    }

    private double parseDouble(String value, String label) {
        try {
            return Double.parseDouble(value.trim());
        } catch (Exception exception) {
            throw new IllegalArgumentException(label + " must be a decimal number.");
        }
    }

    private String formatTimestamp(java.time.LocalDateTime value) {
        return value == null ? "n/a" : TIMESTAMP_FORMATTER.format(value);
    }
}
