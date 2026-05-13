package model.domain;

import model.enums.CustomerStatus;

import java.time.LocalDateTime;

public class Customer {

    private String firstName;
    private String lastName;
    private String email;
    private String birthDate;
    private CustomerStatus status = CustomerStatus.BOOKED;
    private boolean returning;
    private Integer assignedAdvisorId;
    private String preferences = "";
    private String applyToNextBooking = "";
    private LocalDateTime statusUpdatedAt;

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public void setStatus(CustomerStatus status) {
        this.status = status;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public boolean isReturning() {
        return returning;
    }

    public void setReturning(boolean returning) {
        this.returning = returning;
    }

    public Integer getAssignedAdvisorId() {
        return assignedAdvisorId;
    }

    public void setAssignedAdvisorId(Integer assignedAdvisorId) {
        this.assignedAdvisorId = assignedAdvisorId;
    }

    public String getApplyToNextBooking() {
        return applyToNextBooking;
    }

    public void setApplyToNextBooking(String applyToNextBooking) {
        this.applyToNextBooking = applyToNextBooking;
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    public LocalDateTime getStatusUpdatedAt() {
        return statusUpdatedAt;
    }

    public void setStatusUpdatedAt(LocalDateTime statusUpdatedAt) {
        this.statusUpdatedAt = statusUpdatedAt;
    }

    public Customer() {}

    public Customer(String firstName, String lastName, String email, String birthDate, CustomerStatus status,
                    boolean returning, Integer assignedAdvisorId,
                    String preferences, String applyToNextBooking) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthDate = birthDate;
        this.status = status;
        this.returning = returning;
        this.assignedAdvisorId = assignedAdvisorId;
        this.preferences = preferences;
        this.applyToNextBooking = applyToNextBooking;
        this.statusUpdatedAt = LocalDateTime.now();
    }

}
