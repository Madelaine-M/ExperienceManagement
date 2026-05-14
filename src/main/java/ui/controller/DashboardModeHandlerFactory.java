package ui.controller;

import ui.navigation.JourneyDetailNavigator;
import ui.service.DashboardDataService;
import ui.view.factory.DashboardSubviewFactory;

// Concept for factory created with AI use
public class DashboardModeHandlerFactory {
    private final DashboardDataService dashboardDataService;
    private final DashboardSubviewFactory subviewFactory;
    private final JourneyDetailNavigator journeyDetailNavigator;

    public DashboardModeHandlerFactory(DashboardDataService dashboardDataService,
                                       DashboardSubviewFactory subviewFactory,
                                       JourneyDetailNavigator journeyDetailNavigator) {
        this.dashboardDataService = dashboardDataService;
        this.subviewFactory = subviewFactory;
        this.journeyDetailNavigator = journeyDetailNavigator;
    }

    public DashboardModeHandler create(DashboardMode mode) {
        return switch (mode) {
            case INCIDENTS -> new IncidentDashboardModeHandler(dashboardDataService, subviewFactory, journeyDetailNavigator);
            case CUSTOMERS -> new CustomerDashboardModeHandler(dashboardDataService, subviewFactory, journeyDetailNavigator);
        };
    }
}
