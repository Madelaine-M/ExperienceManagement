package ui.model;

import model.domain.Feedback;
import model.domain.Flight;

import java.util.ArrayList;
import java.util.List;

public class FlightDetailData {
    private final Flight flight;
    private final String customerName;
    private final List<Feedback> feedbacks;

    public FlightDetailData(Flight flight, String customerName, List<Feedback> feedbacks) {
        this.flight = flight;
        this.customerName = customerName;
        this.feedbacks = feedbacks != null ? new ArrayList<>(feedbacks) : new ArrayList<>();
    }

    public Flight getFlight() {
        return flight;
    }

    public String getCustomerName() {
        return customerName;
    }

    public List<Feedback> getFeedbacks() {
        return new ArrayList<>(feedbacks);
    }
}
