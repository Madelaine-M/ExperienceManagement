package ui.service;

import model.domain.ActionItem;
import model.domain.CustomerNote;
import model.view.CustomerDetailView;
import model.view.CustomerOverview;
import model.view.IncidentDetailView;
import model.view.IncidentOverview;
import model.workflow.RecommendationEmailDraft;
import service.interfaces.frontend.ActionService;
import service.interfaces.frontend.CustomerNoteService;
import service.interfaces.frontend.CustomerService;
import service.interfaces.frontend.IncidentService;
import service.interfaces.frontend.NPSService;
import service.interfaces.frontend.RecommendationExecutionService;

import java.time.LocalDateTime;
import java.util.List;

public class DashboardDataServiceImpl implements DashboardDataService {
    private final CustomerService customerService;
    private final CustomerNoteService customerNoteService;
    private final IncidentService incidentService;
    private final ActionService actionService;
    private final NPSService npsService;
    private final RecommendationExecutionService recommendationExecutionService;

    public DashboardDataServiceImpl(CustomerService customerService,
                                    CustomerNoteService customerNoteService,
                                    IncidentService incidentService,
                                    ActionService actionService,
                                    NPSService npsService,
                                    RecommendationExecutionService recommendationExecutionService) {
        this.customerService = customerService;
        this.customerNoteService = customerNoteService;
        this.incidentService = incidentService;
        this.actionService = actionService;
        this.npsService = npsService;
        this.recommendationExecutionService = recommendationExecutionService;
    }

    @Override
    public float getCompanyNps() {
        return npsService.getNPS();
    }

    @Override
    public List<IncidentOverview> loadOpenIncidentOverviews(int advisorId) {
        return incidentService.findOpenOverviewsByAdvisorId(advisorId);
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
    public void saveCustomerAdvisorNote(int customerId, int advisorId, String noteText) {
        String sanitized = noteText == null ? "" : noteText.trim();
        if (sanitized.isBlank()) {
            throw new IllegalArgumentException("Note text cannot be empty.");
        }

        LocalDateTime now = LocalDateTime.now();
        customerNoteService.save(new CustomerNote(0, customerId, advisorId, sanitized, now, now));
    }

    @Override
    public List<ActionItem> loadActionItemsForIncident(int incidentId) {
        return actionService.findByIncidentId(incidentId);
    }

    @Override
    public RecommendationEmailDraft prepareRecommendationDraft(int actionId, int optionNumber) {
        return recommendationExecutionService.prepareDraft(actionId, optionNumber);
    }

    @Override
    public void executeRecommendation(int actionId, int optionNumber, String subject, String emailBody) {
        recommendationExecutionService.sendRecommendation(actionId, optionNumber, subject, emailBody);
    }
}
