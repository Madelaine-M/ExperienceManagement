package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Feedback {
    private int id;
    private int customerId;
    private LocalDateTime createdAt;
    private List<FeedbackItem> items;

    public Feedback() {
        this.items = new ArrayList<>();
    }

    public Feedback(int customerId, LocalDateTime createdAt, List<FeedbackItem> items) {
        this.customerId = customerId;
        this.createdAt = createdAt;
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<FeedbackItem> getItems() {
        return items;
    }

    public void setItems(List<FeedbackItem> items) {
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
    }

    public double getOverallScore() {
        if (items == null || items.isEmpty()) {
            return 0.0;
        }

        int totalScore = 0;
        for (FeedbackItem item : items) {
            totalScore += item.getScore();
        }

        return (double) totalScore / items.size();
    }
}
