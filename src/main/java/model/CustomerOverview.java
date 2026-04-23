package model;

import model.enums.CustomerStatus;
import model.enums.CustomerType;
import model.enums.Packages;

public class CustomerOverview {
    private int customerId;
    private String customerFirstName;
    private String customerLastName;
    private CustomerStatus status;
    private CustomerType customerType;
    private Packages bookingPackage;
    private float clvScore; //umbennen zu cv score
    private boolean returning;
    private boolean hasOpenIncident;
    private Integer openIncidentId;
    private Double highestPriorityScore;
    private String incidentDescription;

    public CustomerOverview() {
    }

    public CustomerOverview(int customerId, String customerFirstName, String customerLastName, CustomerStatus status,
                            CustomerType customerType, Packages bookingPackage, float clvScore, boolean returning,
                            boolean hasOpenIncident, Integer openIncidentId, Double highestPriorityScore,
                            String incidentDescription) {
        this.customerId = customerId;
        this.customerFirstName = customerFirstName;
        this.customerLastName = customerLastName;
        this.status = status;
        this.customerType = customerType;
        this.bookingPackage = bookingPackage;
        this.clvScore = clvScore;
        this.returning = returning;
        this.hasOpenIncident = hasOpenIncident;
        this.openIncidentId = openIncidentId;
        this.highestPriorityScore = highestPriorityScore;
        this.incidentDescription = incidentDescription;
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

    public CustomerStatus getStatus() {
        return status;
    }

    public void setStatus(CustomerStatus status) {
        this.status = status;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    public Packages getBookingPackage() {
        return bookingPackage;
    }

    public void setBookingPackage(Packages bookingPackage) {
        this.bookingPackage = bookingPackage;
    }

    public float getClvScore() {
        return clvScore;
    }

    public void setClvScore(float clvScore) {
        this.clvScore = clvScore;
    }

    public boolean isReturning() {
        return returning;
    }

    public void setReturning(boolean returning) {
        this.returning = returning;
    }

    public boolean isHasOpenIncident() {
        return hasOpenIncident;
    }

    public boolean hasOpenIncident() {
        return hasOpenIncident;
    }

    public void setHasOpenIncident(boolean hasOpenIncident) {
        this.hasOpenIncident = hasOpenIncident;
    }

    public Integer getOpenIncidentId() {
        return openIncidentId;
    }

    public void setOpenIncidentId(Integer openIncidentId) {
        this.openIncidentId = openIncidentId;
    }

    public Double getHighestPriorityScore() {
        return highestPriorityScore;
    }

    public void setHighestPriorityScore(Double highestPriorityScore) {
        this.highestPriorityScore = highestPriorityScore;
    }

    public String getIncidentDescription() {
        return incidentDescription;
    }

    public void setIncidentDescription(String incidentDescription) {
        this.incidentDescription = incidentDescription;
    }
}
