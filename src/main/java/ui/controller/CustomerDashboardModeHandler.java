package ui.controller;

import javafx.scene.control.ListCell;
import model.domain.ActionItem;
import model.view.CustomerDetailView;
import model.view.CustomerOverview;
import ui.navigation.JourneyDetailNavigator;
import ui.service.DashboardDataService;
import ui.view.factory.DashboardSubviewFactory;

import java.util.ArrayList;
import java.util.List;

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
        try {
            return new ArrayList<>(dashboardDataService.loadCustomerOverviews(advisorId));
        } catch (RuntimeException exception) {
            return List.of();
        }
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

        CustomerDetailView detail;
        try {
            detail = dashboardDataService.loadCustomerDetail(overview.getCustomerId());
        } catch (RuntimeException exception) {
            host.showEmptyDetail("Dashboard Detail", "Customer details could not be loaded right now.");
            host.showEmptyActions("Recommendations are unavailable because customer data could not be loaded.");
            return;
        }
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
            try {
                List<ActionItem> actions = dashboardDataService.loadActionItemsForIncident(detail.getOpenIncidentId());
                actionView.controller().showActions(
                        actions,
                        detail.getOpenIncidentId(),
                        dashboardDataService,
                        host::refreshDashboardData
                );
            } catch (RuntimeException exception) {
                actionView.controller().showEmptyState("Recommendations could not be loaded right now.");
            }
        } else {
            actionView.controller().showEmptyState("No active incidents for this customer.");
        }
        host.showActions(actionView.root());
    }
}
