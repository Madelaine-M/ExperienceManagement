package service;

import database.initialization.DatabaseInitializer;
import repository.implementation.DatabaseActionRepository;
import repository.implementation.DatabaseAdvisorRepository;
import repository.implementation.DatabaseCustomerRepository;
import repository.implementation.DatabaseFeedbackRepository;
import repository.implementation.DatabaseHistoryRepository;
import repository.implementation.DatabaseIncidentRepository;
import repository.interfaces.ActionRepository;
import repository.interfaces.AdvisorRepository;
import repository.interfaces.CustomerRepository;
import repository.interfaces.FeedbackRepository;
import repository.interfaces.HistoryRepository;
import repository.interfaces.IncidentRepository;

public class Backend {
    private final ActionRepository actionRepository;
    private final AdvisorRepository advisorRepository;
    private final CustomerRepository customerRepository;
    private final IncidentRepository incidentRepository;
    private final FeedbackRepository feedbackRepository;
    private final HistoryRepository historyRepository;

    public Backend() {
        DatabaseInitializer.initialize();
        this.actionRepository = new DatabaseActionRepository();
        this.advisorRepository = new DatabaseAdvisorRepository();
        this.customerRepository = new DatabaseCustomerRepository();
        this.incidentRepository = new DatabaseIncidentRepository();
        this.feedbackRepository = new DatabaseFeedbackRepository();
        this.historyRepository = new DatabaseHistoryRepository();
    }

    public ActionRepository getActionRepository() {
        return actionRepository;
    }

    public AdvisorRepository getAdvisorRepository() {
        return advisorRepository;
    }

    public CustomerRepository getCustomerRepository() {
        return customerRepository;
    }

    public IncidentRepository getIncidentRepository() {
        return incidentRepository;
    }

    public FeedbackRepository getFeedbackRepository() {
        return feedbackRepository;
    }

    public HistoryRepository getHistoryRepository() {
        return historyRepository;
    }
}
