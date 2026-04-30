package ui.app;

import database.initialization.DataSeeder;
import service.Backend;
import service.implementation.ActionServiceImpl;
import service.implementation.CustomerNoteServiceImpl;
import service.implementation.CustomerServiceImpl;
import service.implementation.FeedbackServiceImpl;
import service.implementation.FlightServiceImpl;
import service.implementation.IncidentServiceImpl;
import service.implementation.NPS.NPSServiceImpl;
import service.implementation.Priority.ExpectedImpactCalcServiceImpl;
import service.implementation.Priority.PriorityCalcServiceImpl;
import service.implementation.RecommendationExecutionServiceImpl;
import service.implementation.advisor.LowestLoadAdvisorAssignmentServiceImpl;
import service.implementation.cv.CVScoreCalcServiceImpl;
import service.implementation.cv.CustomerCvScoreServiceImpl;
import service.implementation.incidents.action.CreateSuggestedActionServiceImpl;
import service.implementation.incidents.delay.CreateDelayIncidentServiceImpl;
import service.implementation.incidents.feedback.CreateFeedbackIncidentServiceImpl;
import service.interfaces.frontend.ActionService;
import service.interfaces.frontend.CustomerNoteService;
import service.interfaces.frontend.CustomerService;
import service.interfaces.frontend.FeedbackService;
import service.interfaces.frontend.FlightService;
import service.interfaces.frontend.IncidentService;
import service.interfaces.frontend.NPSService;
import service.interfaces.frontend.RecommendationExecutionService;
import service.interfaces.internal.AdvisorAssignmentService;
import service.interfaces.internal.CreateDelayIncidentService;
import service.interfaces.internal.CreateFeedbackIncidentService;
import service.interfaces.internal.CreateSuggestedActionService;
import service.interfaces.internal.CustomerCvScoreService;
import service.interfaces.internal.ExpectedImpactCalcService;
import service.interfaces.internal.PriorityCalcService;
import simulation.SimulationService;
import simulation.engine.DefaultSimulationEngine;
import simulation.implementation.SimulationServiceImpl;
import simulation.persistence.DatabaseSimulationDataCleanupService;
import simulation.persistence.SimulationDataCleanupService;
import ui.service.DashboardDataService;
import ui.service.DashboardDataServiceImpl;
import ui.service.FlightDetailService;
import ui.service.FlightDetailServiceImpl;
import ui.service.JourneyDetailService;
import ui.service.JourneyDetailServiceImpl;
import ui.service.SimulationControlService;
import ui.service.SimulationControlServiceImpl;

import java.util.Random;

