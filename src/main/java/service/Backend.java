package service;

import repository.interfaces.ActionLookup;
import repository.interfaces.ActionUpdate;
import repository.interfaces.AdvisorRepository;
import repository.interfaces.CustomerCvProfileLookup;
import repository.interfaces.CustomerCvProfileUpdate;
import repository.interfaces.CustomerLookup;
import repository.interfaces.CustomerNoteLookup;
import repository.interfaces.CustomerNoteUpdate;
import repository.interfaces.CustomerSearch;
import repository.interfaces.CustomerUpdate;
import repository.interfaces.CustomerView;
import repository.interfaces.FeedbackAnalytics;
import repository.interfaces.FeedbackLookup;
import repository.interfaces.FeedbackUpdate;
import repository.interfaces.FlightRepository;
import repository.interfaces.IncidentLookup;
import repository.interfaces.IncidentManagement;
import repository.interfaces.IncidentUpdate;
import repository.interfaces.IncidentView;
import repository.interfaces.NPSScores;
import service.interfaces.internal.CustomerCvScoreService;

public class Backend {
    private final ActionLookup actionLookup;
    private final ActionUpdate actionUpdate;
    private final AdvisorRepository advisorRepository;
    private final CustomerCvProfileLookup customerCvProfileLookup;
    private final CustomerCvProfileUpdate customerCvProfileUpdate;
    private final CustomerLookup customerLookup;
    private final CustomerNoteLookup customerNoteLookup;
    private final CustomerNoteUpdate customerNoteUpdate;
    private final CustomerSearch customerSearch;
    private final CustomerUpdate customerUpdate;
    private final CustomerView customerView;
    private final CustomerCvScoreService customerCvScoreService;
    private final FeedbackAnalytics feedbackAnalytics;
    private final FeedbackLookup feedbackLookup;
    private final FeedbackUpdate feedbackUpdate;
    private final NPSScores npsScores;
    private final FlightRepository flightRepository;
    private final IncidentLookup incidentLookup;
    private final IncidentManagement incidentManagement;
    private final IncidentUpdate incidentUpdate;
    private final IncidentView incidentView;

    public Backend(ActionLookup actionLookup,
                   ActionUpdate actionUpdate,
                   AdvisorRepository advisorRepository,
                   CustomerCvProfileLookup customerCvProfileLookup,
                   CustomerCvProfileUpdate customerCvProfileUpdate,
                   CustomerLookup customerLookup,
                   CustomerNoteLookup customerNoteLookup,
                   CustomerNoteUpdate customerNoteUpdate,
                   CustomerSearch customerSearch,
                   CustomerUpdate customerUpdate,
                   CustomerView customerView,
                   CustomerCvScoreService customerCvScoreService,
                   FeedbackAnalytics feedbackAnalytics,
                   FeedbackLookup feedbackLookup,
                   FeedbackUpdate feedbackUpdate,
                   NPSScores npsScores,
                   FlightRepository flightRepository,
                   IncidentLookup incidentLookup,
                   IncidentManagement incidentManagement,
                   IncidentUpdate incidentUpdate,
                   IncidentView incidentView) {
        this.actionLookup = actionLookup;
        this.actionUpdate = actionUpdate;
        this.advisorRepository = advisorRepository;
        this.customerCvProfileLookup = customerCvProfileLookup;
        this.customerCvProfileUpdate = customerCvProfileUpdate;
        this.customerLookup = customerLookup;
        this.customerNoteLookup = customerNoteLookup;
        this.customerNoteUpdate = customerNoteUpdate;
        this.customerSearch = customerSearch;
        this.customerUpdate = customerUpdate;
        this.customerView = customerView;
        this.customerCvScoreService = customerCvScoreService;
        this.feedbackAnalytics = feedbackAnalytics;
        this.feedbackLookup = feedbackLookup;
        this.feedbackUpdate = feedbackUpdate;
        this.npsScores = npsScores;
        this.flightRepository = flightRepository;
        this.incidentLookup = incidentLookup;
        this.incidentManagement = incidentManagement;
        this.incidentUpdate = incidentUpdate;
        this.incidentView = incidentView;
    }

    public ActionLookup getActionLookup() {
        return actionLookup;
    }

    public ActionUpdate getActionUpdate() {
        return actionUpdate;
    }

    public AdvisorRepository getAdvisorRepository() {
        return advisorRepository;
    }

    public CustomerLookup getCustomerLookup() {
        return customerLookup;
    }

    public CustomerCvProfileLookup getCustomerCvProfileLookup() {
        return customerCvProfileLookup;
    }

    public CustomerCvProfileUpdate getCustomerCvProfileUpdate() {
        return customerCvProfileUpdate;
    }

    public CustomerSearch getCustomerSearch() {
        return customerSearch;
    }

    public CustomerNoteLookup getCustomerNoteLookup() {
        return customerNoteLookup;
    }

    public CustomerNoteUpdate getCustomerNoteUpdate() {
        return customerNoteUpdate;
    }

    public CustomerUpdate getCustomerUpdate() {
        return customerUpdate;
    }

    public CustomerView getCustomerView() {
        return customerView;
    }

    public CustomerCvScoreService getCustomerCvScoreService() {
        return customerCvScoreService;
    }

    public IncidentLookup getIncidentLookup() {
        return incidentLookup;
    }

    public IncidentManagement getIncidentManagement() {
        return incidentManagement;
    }

    public IncidentUpdate getIncidentUpdate() {
        return incidentUpdate;
    }

    public IncidentView getIncidentView() {
        return incidentView;
    }

    public FeedbackLookup getFeedbackLookup() {
        return feedbackLookup;
    }

    public FeedbackUpdate getFeedbackUpdate() {
        return feedbackUpdate;
    }

    public FeedbackAnalytics getFeedbackAnalytics() {
        return feedbackAnalytics;
    }

    public NPSScores getNpsScores() {
        return npsScores;
    }

    public FlightRepository getFlightRepository() {
        return flightRepository;
    }
}
