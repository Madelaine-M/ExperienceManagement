package model.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Feedback {
    private int id;
    private int customerId;
    private LocalDateTime createdAt;
    private List<FeedbackItem> items;
    private int totalScore;
    private int customerSatScore; // weiß nciht ob das nciht rauas kann
    private int referralScore; //noch in DB und getter setter
    private Integer flightId;

    public Feedback() {
        this.items = new ArrayList<>();
    }

    public Feedback(int customerId, LocalDateTime createdAt, List<FeedbackItem> items, int totalScore,
                    int customerSatScore, int referralScore, Integer flightId) {
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

    public int getOverallScore() {
        if (items == null || items.isEmpty()) {
            return 0;
        }

        int totalScore = 0;
        for (FeedbackItem item : items) {
            totalScore += item.getScore();
        }

        return Math.round((float) totalScore / items.size());
    }

    public int getCustomerSatScore() {
        return customerSatScore;
    }

    public void setCustomerSatScore(int customerSatScore) {
        this.customerSatScore = customerSatScore;
    }

    public int getReferralScore() {
        return referralScore;
    }

    public void setReferralScore(int referralScore) {
        this.referralScore = referralScore;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public Integer getFlightId() {
        return flightId;
    }

    public void setFlightId(Integer flightId) {
        this.flightId = flightId;
    }
}
