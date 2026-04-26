package ui.view;

import database.initialization.DataSeeder;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Customer;
import service.Backend;

import java.util.ArrayList;
import java.util.List;

public class CustomerDashboardApp extends Application {
    private final ObservableList<String> customerItems = FXCollections.observableArrayList();

    private Backend backend;

    @Override
    public void init() {
        backend = new Backend();

        DataSeeder.seed(
                backend.getAdvisorRepository(),
                backend.getCustomerRepository(),
                backend.getIncidentRepository(),
                backend.getActionRepository(),
                backend.getHistoryRepository()
        );
    }

    @Override
    public void start(Stage stage) {
        Label heading = new Label("Customers");
        heading.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button loadCustomersButton = new Button("Show All Customers");
        loadCustomersButton.setOnAction(event -> loadCustomers());

        ListView<String> customerList = new ListView<>(customerItems);
        customerList.setPlaceholder(new Label("Click 'Show All Customers' to load customers."));

        VBox content = new VBox(12, heading, loadCustomersButton, customerList);
        content.setPadding(new Insets(16));

        BorderPane root = new BorderPane(content);

        Scene scene = new Scene(root, 640, 420);
        stage.setTitle("Customer Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    private void loadCustomers() {
        List<Customer> customers = backend.getCustomerRepository().findAll();
        List<String> formattedCustomers = new ArrayList<>();
        for (Customer customer : customers) {
            formattedCustomers.add(formatCustomer(customer));
        }
        customerItems.setAll(formattedCustomers);
    }

    private String formatCustomer(Customer customer) {
        return String.format(
                "%d | %s %s | %s",
                customer.getId(),
                safeValue(customer.getFirstName()),
                safeValue(customer.getLastName()),
                safeValue(customer.getEmail())
        );
    }

    private String safeValue(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
