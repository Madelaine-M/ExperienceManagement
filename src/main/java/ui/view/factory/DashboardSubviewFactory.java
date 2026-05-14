package ui.view.factory;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import ui.controller.ActionPanelController;
import ui.controller.CustomerDetailController;
import ui.controller.IncidentDetailController;

import java.io.IOException;
import java.io.UncheckedIOException;

// AI used
public class DashboardSubviewFactory {

    public LoadedSubview<IncidentDetailController> loadIncidentDetailView() {
        return load("/ui/view/IncidentDetailView.fxml", IncidentDetailController.class);
    }

    public LoadedSubview<CustomerDetailController> loadCustomerDetailView() {
        return load("/ui/view/CustomerDetailView.fxml", CustomerDetailController.class);
    }

    public LoadedSubview<ActionPanelController> loadActionPanelView() {
        return load("/ui/view/ActionPanelView.fxml", ActionPanelController.class);
    }

    private <T> LoadedSubview<T> load(String resourcePath, Class<T> controllerType) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resourcePath));
            Parent root = loader.load();
            Object controller = loader.getController();
            if (!controllerType.isInstance(controller)) {
                throw new IllegalStateException("Unexpected controller type for " + resourcePath);
            }
            return new LoadedSubview<>(root, controllerType.cast(controller));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load subview " + resourcePath, e);
        }
    }
}
