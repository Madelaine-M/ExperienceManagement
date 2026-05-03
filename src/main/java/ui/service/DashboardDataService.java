package ui.service;

import model.domain.ActionItem;
import model.view.CustomerDetailView;
import model.view.CustomerOverview;
import model.view.IncidentDetailView;
import model.view.IncidentOverview;
import model.workflow.RecommendationEmailDraft;

import java.util.List;

public interface DashboardDataService {

    float getCompanyNps();

    List<IncidentOverview> loadOpenIncidentOverviews(int advisorId);

    IncidentDetailView loadIncidentDetail(int incidentId);

    List<CustomerOverview> loadCustomerOverviews(int advisorId);

    CustomerDetailView loadCustomerDetail(int customerId);

    void saveCustomerAdvisorNote(int customerId, int advisorId, String noteText);

    List<ActionItem> loadActionItemsForIncident(int incidentId);

    RecommendationEmailDraft prepareRecommendationDraft(int actionId, int optionNumber);

    void executeRecommendation(int actionId, int optionNumber, String subject, String emailBody);
}
