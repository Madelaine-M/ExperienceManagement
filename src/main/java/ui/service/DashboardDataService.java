package ui.service;

import model.ActionItem;
import model.CustomerDetailView;
import model.CustomerOverview;
import model.IncidentDetailView;
import model.IncidentOverview;

import java.util.List;

public interface DashboardDataService {

    float getCompanyNps();

    List<IncidentOverview> loadIncidentOverviews(int advisorId);

    IncidentDetailView loadIncidentDetail(int incidentId);

    List<CustomerOverview> loadCustomerOverviews(int advisorId);

    CustomerDetailView loadCustomerDetail(int customerId);

    List<ActionItem> loadActionItemsForIncident(int incidentId);
}