public class DashboardApplicationBootstrap {
    public DashboardApplicationContext bootstrap() {
        Backend backend = new Backend();
        seedDatabase(backend);
        CustomerCvScoreService customerCvScoreService = new CustomerCvScoreServiceImpl(
                backend.getCustomerLookup(),
                backend.getCustomerCvProfileLookup(),
                backend.getFlightRepository(),
                new CVScoreCalcServiceImpl()
        );
        ExpectedImpactCalcService expectedImpactCalcService = new ExpectedImpactCalcServiceImpl();
        PriorityCalcService priorityCalcService = new PriorityCalcServiceImpl(
                customerCvScoreService,
                expectedImpactCalcService
        );

        IncidentService incidentService = new IncidentServiceImpl(
                backend.getIncidentLookup(),
                backend.getIncidentView(),
                backend.getIncidentManagement(),
                backend.getIncidentUpdate(),
                priorityCalcService
        );
        CustomerService customerService = new CustomerServiceImpl(
                backend.getCustomerLookup(),
                backend.getCustomerSearch(),
                backend.getCustomerUpdate(),
                backend.getCustomerView()
        );
        ActionService actionService = new ActionServiceImpl(
                backend.getActionLookup(),
                backend.getActionManagement(),
                backend.getActionUpdate()
        );
        FeedbackService feedbackService = new FeedbackServiceImpl(
                backend.getFeedbackAnalytics(),
                backend.getFeedbackLookup(),
                backend.getFeedbackUpdate()
        );
        CustomerNoteService customerNoteService = new CustomerNoteServiceImpl(
                backend.getCustomerNoteLookup(),
                backend.getCustomerNoteUpdate()
        );
        FlightService flightService = new FlightServiceImpl(backend.getFlightRepository());
        NPSService npsService = new NPSServiceImpl(backend.getNpsScores());
        RecommendationExecutionService recommendationExecutionService = new RecommendationExecutionServiceImpl(
                actionService,
                incidentService,
                customerService,
                backend.getAdvisorRepository()
        );

        DashboardDataService dashboardDataService = new DashboardDataServiceImpl(
                customerService,
                incidentService,
                actionService,
                npsService,
                recommendationExecutionService
        );
        JourneyDetailService journeyDetailService = new JourneyDetailServiceImpl(
                customerService,
                incidentService,
                feedbackService,
                customerNoteService
        );
        FlightDetailService flightDetailService = new FlightDetailServiceImpl(
                flightService,
                feedbackService,
                customerService
        );

        SimulationService simulationService = buildSimulationService(backend, expectedImpactCalcService, priorityCalcService);
        SimulationDataCleanupService simulationDataCleanupService = new DatabaseSimulationDataCleanupService();
        SimulationControlService simulationControlService = new SimulationControlServiceImpl(
                backend.getAdvisorRepository(),
                simulationService,
                simulationDataCleanupService
        );

        return new DashboardApplicationContext(
                dashboardDataService,
                journeyDetailService,
                flightDetailService,
                simulationControlService,
                simulationDataCleanupService
        );
    }

    private void seedDatabase(Backend backend) {
        DataSeeder.seed(
                backend.getAdvisorRepository(),
                backend.getCustomerLookup(),
                backend.getCustomerUpdate(),
                backend.getCustomerCvProfileUpdate(),
                backend.getCustomerNoteUpdate(),
                backend.getFlightRepository(),
                backend.getFeedbackUpdate(),
                backend.getIncidentLookup(),
                backend.getIncidentUpdate(),
                backend.getActionLookup(),
                backend.getActionUpdate()
        );
    }

    private SimulationService buildSimulationService(Backend backend,
                                                     ExpectedImpactCalcService expectedImpactCalcService,
                                                     PriorityCalcService priorityCalcService) {
        AdvisorAssignmentService advisorAssignmentService = new LowestLoadAdvisorAssignmentServiceImpl(
                backend.getAdvisorRepository(),
                backend.getCustomerSearch()
        );
        CreateDelayIncidentService createDelayIncidentService = new CreateDelayIncidentServiceImpl(
                backend.getCustomerLookup(),
                backend.getFlightRepository(),
                backend.getIncidentLookup(),
                backend.getIncidentUpdate(),
                expectedImpactCalcService,
                priorityCalcService
        );
        CreateFeedbackIncidentService createFeedbackIncidentService = new CreateFeedbackIncidentServiceImpl(
                backend.getIncidentUpdate(),
                backend.getIncidentLookup(),
                backend.getFeedbackLookup(),
                backend.getCustomerLookup(),
                expectedImpactCalcService,
                priorityCalcService
        );
        CreateSuggestedActionService createSuggestedActionService = new CreateSuggestedActionServiceImpl(
                backend.getActionLookup(),
                backend.getActionUpdate(),
                backend.getFlightRepository()
        );

        return new SimulationServiceImpl(
                new simulation.validation.DefaultSimulationConfigValidator(),
                new DefaultSimulationEngine(
                        backend.getCustomerUpdate(),
                        backend.getCustomerLookup(),
                        backend.getCustomerCvProfileUpdate(),
                        backend.getFlightRepository(),
                        backend.getFeedbackLookup(),
                        backend.getFeedbackUpdate(),
                        advisorAssignmentService,
                        createDelayIncidentService,
                        createFeedbackIncidentService,
                        createSuggestedActionService,
                        new Random()
                )
        );
    }
}
