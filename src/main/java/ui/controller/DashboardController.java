package ui.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import model.domain.Advisor;
import model.view.CustomerOverview;
import model.view.IncidentOverview;
import ui.navigation.JourneyDetailNavigator;
import ui.service.DashboardDataService;
import ui.view.factory.DashboardSubviewFactory;
import ui.view.render.DashboardNodeFactory;

public class DashboardController implements DashboardPaneHost {
    @FXML
    private Label npsValueLabel;
    @FXML
    private Label listTitleLabel;
    @FXML
    private Label advisorValueLabel;
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
    private final DashboardNodeFactory nodeFactory = new DashboardNodeFactory();
    private final Timeline refreshTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> refreshDashboard()));

    private DashboardMode currentMode = DashboardMode.INCIDENTS;
    private DashboardModeHandlerFactory modeHandlerFactory;
    private DashboardSubviewFactory subviewFactory;
    private DashboardDataService dashboardDataService;
    private int advisorId;
    private Runnable backToSimulationAction;

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
                                    JourneyDetailNavigator journeyDetailNavigator,
                                    Advisor advisor,
                                    Runnable backToSimulationAction) {
        this.dashboardDataService = dashboardDataService;
        this.subviewFactory = new DashboardSubviewFactory();
        this.modeHandlerFactory = new DashboardModeHandlerFactory(dashboardDataService, subviewFactory, journeyDetailNavigator);
        this.advisorId = advisor.getId();
        this.backToSimulationAction = backToSimulationAction;
        advisorValueLabel.setText(advisor.getFirstName() + " " + advisor.getLastName());
        incidentsToggle.setSelected(true);
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
        configureMode(DashboardMode.INCIDENTS);
    }

    public void stopAutoRefresh() {
        refreshTimeline.stop();
    }

    @FXML
    private void goBackToSimulation() {
        if (backToSimulationAction != null) {
            backToSimulationAction.run();
        }
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
        try {
            updateNpsDisplay();
            leftItems.setAll(handler.loadItems(advisorId));
        } catch (RuntimeException exception) {
            leftItems.clear();
            showErrorState("Dashboard data could not be loaded right now.");
            return;
        }

        if (leftItems.isEmpty()) {
            showEmptyDetail("Dashboard Detail", handler.getEmptyDetailMessage());
            showEmptyActions(handler.getEmptyActionsMessage());
            return;
        }

        leftListView.getSelectionModel().selectFirst();
    }

    private void refreshDashboard() {
        if (dashboardDataService == null || modeHandlerFactory == null) {
            return;
        }

        Object previousSelection = leftListView.getSelectionModel().getSelectedItem();
        SelectionKey selectionKey = SelectionKey.from(previousSelection);
        DashboardModeHandler handler = modeHandlerFactory.create(currentMode);

        try {
            updateNpsDisplay();
            leftItems.setAll(handler.loadItems(advisorId));
        } catch (RuntimeException exception) {
            leftItems.clear();
            showErrorState("Dashboard data could not be refreshed right now.");
            return;
        }

        if (leftItems.isEmpty()) {
            showEmptyDetail("Dashboard Detail", handler.getEmptyDetailMessage());
            showEmptyActions(handler.getEmptyActionsMessage());
            return;
        }

        Object matchingItem = findMatchingItem(selectionKey);
        if (matchingItem != null) {
            leftListView.getSelectionModel().select(matchingItem);
        } else if (leftListView.getSelectionModel().getSelectedItem() == null) {
            leftListView.getSelectionModel().selectFirst();
        } else {
            refreshCurrentSelection();
        }
    }

    private Object findMatchingItem(SelectionKey selectionKey) {
        if (selectionKey == null) {
            return null;
        }
        for (Object item : leftItems) {
            if (selectionKey.matches(item)) {
                return item;
            }
        }
        return null;
    }

    private void handleSelection(Object selection) {
        try {
            modeHandlerFactory.create(currentMode).handleSelection(selection, this);
        } catch (RuntimeException exception) {
            showErrorState("Selection details could not be loaded right now.");
        }
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
    public void refreshDashboardData() {
        refreshDashboard();
    }

    private void updateNpsDisplay() {
        long roundedNps = Math.round(dashboardDataService.getCompanyNps());
        npsValueLabel.setText(String.valueOf(roundedNps));
        npsValueLabel.getStyleClass().removeAll("nps-positive", "nps-negative");
        npsValueLabel.getStyleClass().add(roundedNps < 0 ? "nps-negative" : "nps-positive");
    }

    private void showErrorState(String message) {
        showEmptyDetail("Dashboard Detail", message);
        showEmptyActions(message);
    }

    private record SelectionKey(String type, int id) {
        static SelectionKey from(Object item) {
            if (item instanceof IncidentOverview overview) {
                return new SelectionKey("incident", overview.getIncidentId());
            }
            if (item instanceof CustomerOverview overview) {
                return new SelectionKey("customer", overview.getCustomerId());
            }
            return null;
        }

        boolean matches(Object item) {
            if ("incident".equals(type) && item instanceof IncidentOverview overview) {
                return overview.getIncidentId() == id;
            }
            if ("customer".equals(type) && item instanceof CustomerOverview overview) {
                return overview.getCustomerId() == id;
            }
            return false;
        }
    }
}
