package model.view;

import model.enums.IncidentType;
import model.enums.Packages;

import java.time.LocalDateTime;

public class IncidentOverview {
    private int incidentId;
    private int customerId;
    private String customerFirstName;
    private String customerLastName;
    private Packages bookingPackage;
    private IncidentType incidentType;
    private float customerCvScore;
    private double priorityScore;
    private String description;
    private int revenueRisk;
    private double scoreImpact;
    private LocalDateTime createdAt;

    public IncidentOverview() {
    }

    public IncidentOverview(int incidentId, int customerId, String customerFirstName, String customerLastName,
                            Packages bookingPackage, double priorityScore, String description, int revenueRisk,
                            double scoreImpact) {
        this.incidentId = incidentId;
        this.customerId = customerId;
        this.customerFirstName = customerFirstName;
        this.customerLastName = customerLastName;
        this.bookingPackage = bookingPackage;
        this.priorityScore = priorityScore;
        this.description = description;
        this.revenueRisk = revenueRisk;
        this.scoreImpact = scoreImpact;
    }

    public int getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(int incidentId) {
        this.incidentId = incidentId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public void setCustomerLastName(String customerLastName) {
        this.customerLastName = customerLastName;
    }

    public Packages getBookingPackage() {
        return bookingPackage;
    }

    public void setBookingPackage(Packages bookingPackage) {
        this.bookingPackage = bookingPackage;
    }

    public IncidentType getIncidentType() {
        return incidentType;
    }

    public void setIncidentType(IncidentType incidentType) {
        this.incidentType = incidentType;
    }

    public float getCustomerCvScore() {
        return customerCvScore;
    }

    public void setCustomerCvScore(float customerCvScore) {
        this.customerCvScore = customerCvScore;
    }

    public double getPriorityScore() {
        return priorityScore;
    }

    public void setPriorityScore(double priorityScore) {
        this.priorityScore = priorityScore;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRevenueRisk() {
        return revenueRisk;
    }

    public void setRevenueRisk(int revenueRisk) {
        this.revenueRisk = revenueRisk;
    }

    public double getScoreImpact() {
        return scoreImpact;
    }

    public void setScoreImpact(double scoreImpact) {
        this.scoreImpact = scoreImpact;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
