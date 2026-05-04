package ui.controller;

import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Window;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import model.domain.ActionItem;
import model.workflow.RecommendationEmailDraft;
import ui.view.DashboardFormatters;
import ui.view.render.DashboardNodeFactory;
import ui.service.DashboardDataService;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

public class ActionPanelController {
    @FXML
    private VBox root;
    @FXML
    private VBox contentBox;

    private final DashboardNodeFactory nodeFactory = new DashboardNodeFactory();

    public void showActions(List<ActionItem> actions,
                            int incidentId,
                            DashboardDataService dashboardDataService,
                            Runnable recommendationSentHandler) {
        contentBox.getChildren().setAll(nodeFactory.createSectionTitle("Decision Support"));

        if (actions == null || actions.isEmpty()) {
            contentBox.getChildren().add(nodeFactory.createEmptyStateCard("No action items available for incident #" + incidentId + "."));
            return;
        }

        for (ActionItem actionItem : actions) {
            contentBox.getChildren().add(createActionCard(actionItem, dashboardDataService, recommendationSentHandler));
        }
        contentBox.getChildren().add(createImpactCard(actions.get(0)));
    }

    public void showEmptyState(String message) {
        contentBox.getChildren().setAll(
                nodeFactory.createSectionTitle("Decision Support"),
                nodeFactory.createEmptyStateCard(message)
        );
    }

    public VBox getRoot() {
        return root;
    }

    private VBox createActionCard(ActionItem actionItem,
                                  DashboardDataService dashboardDataService,
                                  Runnable recommendationSentHandler) {
        VBox card = nodeFactory.createCard("action-card");
        Label header = new Label("System Recommendation");
        header.getStyleClass().add("action-title");

        Label description = new Label(DashboardFormatters.defaultText(actionItem.getDescription(), "No recommendation summary."));
        description.getStyleClass().add("action-description");
        description.setWrapText(true);

        VBox recommendations = new VBox(12);
        recommendations.getChildren().add(createRecommendationOption(actionItem, 1, actionItem.getSuggestion1(),
                dashboardDataService, recommendationSentHandler));
        if (actionItem.getSuggestion2() != null && !actionItem.getSuggestion2().isBlank()) {
            recommendations.getChildren().add(createRecommendationOption(actionItem, 2, actionItem.getSuggestion2(),
                    dashboardDataService, recommendationSentHandler));
        }

        card.getChildren().addAll(header, description, recommendations);
        return card;
    }

    private VBox createRecommendationOption(ActionItem actionItem,
                                            int optionNumber,
                                            String text,
                                            DashboardDataService dashboardDataService,
                                            Runnable recommendationSentHandler) {
        VBox optionCard = nodeFactory.createCard("recommendation-option");
        boolean alreadySent = dashboardDataService.isRecommendationOptionSent(actionItem.getIncidentId(), optionNumber);
        if (alreadySent) {
            optionCard.getStyleClass().add("recommendation-option-sent");
        }

        Label optionLabel = new Label("Option " + optionNumber);
        optionLabel.getStyleClass().add("recommendation-label");

        Label suggestion = new Label(optionNumber + ". " + DashboardFormatters.defaultText(text, "No suggestion available."));
        suggestion.getStyleClass().add("recommendation-text");
        suggestion.setWrapText(true);

        var button = nodeFactory.createActionButton(
                isInternalTeamSuggestion(text) ? "Compose team mail" : "Compose recovery mail",
                "primary-button",
                () -> openRecommendationDialog(actionItem.getId(), optionNumber, dashboardDataService, recommendationSentHandler)
        );
        button.setDisable(alreadySent);

        optionCard.getChildren().addAll(optionLabel, suggestion);
        if (alreadySent) {
            Label sentLabel = new Label("Mail sent");
            sentLabel.getStyleClass().add("recommendation-sent-label");
            optionCard.getChildren().add(sentLabel);
        }
        optionCard.getChildren().add(button);
        return optionCard;
    }

    private VBox createImpactCard(ActionItem actionItem) {
        VBox card = nodeFactory.createCard("impact-card");
        card.getChildren().addAll(
                nodeFactory.createSectionLabel("Expected Impact"),
                nodeFactory.createMetricLine("Recommendation", String.valueOf(actionItem.getExpectedRec())),
                nodeFactory.createMetricLine("Book again", String.valueOf(actionItem.getExpectedRebooking())),
                nodeFactory.createMetricLine("Score impact", String.valueOf(actionItem.getScoreImpact()))
        );
        return card;
    }

    private boolean isInternalTeamSuggestion(String text) {
        String normalizedText = text == null ? "" : text.toLowerCase();
        return normalizedText.contains("driver")
                || normalizedText.contains("detour")
                || normalizedText.contains("lunch")
                || normalizedText.contains("onboarding team");
    }

    private void openRecommendationDialog(int actionId,
                                          int optionNumber,
                                          DashboardDataService dashboardDataService,
                                          Runnable recommendationSentHandler) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/view/RecommendationEmailView.fxml"));
            Parent dialogRoot = loader.load();
            RecommendationEmailController controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            Window ownerWindow = root != null && root.getScene() != null ? root.getScene().getWindow() : null;
            if (ownerWindow != null) {
                dialogStage.initOwner(ownerWindow);
            }

            Scene scene = new Scene(dialogRoot, 720, 620);
            scene.getStylesheets().add(ActionPanelController.class.getResource("/ui/view/dashboard.css").toExternalForm());
            dialogStage.setScene(scene);
            RecommendationEmailDraft draft = dashboardDataService.prepareRecommendationDraft(actionId, optionNumber);
            dialogStage.setTitle(draft.getDialogTitle());

            controller.configure(
                    draft,
                    dashboardDataService,
                    recommendationSentHandler,
                    dialogStage
            );

            dialogStage.showAndWait();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to open recommendation email dialog.", e);
        } catch (RuntimeException exception) {
            showRecommendationError("Recommendation mail could not be opened right now.");
        }
    }

    private void showRecommendationError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Action Unavailable");
        alert.setHeaderText("Recommendation action failed");
        alert.setContentText(message);
        Window ownerWindow = root != null && root.getScene() != null ? root.getScene().getWindow() : null;
        if (ownerWindow != null) {
            alert.initOwner(ownerWindow);
        }
        alert.showAndWait();
    }
}
