package ui.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.domain.Advisor;
import simulation.persistence.SimulationDataCleanupService;
import ui.controller.DashboardController;
import ui.controller.SimulationController;
import ui.navigation.JourneyDetailNavigator;
import ui.navigation.JourneyDetailNavigatorImpl;
import ui.service.DashboardDataService;
import ui.service.DashboardDataServiceImpl;
import ui.service.FlightDetailService;
import ui.service.FlightDetailServiceImpl;
import ui.service.JourneyDetailService;
import ui.service.JourneyDetailServiceImpl;
import ui.service.SimulationControlService;
import ui.service.SimulationControlServiceImpl;

import java.io.IOException;

public class DashboardApp extends Application {
    private DashboardDataService dashboardDataService;
    private JourneyDetailService journeyDetailService;
    private JourneyDetailNavigator journeyDetailNavigator;
    private FlightDetailService flightDetailService;
    private SimulationControlService simulationControlService;
    private Stage primaryStage;
    private Scene simulationScene;
    private SimulationController simulationController;
    private DashboardController dashboardController;
    private SimulationDataCleanupService simulationDataCleanupService;

    public static void launchDashboard(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        DashboardApplicationContext applicationContext = new DashboardApplicationBootstrap().bootstrap();
        dashboardDataService = applicationContext.dashboardDataService();
        journeyDetailService = applicationContext.journeyDetailService();
        flightDetailService = applicationContext.flightDetailService();
        simulationControlService = applicationContext.simulationControlService();
        simulationDataCleanupService = applicationContext.simulationDataCleanupService();
    }

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;
        showSimulationScene();
    }

    private void showSimulationScene() throws IOException {
        if (dashboardController != null) {
            dashboardController.stopAutoRefresh();
            dashboardController = null;
        }

        if (simulationScene == null) {
            FXMLLoader loader = new FXMLLoader(DashboardApp.class.getResource("/ui/view/SimulationView.fxml"));
            Parent root = loader.load();
            simulationScene = new Scene(root, 1560, 920);
            simulationScene.getStylesheets().add(DashboardApp.class.getResource("/ui/view/dashboard.css").toExternalForm());
            simulationController = loader.getController();
            simulationController.initializeSimulation(simulationControlService, advisor -> {
                try {
                    showDashboardScene(advisor);
                } catch (IOException exception) {
                    throw new RuntimeException(exception);
                }
            });
        }

        primaryStage.setTitle("Simulation Control");
        primaryStage.setScene(simulationScene);
        primaryStage.setMinWidth(1280);
        primaryStage.setMinHeight(820);

        primaryStage.show();
    }

    private void showDashboardScene(Advisor advisor) throws IOException {
        FXMLLoader loader = new FXMLLoader(DashboardApp.class.getResource("/ui/view/DashboardView.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1560, 920);
        scene.getStylesheets().add(DashboardApp.class.getResource("/ui/view/dashboard.css").toExternalForm());

        primaryStage.setTitle("Advisor Dashboard");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1280);
        primaryStage.setMinHeight(820);

        journeyDetailNavigator = new JourneyDetailNavigatorImpl(journeyDetailService, flightDetailService, primaryStage);

        dashboardController = loader.getController();
        dashboardController.initializeDashboard(
                dashboardDataService,
                journeyDetailNavigator,
                advisor,
                () -> {
                    try {
                        showSimulationScene();
                    } catch (IOException exception) {
                        throw new RuntimeException(exception);
                    }
                }
        );

        primaryStage.show();
    }

    @Override
    public void stop() {
        if (dashboardController != null) {
            dashboardController.stopAutoRefresh();
        }
        if (simulationDataCleanupService != null) {
            simulationDataCleanupService.cleanupGeneratedData();
        }
    }
}
