package model.workflow;

import java.time.LocalDateTime;

public class RecoveryActionSummary {
    private final int advisorId;
    private final String advisorName;
    private final LocalDateTime sentAt;
    private final int optionNumber;
    private final String selectedRecommendation;
    private final String subject;
    private final String mailBody;

    public RecoveryActionSummary(int advisorId,
                                 String advisorName,
                                 LocalDateTime sentAt,
                                 int optionNumber,
                                 String selectedRecommendation,
                                 String subject,
                                 String mailBody) {
        this.advisorId = advisorId;
        this.advisorName = advisorName;
        this.sentAt = sentAt;
        this.optionNumber = optionNumber;
        this.selectedRecommendation = selectedRecommendation;
        this.subject = subject;
        this.mailBody = mailBody;
    }

    public int getAdvisorId() {
        return advisorId;
    }

    public String getAdvisorName() {
        return advisorName;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public int getOptionNumber() {
        return optionNumber;
    }

    public String getSelectedRecommendation() {
        return selectedRecommendation;
    }

    public String getSubject() {
        return subject;
    }

    public String getMailBody() {
        return mailBody;
    }
}
