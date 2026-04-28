package ui.controller;

import javafx.scene.control.ListCell;
import model.IncidentDetailView;
import model.IncidentOverview;
import ui.navigation.JourneyDetailNavigator;
import ui.service.DashboardDataService;
import ui.view.factory.DashboardSubviewFactory;

import java.util.ArrayList;
import java.util.List;

public class IncidentDashboardModeHandler implements DashboardModeHandler {
    private final DashboardDataService dashboardDataService;
    private final DashboardSubviewFactory subviewFactory;
    private final JourneyDetailNavigator journeyDetailNavigator;

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
        return new ArrayList<>(dashboardDataService.loadIncidentOverviews(advisorId));
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

        IncidentDetailView detail = dashboardDataService.loadIncidentDetail(overview.getIncidentId());
        if (detail == null) {
            host.showEmptyDetail("Dashboard Detail", "Incident details could not be loaded.");
            host.showEmptyActions("No recommendations available because the incident detail is unavailable.");
            return;
        }

        var detailView = subviewFactory.loadIncidentDetailView();
        detailView.controller().configure(journeyDetailNavigator);
        detailView.controller().setData(detail, overview);
        host.showDetail(detailView.root());

        var actionView = subviewFactory.loadActionPanelView();
        actionView.controller().showActions(
                dashboardDataService.loadActionItemsForIncident(overview.getIncidentId()),
                overview.getIncidentId(),
                collectSelectedActionIds(host, dashboardDataService.loadActionItemsForIncident(overview.getIncidentId())),
                host::selectAction
        );
        host.showActions(actionView.root());
    }

    private java.util.Set<Integer> collectSelectedActionIds(DashboardPaneHost host, List<model.ActionItem> actions) {
        java.util.Set<Integer> selected = new java.util.HashSet<>();
        for (model.ActionItem action : actions) {
            if (host.isActionSelected(action.getId())) {
                selected.add(action.getId());
            }
        }
        return selected;
    }
}
