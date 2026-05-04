package ui.view.cell;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import model.view.CustomerOverview;
import ui.view.DashboardFormatters;

public class CustomerOverviewCell extends ListCell<Object> {
    @Override
    protected void updateItem(Object item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || !(item instanceof CustomerOverview overview)) {
            setText(null);
            setGraphic(null);
            return;
        }

        VBox card = new VBox(10);
        card.getStyleClass().add("list-card");
        card.setMaxWidth(Double.MAX_VALUE);
        card.prefWidthProperty().unbind();
        if (getListView() != null) {
            card.prefWidthProperty().bind(getListView().widthProperty().subtract(36));
        }

        HBox header = new HBox(10);
        Label name = new Label(DashboardFormatters.formatName(
                overview.getCustomerFirstName(),
                overview.getCustomerLastName()
        ) + " (" + DashboardFormatters.formatPackage(overview.getBookingPackage()) + ")");
        name.getStyleClass().add("list-card-title");
        name.setWrapText(true);
        name.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(name, Priority.ALWAYS);
        Region spacer = new Region();

        Label statusBadge = new Label(overview.hasOpenIncident() ? "OPEN INCIDENT" : "STABLE");
        statusBadge.getStyleClass().addAll("priority-badge", overview.hasOpenIncident() ? "priority-high" : "priority-low");
        header.getChildren().addAll(name, spacer, statusBadge);

        Label cvScore = new Label("CV Score: " + String.format(java.util.Locale.ENGLISH, "%.1f", overview.getCvScore()));
        cvScore.getStyleClass().add("list-card-accent");

        Label customerType = new Label("Type: "
                + DashboardFormatters.formatValue(overview.getCustomerType()));
        customerType.getStyleClass().add("list-card-meta");

        Label journeyStatus = new Label("Journey Status: "
                + DashboardFormatters.formatValue(overview.getStatus()));
        journeyStatus.getStyleClass().add("list-card-meta");

        Label incidentInfo = new Label(overview.hasOpenIncident()
                ? "Open Incident: " + DashboardFormatters.defaultText(overview.getIncidentDescription(), "Incident recorded")
                : "No active incidents");
        incidentInfo.getStyleClass().add(overview.hasOpenIncident() ? "list-card-meta" : "list-card-muted");
        incidentInfo.setWrapText(true);

        card.getChildren().addAll(header, cvScore, customerType, journeyStatus, incidentInfo);
        setText(null);
        setGraphic(card);
    }
}
