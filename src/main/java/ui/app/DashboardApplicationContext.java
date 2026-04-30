package ui.app;

import simulation.persistence.SimulationDataCleanupService;
import ui.service.DashboardDataService;
import ui.service.FlightDetailService;
import ui.service.JourneyDetailService;
import ui.service.SimulationControlService;

public record DashboardApplicationContext(
        DashboardDataService dashboardDataService,
        JourneyDetailService journeyDetailService,
        FlightDetailService flightDetailService,
        SimulationControlService simulationControlService,
        SimulationDataCleanupService simulationDataCleanupService
) {
}
