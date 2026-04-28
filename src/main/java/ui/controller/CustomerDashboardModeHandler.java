package ui.controller;

import javafx.scene.control.ListCell;
import model.CustomerDetailView;
import model.CustomerOverview;
import ui.navigation.JourneyDetailNavigator;
import ui.service.DashboardDataService;
import ui.view.factory.DashboardSubviewFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomerDashboardModeHandler implements DashboardModeHandler {
    private final DashboardDataService dashboardDataService;
    private final DashboardSubviewFactory subviewFactory;
    private final JourneyDetailNavigator journeyDetailNavigator;

    public CustomerDashboardModeHandler(DashboardDataService dashboardDataService,
                                        DashboardSubviewFactory subviewFactory,
                                        JourneyDetailNavigator journeyDetailNavigator) {
        this.dashboardDataService = dashboardDataService;
        this.subviewFactory = subviewFactory;
        this.journeyDetailNavigator = journeyDetailNavigator;
    }

    @Override
    public String getListTitle() {
        return "Assigned Customers";
    }

    @Override
    public List<Object> loadItems(int advisorId) {
        return new ArrayList<>(dashboardDataService.loadCustomerOverviews(advisorId));
    }

    @Override
    public ListCell<Object> createListCell() {
        return new ui.view.cell.CustomerOverviewCell();
    }

    @Override
    public String getEmptyDetailMessage() {
        return "No customers assigned.";
    }

    @Override
    public String getEmptyActionsMessage() {
        return "No recommendations because there are no assigned customers.";
    }

    @Override
    public void handleSelection(Object selection, DashboardPaneHost host) {
        if (!(selection instanceof CustomerOverview overview)) {
            host.showEmptyDetail("Dashboard Detail", "Unsupported customer selection.");
            host.showEmptyActions("No recommendations available for this selection.");
            return;
        }

        CustomerDetailView detail = dashboardDataService.loadCustomerDetail(overview.getCustomerId());
        if (detail == null) {
            host.showEmptyDetail("Dashboard Detail", "Customer details could not be loaded.");
            host.showEmptyActions("No recommendations available because the customer detail is unavailable.");
            return;
        }

        var detailView = subviewFactory.loadCustomerDetailView();
        detailView.controller().configure(journeyDetailNavigator);
        detailView.controller().setData(detail, overview);
        host.showDetail(detailView.root());

        var actionView = subviewFactory.loadActionPanelView();
        if (detail.hasOpenIncident() && detail.getOpenIncidentId() != null) {
            List<model.ActionItem> actions = dashboardDataService.loadActionItemsForIncident(detail.getOpenIncidentId());
            actionView.controller().showActions(
                    actions,
                    detail.getOpenIncidentId(),
                    collectSelectedActionIds(host, actions),
                    host::selectAction
            );
        } else {
            actionView.controller().showEmptyState("No active incidents for this customer.");
        }
        host.showActions(actionView.root());
    }

    private Set<Integer> collectSelectedActionIds(DashboardPaneHost host, List<model.ActionItem> actions) {
        Set<Integer> selected = new HashSet<>();
        for (model.ActionItem action : actions) {
            if (host.isActionSelected(action.getId())) {
                selected.add(action.getId());
            }
        }
        return selected;
    }
}
