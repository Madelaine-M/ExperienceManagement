package service.implementation;

import model.domain.Incident;
import model.view.IncidentDetailView;
import model.view.IncidentOverview;
import model.enums.IncidentStatus;
import repository.interfaces.IncidentLookup;
import repository.interfaces.IncidentManagement;
import repository.interfaces.IncidentUpdate;
import repository.interfaces.IncidentView;
import service.interfaces.frontend.IncidentService;
import service.interfaces.internal.PriorityCalcService;

import java.util.Comparator;
import java.util.List;

public class IncidentServiceImpl implements IncidentService {
    private final IncidentLookup incidentLookup;
    private final IncidentView incidentView;
    private final IncidentUpdate incidentUpdate;
    private final IncidentManagement incidentManagement;
    private final PriorityCalcService priorityCalcService;

    public IncidentServiceImpl (IncidentLookup incidentLookup,
                                IncidentView incidentView,
                                IncidentManagement incidentManagement,
                                IncidentUpdate incidentUpdate,
                                PriorityCalcService priorityCalcService) {
        this.incidentView = incidentView;
        this.incidentManagement = incidentManagement;
        this.incidentUpdate = incidentUpdate;
        this.incidentLookup=incidentLookup;
        this.priorityCalcService = priorityCalcService;
    }

    @Override
    public void save(Incident incident) {
        incidentUpdate.save(incident);
    }

    @Override
    public Incident findById(int id) {
        return incidentLookup.findById(id);
    }

    @Override
    public List<Incident> findAllByCustomerId(int customerId) {
        return incidentLookup.findAllByCustomerId(customerId);
    }

    @Override
    public List<Incident> findByStatus(IncidentStatus status) {
        return incidentManagement.findByStatus(status);
    }

    @Override
    public List<Incident> findUnassigned() {
        return incidentManagement.findUnassigned();
    }

    @Override
    public List<Incident> findByAdvisorId(int advisorId) {
        return incidentManagement.findByAdvisorId(advisorId);
    }

    @Override
    public List<Incident> findPendingActionItems() {
        return incidentManagement.findPendingActionItems();
    }

    @Override
    public void updateStatus(int id, IncidentStatus status) {
        incidentUpdate.updateStatus(id, status);
    }

    @Override
    public List<IncidentOverview> findOpenOverviewsByAdvisorId(int advisorId) {
        List<IncidentOverview> overviews = incidentView.findOpenOverviewsByAdvisorId(advisorId);
        for (IncidentOverview overview : overviews) {
            overview.setCustomerCvScore(priorityCalcService.calculateCustomerCvScore(overview.getCustomerId()));
            overview.setPriorityScore(priorityCalcService.calculate(overview.getIncidentType(), overview.getCustomerCvScore()));
        }
        overviews.sort(Comparator
                .comparingDouble(IncidentOverview::getPriorityScore).reversed()
                .thenComparing(IncidentOverview::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparingInt(IncidentOverview::getIncidentId));
        return overviews;
    }

    @Override
    public IncidentDetailView findDetailByIncidentId(int incidentId) {
        return incidentView.findDetailByIncidentId(incidentId);
    }

}
