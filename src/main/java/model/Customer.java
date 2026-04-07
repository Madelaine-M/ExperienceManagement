package model;

import model.enums.CustomerStatus;
import model.enums.Packages;

public class Customer {

    private String firstName;
    private String lastName;
    private String email;
    private String birthDate;
    private CustomerStatus status;
    private Packages bookingPackage;
    private String bookingDate;
    private String flightDate;
    private boolean returning;
    private int assignedAdvisorId;
    private float clvScore;

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

    public Packages getBookingPackage() {
        return bookingPackage;
    }

    public void setBookingPackage(Packages bookingPackage) {
        this.bookingPackage = bookingPackage;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getFlightDate() {
        return flightDate;
    }

    public void setFlightDate(String flightDate) {
        this.flightDate = flightDate;
    }

    public boolean isReturning() {
        return returning;
    }

    public void setReturning(boolean returning) {
        this.returning = returning;
    }

    public int getAssignedAdvisorId() {
        return assignedAdvisorId;
    }

    public void setAssignedAdvisorId(int assignedAdvisorId) {
        this.assignedAdvisorId = assignedAdvisorId;
    }

    public float getClvScore() {
        return clvScore;
    }

    public void setClvScore(float clv_score) {
        this.clvScore= clv_score;
    }

    // Leerer Konstruktor (wichtig für später)
    public Customer() {}

    // Konstruktor für schnelles Erstellen (z.B. für Testdaten)
    public Customer(String firstName, String lastName, String email, CustomerStatus status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.status = status;
    }

}
