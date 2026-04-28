package ui.navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.controller.FlightDetailController;
import ui.controller.JourneyDetailController;
import ui.service.FlightDetailService;
import ui.service.JourneyDetailService;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.Deque;

public class JourneyDetailNavigatorImpl implements JourneyDetailNavigator {
    private final JourneyDetailService journeyDetailService;
    private final FlightDetailService flightDetailService;
    private final Stage primaryStage;
    private final Deque<SceneState> sceneStack = new ArrayDeque<>();

    public JourneyDetailNavigatorImpl(JourneyDetailService journeyDetailService,
                                      FlightDetailService flightDetailService,
                                      Stage primaryStage) {
        this.journeyDetailService = journeyDetailService;
        this.flightDetailService = flightDetailService;
        this.primaryStage = primaryStage;
    }

    @Override
    public void openJourney(int customerId, Integer incidentId) {
        try {
            pushCurrentScene();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/view/JourneyDetailView.fxml"));
            Parent root = loader.load();
            JourneyDetailController controller = loader.getController();
            controller.initializeJourney(journeyDetailService, this, customerId, incidentId);

            Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/ui/view/dashboard.css").toExternalForm());

            primaryStage.setTitle("Customer Journey");
            primaryStage.setScene(scene);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to open journey detail view.", e);
        }
    }

    @Override
    public void openFlightDetail(int flightId) {
        try {
            pushCurrentScene();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/view/FlightDetailView.fxml"));
            Parent root = loader.load();
            FlightDetailController controller = loader.getController();
            controller.initializeFlightDetail(flightDetailService, this, flightId);

            Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/ui/view/dashboard.css").toExternalForm());

            primaryStage.setTitle("Flight Detail");
            primaryStage.setScene(scene);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to open flight detail view.", e);
        }
    }

    @Override
    public void goBack() {
        if (sceneStack.isEmpty()) {
            return;
        }
        SceneState previous = sceneStack.pop();
        primaryStage.setScene(previous.scene());
        primaryStage.setTitle(previous.title());
    }

    private void pushCurrentScene() {
        if (primaryStage.getScene() == null) {
            return;
        }
        sceneStack.push(new SceneState(primaryStage.getScene(), primaryStage.getTitle()));
    }

    private record SceneState(Scene scene, String title) {
    }
}
