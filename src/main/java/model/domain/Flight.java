package model.domain;

import model.enums.Packages;

public class Flight {
    private int id;
    private int customerId;
    private String flightNumber;
    private String bookingDate;
    private String flightDate;
    private Packages bookingPackage;
    private String status;
    private boolean current;

    public Flight() {
    }

    public Flight(int id, int customerId, String flightNumber, String bookingDate, String flightDate,
                  Packages bookingPackage, String status, boolean current) {
        this.id = id;
        this.customerId = customerId;
        this.flightNumber = flightNumber;
        this.bookingDate = bookingDate;
        this.flightDate = flightDate;
        this.bookingPackage = bookingPackage;
        this.status = status;
        this.current = current;
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

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
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

    public Packages getBookingPackage() {
        return bookingPackage;
    }

    public void setBookingPackage(Packages bookingPackage) {
        this.bookingPackage = bookingPackage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }
}
