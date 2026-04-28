package ui.app;

import database.initialization.DataSeeder;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.Backend;
import service.implementation.ActionServiceImpl;
import service.implementation.CustomerNoteServiceImpl;
import service.implementation.CustomerServiceImpl;
import service.implementation.FeedbackServiceImpl;
import service.implementation.FlightServiceImpl;
import service.implementation.IncidentServiceImpl;
import service.implementation.NPS.NPSServiceImpl;
import service.interfaces.frontend.ActionService;
import service.interfaces.frontend.CustomerNoteService;
import service.interfaces.frontend.CustomerService;
import service.interfaces.frontend.FeedbackService;
import service.interfaces.frontend.FlightService;
import service.interfaces.frontend.IncidentService;
import service.interfaces.frontend.NPSService;
import ui.controller.DashboardController;
import ui.navigation.JourneyDetailNavigator;
import ui.navigation.JourneyDetailNavigatorImpl;
import ui.service.DashboardDataService;
import ui.service.DashboardDataServiceImpl;
import ui.service.FlightDetailService;
import ui.service.FlightDetailServiceImpl;
import ui.service.JourneyDetailService;
import ui.service.JourneyDetailServiceImpl;

import java.io.IOException;

public class DashboardApp extends Application {
    private Backend backend;
    private CustomerService customerService;
    private IncidentService incidentService;
    private ActionService actionService;
    private NPSService npsService;
    private DashboardDataService dashboardDataService;
    private FeedbackService feedbackService;
    private CustomerNoteService customerNoteService;
    private JourneyDetailService journeyDetailService;
    private JourneyDetailNavigator journeyDetailNavigator;
    private FlightService flightService;
    private FlightDetailService flightDetailService;

    public static void launchDashboard(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        backend = new Backend();
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

        incidentService = new IncidentServiceImpl(
                backend.getIncidentLookup(),
                backend.getIncidentView(),
                backend.getIncidentManagement(),
                backend.getIncidentUpdate()
        );
        customerService = new CustomerServiceImpl(
                backend.getCustomerLookup(),
                backend.getCustomerSearch(),
                backend.getCustomerUpdate(),
                incidentService,
                backend.getCustomerView(),
                null
        );
        actionService = new ActionServiceImpl(
                backend.getActionLookup(),
                backend.getActionManagement(),
                backend.getActionUpdate()
        );
        feedbackService = new FeedbackServiceImpl(
                backend.getFeedbackAnalytics(),
                backend.getFeedbackLookup(),
                backend.getFeedbackUpdate()
        );
        customerNoteService = new CustomerNoteServiceImpl(
                backend.getCustomerNoteLookup(),
                backend.getCustomerNoteUpdate()
        );
        flightService = new FlightServiceImpl(backend.getFlightRepository());
        npsService = new NPSServiceImpl(backend.getNpsScores());
        dashboardDataService = new DashboardDataServiceImpl(
                customerService,
                incidentService,
                actionService,
                npsService
        );
        journeyDetailService = new JourneyDetailServiceImpl(
                customerService,
                incidentService,
                feedbackService,
                customerNoteService
        );
        flightDetailService = new FlightDetailServiceImpl(
                flightService,
                feedbackService,
                customerService
        );
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(DashboardApp.class.getResource("/ui/view/DashboardView.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1560, 920);
        scene.getStylesheets().add(DashboardApp.class.getResource("/ui/view/dashboard.css").toExternalForm());

        stage.setTitle("Space Flight Experience Management");
        stage.setScene(scene);
        stage.setMinWidth(1280);
        stage.setMinHeight(820);

        journeyDetailNavigator = new JourneyDetailNavigatorImpl(journeyDetailService, flightDetailService, stage);

        DashboardController controller = loader.getController();
        controller.initializeDashboard(dashboardDataService, journeyDetailNavigator);

        stage.show();
    }
}
