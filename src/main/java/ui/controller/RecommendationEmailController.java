package ui.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.workflow.RecommendationEmailDraft;
import ui.service.DashboardDataService;

public class RecommendationEmailController {
    private static final String MAIL_INPUT_STYLE =
            "-fx-control-inner-background: #1b2942;"
                    + "-fx-background-color: #1b2942;"
                    + "-fx-text-fill: #f8fafc;"
                    + "-fx-text-inner-color: #f8fafc;"
                    + "-fx-highlight-fill: #2764ff;"
                    + "-fx-highlight-text-fill: #f8fafc;";
    private static final String MAIL_CONTENT_STYLE =
            "-fx-background-color: #1b2942;"
                    + "-fx-control-inner-background: #1b2942;";
    private static final String MAIL_TEXT_STYLE = "-fx-fill: #f8fafc;";

    @FXML
    private Label titleLabel;
    @FXML
    private Label primaryKeyLabel;
    @FXML
    private Label customerValueLabel;
    @FXML
    private Label secondaryKeyLabel;
    @FXML
    private Label advisorValueLabel;
    @FXML
    private Label suggestionValueLabel;
    @FXML
    private Label errorLabel;
    @FXML
    private TextField subjectField;
    @FXML
    private TextArea bodyArea;
    @FXML
    private javafx.scene.control.Button sendButton;

    private RecommendationEmailDraft draft;
    private DashboardDataService dashboardDataService;
    private Runnable recommendationSentHandler;
    private Stage dialogStage;

    public void configure(RecommendationEmailDraft draft,
                          DashboardDataService dashboardDataService,
                          Runnable recommendationSentHandler,
                          Stage dialogStage) {
        this.draft = draft;
        this.dashboardDataService = dashboardDataService;
        this.recommendationSentHandler = recommendationSentHandler;
        this.dialogStage = dialogStage;

        titleLabel.setText(draft.getDialogTitle());
        primaryKeyLabel.setText(draft.getPrimaryLabel());
        customerValueLabel.setText(draft.getPrimaryValue());
        secondaryKeyLabel.setText(draft.getSecondaryLabel());
        advisorValueLabel.setText(draft.getAdvisorName());
        suggestionValueLabel.setText(draft.getSelectedSuggestion());
        sendButton.setText(draft.getSendButtonText());
        subjectField.setText(draft.getSubject());
        bodyArea.setText(draft.getBody());
        subjectField.setStyle(MAIL_INPUT_STYLE);
        bodyArea.setStyle(MAIL_INPUT_STYLE);
        Platform.runLater(this::applyInputSkinStyles);
        errorLabel.setText("");
    }

    @FXML
    private void sendEmail() {
        try {
            dashboardDataService.executeRecommendation(
                    draft.getActionId(),
                    draft.getOptionNumber(),
                    subjectField.getText(),
                    bodyArea.getText()
            );
            if (recommendationSentHandler != null) {
                recommendationSentHandler.run();
            }
            closeDialog();
        } catch (RuntimeException exception) {
            errorLabel.setText(exception.getMessage());
        }
    }

    @FXML
    private void closeDialog() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    private void applyInputSkinStyles() {
        applyStyleIfPresent(subjectField.lookup(".content"), MAIL_CONTENT_STYLE);
        applyStyleIfPresent(bodyArea.lookup(".content"), MAIL_CONTENT_STYLE);
        applyStyleIfPresent(subjectField.lookup(".text"), MAIL_TEXT_STYLE);
        applyStyleIfPresent(bodyArea.lookup(".text"), MAIL_TEXT_STYLE);
    }

    private void applyStyleIfPresent(Node node, String style) {
        if (node != null) {
            node.setStyle(style);
        }
    }
}
