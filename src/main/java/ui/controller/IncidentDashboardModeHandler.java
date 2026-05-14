package ui.controller;

import javafx.scene.control.ListCell;
import model.view.IncidentDetailView;
import model.view.IncidentOverview;
import ui.navigation.JourneyDetailNavigator;
import ui.service.DashboardDataService;
import ui.view.factory.DashboardSubviewFactory;

import java.util.ArrayList;
import java.util.List;

// AI used
public class IncidentDashboardModeHandler implements DashboardModeHandler {
    private final DashboardDataService dashboardDataService;
    private final DashboardSubviewFactory subviewFactory;
    private final JourneyDetailNavigator journeyDetailNavigator;
    private int currentAdvisorId;

    public IncidentDashboardModeHandler(DashboardDataService dashboardDataService,
                                        DashboardSubviewFactory subviewFactory,
                                        JourneyDetailNavigator journeyDetailNavigator) {
        this.dashboardDataService = dashboardDataService;
        this.subviewFactory = subviewFactory;
        this.journeyDetailNavigator = journeyDetailNavigator;
    }

    @Override
    public String getListTitle() {
        return "Active Incidents (Priority Order)";
    }

    @Override
    public List<Object> loadItems(int advisorId) {
        currentAdvisorId = advisorId;
        try {
            return new ArrayList<>(dashboardDataService.loadOpenIncidentOverviews(advisorId));
        } catch (RuntimeException exception) {
            return List.of();
        }
    }

    @Override
    public ListCell<Object> createListCell() {
        return new ui.view.cell.IncidentOverviewCell();
    }

    @Override
    public String getEmptyDetailMessage() {
        return "No incidents available.";
    }

    @Override
    public String getEmptyActionsMessage() {
        return "No recommendations because there are no active incidents.";
    }

    @Override
    public void handleSelection(Object selection, DashboardPaneHost host) {
        if (!(selection instanceof IncidentOverview overview)) {
            host.showEmptyDetail("Dashboard Detail", "Unsupported incident selection.");
            host.showEmptyActions("No recommendations available for this selection.");
            return;
        }

        IncidentDetailView detail;
        try {
            detail = dashboardDataService.loadIncidentDetail(overview.getIncidentId());
        } catch (RuntimeException exception) {
            host.showEmptyDetail("Dashboard Detail", "Incident details could not be loaded right now.");
            host.showEmptyActions("Recommendations are unavailable because incident data could not be loaded.");
            return;
        }
        if (detail == null) {
            host.showEmptyDetail("Dashboard Detail", "Incident details could not be loaded.");
            host.showEmptyActions("No recommendations available because the incident detail is unavailable.");
            return;
        }

        var detailView = subviewFactory.loadIncidentDetailView();
        detailView.controller().configure(
                journeyDetailNavigator,
                dashboardDataService,
                currentAdvisorId,
                host::refreshDashboardData
        );
        detailView.controller().setData(detail, overview);
        host.showDetail(detailView.root());

        var actionView = subviewFactory.loadActionPanelView();
        try {
            actionView.controller().showActions(dashboardDataService.loadActionItemsForIncident(overview.getIncidentId()),
                    overview.getIncidentId(),
                    dashboardDataService,
                    host::refreshDashboardData);
        } catch (RuntimeException exception) {
            actionView.controller().showEmptyState("Recommendations could not be loaded right now.");
        }
        host.showActions(actionView.root());
    }
}
