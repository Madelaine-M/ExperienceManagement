package model;

import model.enums.FeedbackCategory;

public class FeedbackItem {
    private int id;
    private int feedbackId;
    private FeedbackCategory category;
    private int score;
    private String comment;

    public FeedbackItem() {
    }

    public FeedbackItem(FeedbackCategory category, int score, String comment) {
        this.category = category;
        this.score = score;
        this.comment = comment;
    }

    public FeedbackItem(int feedbackId, FeedbackCategory category, int score, String comment) {
        this.feedbackId = feedbackId;
        this.category = category;
        this.score = score;
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }

    public FeedbackCategory getCategory() {
        return category;
    }

    public void setCategory(FeedbackCategory category) {
        this.category = category;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
