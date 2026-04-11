package model;

import java.time.LocalDateTime;

public class SentimentHistory {
    private int id;
    private int customerId;
    private LocalDateTime recordedAt;
    private double clvScore;
    private double scoreImpact;
    private int feedbackId; // noch in DB ergenzen und als Fremdschlüsselk mit feedback DB verknüfen dann

    public SentimentHistory() {
    }

    public SentimentHistory(int customerId, LocalDateTime recordedAt, double clvScore, double scoreImpact) {
        this.customerId = customerId;
        this.recordedAt = recordedAt;
        this.clvScore = clvScore;
        this.scoreImpact = scoreImpact;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }

    public double getClvScore() {
        return clvScore;
    }

    public void setClvScore(double clvScore) {
        this.clvScore = clvScore;
    }

    public double getScoreImpact() {
        return scoreImpact;
    }

    public void setScoreImpact(double scoreImpact) {
        this.scoreImpact = scoreImpact;
    }

    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }
}
