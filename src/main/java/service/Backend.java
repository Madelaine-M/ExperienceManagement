package service;

import database.initialization.DatabaseInitializer;
import repository.implementation.DatabaseActionRepository;
import repository.implementation.DatabaseAdvisorRepository;
import repository.implementation.DatabaseCustomerRepository;
import repository.implementation.DatabaseCustomerView;
import repository.implementation.DatabaseFeedbackAnalytics;
import repository.implementation.DatabaseFeedbackLookup;
import repository.implementation.DatabaseFeedbackUpdate;
import repository.implementation.DatabaseFlightRepository;
import repository.implementation.DatabaseIncidentRepository;
import repository.implementation.DatabaseIncidentView;
import repository.implementation.DatabaseNPSScores;
import repository.interfaces.ActionLookup;
import repository.interfaces.ActionManagement;
import repository.interfaces.ActionUpdate;
import repository.interfaces.AdvisorRepository;
import repository.interfaces.CustomerLookup;
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

public class Backend {
    private final ActionLookup actionLookup;
    private final ActionManagement actionManagement;
    private final ActionUpdate actionUpdate;
    private final AdvisorRepository advisorRepository;
    private final CustomerLookup customerLookup;
    private final CustomerSearch customerSearch;
    private final CustomerUpdate customerUpdate;
    private final CustomerView customerView;
    private final FeedbackAnalytics feedbackAnalytics;
    private final FeedbackLookup feedbackLookup;
    private final FeedbackUpdate feedbackUpdate;
    private final NPSScores npsScores;
    private final FlightRepository flightRepository;
    private final IncidentLookup incidentLookup;
    private final IncidentManagement incidentManagement;
    private final IncidentUpdate incidentUpdate;
    private final IncidentView incidentView;

    public Backend() {
        DatabaseInitializer.initialize();
        DatabaseCustomerRepository customerRepository = new DatabaseCustomerRepository();
        DatabaseCustomerView customerViewRepository = new DatabaseCustomerView();
        DatabaseFeedbackLookup feedbackLookupRepository = new DatabaseFeedbackLookup();
        DatabaseFeedbackUpdate feedbackUpdateRepository = new DatabaseFeedbackUpdate();
        DatabaseFeedbackAnalytics feedbackAnalyticsRepository = new DatabaseFeedbackAnalytics();
        DatabaseNPSScores npsScoresRepository = new DatabaseNPSScores();
        DatabaseFlightRepository flightRepository = new DatabaseFlightRepository();
        DatabaseIncidentRepository incidentRepository = new DatabaseIncidentRepository();
        DatabaseIncidentView incidentViewRepository = new DatabaseIncidentView();
        DatabaseActionRepository actionRepository = new DatabaseActionRepository();
        this.actionLookup = actionRepository;
        this.actionManagement = actionRepository;
        this.actionUpdate = actionRepository;
        this.advisorRepository = new DatabaseAdvisorRepository();
        this.customerLookup = customerRepository;
        this.customerSearch = customerRepository;
        this.customerUpdate = customerRepository;
        this.customerView = customerViewRepository;
        this.feedbackAnalytics = feedbackAnalyticsRepository;
        this.feedbackLookup = feedbackLookupRepository;
        this.feedbackUpdate = feedbackUpdateRepository;
        this.npsScores = npsScoresRepository;
        this.flightRepository = flightRepository;
        this.incidentLookup = incidentRepository;
        this.incidentManagement = incidentRepository;
        this.incidentUpdate = incidentRepository;
        this.incidentView = incidentViewRepository;
    }

    public ActionLookup getActionLookup() {
        return actionLookup;
    }

    public ActionManagement getActionManagement() {
        return actionManagement;
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

    public CustomerSearch getCustomerSearch() {
        return customerSearch;
    }

    public CustomerUpdate getCustomerUpdate() {
        return customerUpdate;
    }

    public CustomerView getCustomerView() {
        return customerView;
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
