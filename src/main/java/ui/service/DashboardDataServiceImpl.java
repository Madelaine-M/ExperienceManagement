package ui.service;

import model.ActionItem;
import model.CustomerDetailView;
import model.CustomerOverview;
import model.IncidentDetailView;
import model.IncidentOverview;
import service.interfaces.frontend.ActionService;
import service.interfaces.frontend.CustomerService;
import service.interfaces.frontend.IncidentService;
import service.interfaces.frontend.NPSService;

import java.util.List;

public class DashboardDataServiceImpl implements DashboardDataService {
    private final CustomerService customerService;
    private final IncidentService incidentService;
    private final ActionService actionService;
    private final NPSService npsService;

    public DashboardDataServiceImpl(CustomerService customerService,
                                    IncidentService incidentService,
                                    ActionService actionService,
                                    NPSService npsService) {
        this.customerService = customerService;
        this.incidentService = incidentService;
        this.actionService = actionService;
        this.npsService = npsService;
    }

    @Override
    public float getCompanyNps() {
        return npsService.getNPS();
    }

    @Override
    public List<IncidentOverview> loadIncidentOverviews(int advisorId) {
        return incidentService.findAllPrioritizedOverviews(advisorId);
    }

    @Override
    public IncidentDetailView loadIncidentDetail(int incidentId) {
        return incidentService.findDetailByIncidentId(incidentId);
    }

    @Override
    public List<CustomerOverview> loadCustomerOverviews(int advisorId) {
        return customerService.findOverviewsByAdvisorId(advisorId);
    }

    @Override
    public CustomerDetailView loadCustomerDetail(int customerId) {
        return customerService.findDetailByCustomerId(customerId);
    }

    @Override
    public List<ActionItem> loadActionItemsForIncident(int incidentId) {
        return actionService.findByIncidentId(incidentId);
    }
}
