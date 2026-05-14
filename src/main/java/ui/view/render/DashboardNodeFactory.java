package ui.view.render;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

// AI used
public class DashboardNodeFactory {

    public Label createSectionTitle(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("section-title");
        return label;
    }

    public Label createSectionLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("card-title");
        return label;
    }

    public Node createDetailLine(String label, String value) {
        HBox line = new HBox(8);
        Label key = new Label(label + ":");
        key.getStyleClass().add("metric-key");
        Label content = new Label(value);
        content.getStyleClass().add("metric-value-accent");
        line.getChildren().addAll(key, content);
        return line;
    }

    public Node createMetricLine(String label, String value) {
        HBox line = new HBox(10);
        line.getStyleClass().add("metric-line");
        Label key = new Label(label + ":");
        key.getStyleClass().add("metric-key");
        Label content = new Label(value);
        content.getStyleClass().add("metric-value");
        content.setWrapText(true);
        HBox.setHgrow(content, Priority.ALWAYS);
        line.getChildren().addAll(key, content);
        return line;
    }

    public VBox createCard(String styleClass) {
        VBox box = new VBox(14);
        box.getStyleClass().add(styleClass);
        return box;
    }

    public Node createInfoCard(String title, String body) {
        VBox card = createCard("detail-card");
        Label titleLabel = createSectionLabel(title);
        Label bodyLabel = new Label(body);
        bodyLabel.getStyleClass().add("body-text");
        bodyLabel.setWrapText(true);
        card.getChildren().addAll(titleLabel, bodyLabel);
        return card;
    }

    public Node createEmptyStateCard(String message) {
        StackPane pane = new StackPane();
        pane.getStyleClass().add("empty-card");
        pane.setPadding(new Insets(40));
        Label label = new Label(message);
        label.getStyleClass().add("empty-text");
        label.setWrapText(true);
        pane.getChildren().add(label);
        return pane;
    }

    public Node createInfoChip(String text) {
        Label chip = new Label(text);
        chip.getStyleClass().add("info-chip");
        return chip;
    }

    public Node createHeroCard(String title, String initials, String modeLabel, Node... detailLines) {
        HBox card = new HBox(18);
        card.getStyleClass().add("hero-card");
        card.setAlignment(Pos.CENTER_LEFT);

        StackPane avatarPanel = new StackPane();
        avatarPanel.getStyleClass().add("avatar-panel");
        avatarPanel.setMinSize(120, 120);
        avatarPanel.setPrefSize(120, 120);
        Label avatar = new Label(initials);
        avatar.getStyleClass().add("avatar-text");
        avatarPanel.getChildren().add(avatar);

        VBox content = new VBox(10);
        Label nameLabel = new Label(title);
        nameLabel.getStyleClass().add("detail-title-accent");
        nameLabel.setWrapText(true);

        VBox details = new VBox(12);
        details.getChildren().addAll(detailLines);

        content.getChildren().addAll(nameLabel, details);
        HBox.setHgrow(content, Priority.ALWAYS);
        card.getChildren().addAll(avatarPanel, content);
        return card;
    }

    public Button createActionButton(String text, String styleClass, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().add(styleClass);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setWrapText(true);
        button.setOnAction(event -> action.run());
        return button;
    }
}
