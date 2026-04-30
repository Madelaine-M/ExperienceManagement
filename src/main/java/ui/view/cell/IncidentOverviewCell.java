package ui.view.cell;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import model.view.IncidentOverview;
import ui.view.DashboardFormatters;

public class IncidentOverviewCell extends ListCell<Object> {
    @Override
    protected void updateItem(Object item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || !(item instanceof IncidentOverview overview)) {
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

        Label priority = new Label(DashboardFormatters.priorityLabel(overview.getPriorityScore()));
        priority.getStyleClass().addAll("priority-badge", DashboardFormatters.priorityStyle(overview.getPriorityScore()));
        header.getChildren().addAll(name, spacer, priority);

        Label description = new Label(DashboardFormatters.defaultText(overview.getDescription(), "Incident recorded"));
        description.getStyleClass().add("list-card-accent");
        description.setWrapText(true);

        Label revenueRisk = new Label("Revenue Risk: " + DashboardFormatters.formatCurrency(overview.getRevenueRisk()));
        revenueRisk.getStyleClass().add("list-card-meta");

        Label scoreImpact = new Label("Score Impact: "
                + String.format(java.util.Locale.ENGLISH, "%.1f", overview.getScoreImpact()));
        scoreImpact.getStyleClass().add("list-card-meta");

        card.getChildren().addAll(header, description, revenueRisk, scoreImpact);
        setText(null);
        setGraphic(card);
    }
}
