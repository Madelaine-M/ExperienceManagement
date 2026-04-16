package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Feedback {
    private int id;
    private int customerId;
    private LocalDateTime createdAt;
    private List<FeedbackItem> items;
    private double totalScore;
    private int customerSatScore;
    private int referralScore; //noch in DB und getter setter
    private int flightId;

    public Feedback() {
        this.items = new ArrayList<>();
    }

    public Feedback(int id, int customerId, LocalDateTime createdAt, List<FeedbackItem> items, double totalScore,
                    int customerSatScore, int referralScore, int flightId) {
        this.id = id;
        this.customerId = customerId;
        this.createdAt = createdAt;
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        this.totalScore = totalScore;
        this.customerSatScore = customerSatScore;
        this.referralScore = referralScore;
        this.flightId = flightId;
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

    public int getCustomerSatScore() {
        return customerSatScore;
    }

    public void setCustomerSatScore(int customerSatScore) {
        this.customerSatScore = customerSatScore;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }
}
