package model;

import model.enums.CustomerType;
import model.enums.IncidentType;
import model.enums.Packages;

import java.util.ArrayList;
import java.util.List;

public class IncidentDetailView {
    private int incidentId;
    private int customerId;
    private String customerFirstName;
    private String customerLastName;
    private Packages bookingPackage;
    private boolean returning;
    private CustomerType customerType;
    private String previousFlights;
    private int currentFlightId;
    private String currentFlightDate;
    private String currentFlightNumber;
    private IncidentType incidentType;
    private String incidentDescription;
    private List<Flight> previousFlightsList = new ArrayList<>();

    public IncidentDetailView() {
    }

    public IncidentDetailView(int incidentId, int customerId, String customerFirstName, String customerLastName,
                              Packages bookingPackage, boolean returning, CustomerType customerType,
                              String previousFlights, int currentFlightId, String currentFlightDate,
                              String currentFlightNumber, IncidentType incidentType, String incidentDescription,
                              List<Flight> previousFlightsList) {
        this.incidentId = incidentId;
        this.customerId = customerId;
        this.customerFirstName = customerFirstName;
        this.customerLastName = customerLastName;
        this.bookingPackage = bookingPackage;
        this.returning = returning;
        this.customerType = customerType;
        this.previousFlights = previousFlights;
        this.currentFlightId = currentFlightId;
        this.currentFlightDate = currentFlightDate;
        this.currentFlightNumber = currentFlightNumber;
        this.incidentType = incidentType;
        this.incidentDescription = incidentDescription;
        this.previousFlightsList = previousFlightsList != null ? new ArrayList<>(previousFlightsList) : new ArrayList<>();
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

    public String getPreviousFlights() {
        return previousFlights;
    }

    public void setPreviousFlights(String previousFlights) {
        this.previousFlights = previousFlights;
    }

    public int getCurrentFlightId() {
        return currentFlightId;
    }

    public void setCurrentFlightId(int currentFlightId) {
        this.currentFlightId = currentFlightId;
    }

    public String getCurrentFlightDate() {
        return currentFlightDate;
    }

    public void setCurrentFlightDate(String currentFlightDate) {
        this.currentFlightDate = currentFlightDate;
    }

    public String getCurrentFlightNumber() {
        return currentFlightNumber;
    }

    public void setCurrentFlightNumber(String currentFlightNumber) {
        this.currentFlightNumber = currentFlightNumber;
    }

    public IncidentType getIncidentType() {
        return incidentType;
    }

    public void setIncidentType(IncidentType incidentType) {
        this.incidentType = incidentType;
    }

    public String getIncidentDescription() {
        return incidentDescription;
    }

    public void setIncidentDescription(String incidentDescription) {
        this.incidentDescription = incidentDescription;
    }

    public List<Flight> getPreviousFlightsList() {
        return previousFlightsList;
    }

    public void setPreviousFlightsList(List<Flight> previousFlightsList) {
        this.previousFlightsList = previousFlightsList != null ? new ArrayList<>(previousFlightsList) : new ArrayList<>();
    }
}
