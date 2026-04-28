package ui.model;

import model.CustomerDetailView;
import model.CustomerNote;
import model.Feedback;
import model.FeedbackItem;
import model.Flight;
import model.IncidentDetailView;

import java.util.ArrayList;
import java.util.List;

public class JourneyDetailData {
    private final int customerId;
    private final Integer incidentId;
    private final String customerName;
    private final CustomerDetailView customerDetail;
    private final IncidentDetailView incidentDetail;
    private final Feedback feedback;
    private final FeedbackItem highlightedFeedbackItem;
    private final List<JourneyStepView> journeySteps;

    public JourneyDetailData(int customerId,
                             Integer incidentId,
                             String customerName,
                             CustomerDetailView customerDetail,
                             IncidentDetailView incidentDetail,
                             Feedback feedback,
                             FeedbackItem highlightedFeedbackItem,
                             List<JourneyStepView> journeySteps) {
        this.customerId = customerId;
        this.incidentId = incidentId;
        this.customerName = customerName;
        this.customerDetail = customerDetail;
        this.incidentDetail = incidentDetail;
        this.feedback = feedback;
        this.highlightedFeedbackItem = highlightedFeedbackItem;
        this.journeySteps = journeySteps != null ? new ArrayList<>(journeySteps) : new ArrayList<>();
    }

    public int getCustomerId() {
        return customerId;
    }

    public Integer getIncidentId() {
        return incidentId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public CustomerDetailView getCustomerDetail() {
        return customerDetail;
    }

    public IncidentDetailView getIncidentDetail() {
        return incidentDetail;
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public FeedbackItem getHighlightedFeedbackItem() {
        return highlightedFeedbackItem;
    }

    public List<JourneyStepView> getJourneySteps() {
        return new ArrayList<>(journeySteps);
    }

    public Flight getCurrentFlight() {
        return customerDetail == null ? null : customerDetail.getCurrentFlight();
    }

    public List<CustomerNote> getNotes() {
        return customerDetail == null ? List.of() : customerDetail.getNotes();
    }

    public boolean hasFeedbackContext() {
        return feedback != null;
    }
}
