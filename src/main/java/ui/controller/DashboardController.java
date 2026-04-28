package ui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import ui.navigation.JourneyDetailNavigator;
import ui.service.DashboardDataService;
import ui.view.factory.DashboardSubviewFactory;
import ui.view.render.DashboardNodeFactory;

import java.util.HashSet;
import java.util.Set;

public class DashboardController implements DashboardPaneHost {
    private static final int ADVISOR_ID = 1;

    @FXML
    private Label npsValueLabel;
    @FXML
    private Label listTitleLabel;
    @FXML
    private ToggleButton incidentsToggle;
    @FXML
    private ToggleButton customersToggle;
    @FXML
    private ListView<Object> leftListView;
    @FXML
    private VBox detailPane;
    @FXML
    private VBox actionPane;

    private final ObservableList<Object> leftItems = FXCollections.observableArrayList();
    private final Set<Integer> selectedActionIds = new HashSet<>();
    private final DashboardNodeFactory nodeFactory = new DashboardNodeFactory();

    private DashboardMode currentMode = DashboardMode.INCIDENTS;
    private DashboardModeHandlerFactory modeHandlerFactory;
    private DashboardSubviewFactory subviewFactory;

    @FXML
    private void initialize() {
        leftListView.setItems(leftItems);
        leftListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                handleSelection(newValue);
            }
        });
    }

    public void initializeDashboard(DashboardDataService dashboardDataService,
                                    JourneyDetailNavigator journeyDetailNavigator) {
        this.subviewFactory = new DashboardSubviewFactory();
        this.modeHandlerFactory = new DashboardModeHandlerFactory(dashboardDataService, subviewFactory, journeyDetailNavigator);
        npsValueLabel.setText(String.valueOf(Math.round(dashboardDataService.getCompanyNps())));
        incidentsToggle.setSelected(true);
        configureMode(DashboardMode.INCIDENTS);
    }

    @FXML
    private void showIncidentsMode() {
        configureMode(DashboardMode.INCIDENTS);
    }

    @FXML
    private void showCustomersMode() {
        configureMode(DashboardMode.CUSTOMERS);
    }

    private void configureMode(DashboardMode mode) {
        currentMode = mode;
        DashboardModeHandler handler = modeHandlerFactory.create(mode);

        incidentsToggle.setSelected(mode == DashboardMode.INCIDENTS);
        customersToggle.setSelected(mode == DashboardMode.CUSTOMERS);
        listTitleLabel.setText(handler.getListTitle());
        leftListView.setCellFactory(listView -> handler.createListCell());
        leftItems.setAll(handler.loadItems(ADVISOR_ID));

        if (leftItems.isEmpty()) {
            showEmptyDetail("Dashboard Detail", handler.getEmptyDetailMessage());
            showEmptyActions(handler.getEmptyActionsMessage());
            return;
        }

        leftListView.getSelectionModel().selectFirst();
    }

    private void handleSelection(Object selection) {
        modeHandlerFactory.create(currentMode).handleSelection(selection, this);
    }

    private void refreshCurrentSelection() {
        Object selected = leftListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            handleSelection(selected);
        }
    }

    @Override
    public void showDetail(Node node) {
        detailPane.getChildren().setAll(node);
    }

    @Override
    public void showActions(Node node) {
        actionPane.getChildren().setAll(node);
    }

    @Override
    public void showEmptyDetail(String title, String message) {
        detailPane.getChildren().setAll(
                nodeFactory.createSectionTitle(title),
                nodeFactory.createEmptyStateCard(message)
        );
    }

    @Override
    public void showEmptyActions(String message) {
        var actionView = subviewFactory.loadActionPanelView();
        actionView.controller().showEmptyState(message);
        actionPane.getChildren().setAll(actionView.root());
    }

    @Override
    public boolean isActionSelected(int actionId) {
        return selectedActionIds.contains(actionId);
    }

    @Override
    public void selectAction(int actionId) {
        selectedActionIds.add(actionId);
        refreshCurrentSelection();
    }
}
