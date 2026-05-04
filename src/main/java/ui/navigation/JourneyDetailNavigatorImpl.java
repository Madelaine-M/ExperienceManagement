package ui.navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
    private final int advisorId;
    private final Deque<SceneState> sceneStack = new ArrayDeque<>();

    public JourneyDetailNavigatorImpl(JourneyDetailService journeyDetailService,
                                      FlightDetailService flightDetailService,
                                      Stage primaryStage,
                                      int advisorId) {
        this.journeyDetailService = journeyDetailService;
        this.flightDetailService = flightDetailService;
        this.primaryStage = primaryStage;
        this.advisorId = advisorId;
    }

    @Override
    public void openJourney(int customerId, Integer incidentId) {
        try {
            pushCurrentScene();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/view/JourneyDetailView.fxml"));
            Parent root = loader.load();
            JourneyDetailController controller = loader.getController();
            controller.initializeJourney(journeyDetailService, this, customerId, incidentId, advisorId);

            primaryStage.setTitle("Customer Journey");
            primaryStage.getScene().setRoot(root);
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

            primaryStage.setTitle("Flight Detail");
            primaryStage.getScene().setRoot(root);
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
        primaryStage.setTitle(previous.title());
        primaryStage.getScene().setRoot(previous.root());
    }

    private void pushCurrentScene() {
        if (primaryStage.getScene() == null) {
            return;
        }
        sceneStack.push(new SceneState(primaryStage.getScene().getRoot(), primaryStage.getTitle()));
    }

    private record SceneState(Parent root, String title) {
    }
}
