package ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.ActionItem;
import ui.view.DashboardFormatters;
import ui.view.render.DashboardNodeFactory;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.IntConsumer;

public class ActionPanelController {
    @FXML
    private VBox root;
    @FXML
    private VBox contentBox;

    private final DashboardNodeFactory nodeFactory = new DashboardNodeFactory();

    public void showActions(List<ActionItem> actions,
                            int incidentId,
                            Set<Integer> selectedActionIds,
                            IntConsumer selectionHandler) {
        contentBox.getChildren().setAll(nodeFactory.createSectionTitle("Decision Support"));

        if (actions == null || actions.isEmpty()) {
            contentBox.getChildren().add(nodeFactory.createEmptyStateCard("No action items available for incident #" + incidentId + "."));
            return;
        }

        for (ActionItem actionItem : actions) {
            contentBox.getChildren().add(createActionCard(actionItem, selectedActionIds.contains(actionItem.getId()), selectionHandler));
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

    private VBox createActionCard(ActionItem actionItem, boolean selected, IntConsumer selectionHandler) {
        VBox card = nodeFactory.createCard("action-card");
        Label header = new Label("System Recommendation");
        header.getStyleClass().add("action-title");

        Label description = new Label(DashboardFormatters.defaultText(actionItem.getDescription(), "No recommendation summary."));
        description.getStyleClass().add("action-description");
        description.setWrapText(true);

        VBox recommendations = new VBox(12);
        recommendations.getChildren().add(createRecommendationOption(actionItem, 1, actionItem.getSugegstion1(), selected, selectionHandler));
        if (actionItem.getSuggestion2() != null && !actionItem.getSuggestion2().isBlank()) {
            recommendations.getChildren().add(createRecommendationOption(actionItem, 2, actionItem.getSuggestion2(), selected, selectionHandler));
        }

        card.getChildren().addAll(header, description, recommendations);
        return card;
    }

    private VBox createRecommendationOption(ActionItem actionItem,
                                            int optionNumber,
                                            String text,
                                            boolean selected,
                                            IntConsumer selectionHandler) {
        VBox optionCard = nodeFactory.createCard("recommendation-option");
        Label optionLabel = new Label("Option " + optionNumber);
        optionLabel.getStyleClass().add("recommendation-label");

        Label suggestion = new Label(optionNumber + ". " + DashboardFormatters.defaultText(text, "No suggestion available."));
        suggestion.getStyleClass().add("recommendation-text");
        suggestion.setWrapText(true);

        var button = nodeFactory.createActionButton(
                selected ? "Selected for follow-up" : "Select this recommendation",
                selected ? "secondary-button" : "primary-button",
                () -> selectionHandler.accept(actionItem.getId())
        );
        button.setDisable(selected);

        optionCard.getChildren().addAll(optionLabel, suggestion, button);
        return optionCard;
    }

    private VBox createImpactCard(ActionItem actionItem) {
        VBox card = nodeFactory.createCard("impact-card");
        card.getChildren().addAll(
                nodeFactory.createSectionLabel("Expected Impact"),
                nodeFactory.createMetricLine("Recommendation", String.format(Locale.ENGLISH, "%.2f", actionItem.getExpectedRec())),
                nodeFactory.createMetricLine("Book again", String.format(Locale.ENGLISH, "%.2f", actionItem.getExpectedRebooking())),
                nodeFactory.createMetricLine("Score impact", String.format(Locale.ENGLISH, "%.2f", actionItem.getScoreImpact()))
        );
        return card;
    }
}
