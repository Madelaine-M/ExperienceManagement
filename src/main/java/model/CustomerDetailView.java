package model;

import model.enums.CustomerStatus;
import model.enums.CustomerType;
import model.enums.Packages;
import model.enums.PaymentMethod;

import java.util.ArrayList;
import java.util.List;

public class CustomerDetailView {
    private int customerId;
    private String customerFirstName;
    private String customerLastName;
    private String email;
    private CustomerStatus status;
    private String bookingDate;
    private boolean returning;
    private CustomerType customerType;
    private float cvScore;
    private List<CustomerNote> notes = new ArrayList<>();
    private String preferences;
    private String applyToNextBooking;
    private PaymentMethod paymentMethod;
    private boolean publicPerson;
    private boolean hasOpenIncident;
    private Integer openIncidentId;
    private Double highestPriorityScore;
    private String incidentDescription;
    private Packages bookingPackage;
    private Flight currentFlight;
    private List<Flight> previousFlightsList = new ArrayList<>();
    private String previousFlights;

    public CustomerDetailView() {
    }

    public CustomerDetailView(int customerId, String customerFirstName, String customerLastName, String email,
                              CustomerStatus status, String bookingDate, boolean returning,
                              CustomerType customerType, float cvScore, List<CustomerNote> notes, String preferences,
                              String applyToNextBooking, PaymentMethod paymentMethod, boolean publicPerson,
                              boolean hasOpenIncident, Integer openIncidentId, Double highestPriorityScore,
                              String incidentDescription, Packages bookingPackage, Flight currentFlight,
                              List<Flight> previousFlightsList, String previousFlights) {
        this.customerId = customerId;
        this.customerFirstName = customerFirstName;
        this.customerLastName = customerLastName;
        this.email = email;
        this.status = status;
        this.bookingDate = bookingDate;
        this.returning = returning;
        this.customerType = customerType;
        this.cvScore = cvScore;
        this.notes = notes != null ? new ArrayList<>(notes) : new ArrayList<>();
        this.preferences = preferences;
        this.applyToNextBooking = applyToNextBooking;
        this.paymentMethod = paymentMethod;
        this.publicPerson = publicPerson;
        this.hasOpenIncident = hasOpenIncident;
        this.openIncidentId = openIncidentId;
        this.highestPriorityScore = highestPriorityScore;
        this.incidentDescription = incidentDescription;
        this.bookingPackage = bookingPackage;
        this.currentFlight = currentFlight;
        this.previousFlightsList = previousFlightsList != null ? new ArrayList<>(previousFlightsList) : new ArrayList<>();
        this.previousFlights = previousFlights;
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

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public boolean isReturning() {
        return returning;
    }

    public void setReturning(boolean returning) {
        this.returning = returning;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    public float getCvScore() {
        return cvScore;
    }

    public void setCvScore(float cvScore) {
        this.cvScore = cvScore;
    }

    public List<CustomerNote> getNotes() {
        return notes;
    }

    public void setNotes(List<CustomerNote> notes) {
        this.notes = notes != null ? new ArrayList<>(notes) : new ArrayList<>();
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    public String getApplyToNextBooking() {
        return applyToNextBooking;
    }

    public void setApplyToNextBooking(String applyToNextBooking) {
        this.applyToNextBooking = applyToNextBooking;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public boolean isPublicPerson() {
        return publicPerson;
    }

    public void setPublicPerson(boolean publicPerson) {
        this.publicPerson = publicPerson;
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

    public Packages getBookingPackage() {
        return bookingPackage;
    }

    public void setBookingPackage(Packages bookingPackage) {
        this.bookingPackage = bookingPackage;
    }

    public Flight getCurrentFlight() {
        return currentFlight;
    }

    public void setCurrentFlight(Flight currentFlight) {
        this.currentFlight = currentFlight;
    }

    public List<Flight> getPreviousFlightsList() {
        return previousFlightsList;
    }

    public void setPreviousFlightsList(List<Flight> previousFlightsList) {
        this.previousFlightsList = previousFlightsList != null ? new ArrayList<>(previousFlightsList) : new ArrayList<>();
    }

    public String getPreviousFlights() {
        return previousFlights;
    }

    public void setPreviousFlights(String previousFlights) {
        this.previousFlights = previousFlights;
    }
}
