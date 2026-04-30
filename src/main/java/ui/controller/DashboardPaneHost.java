package ui.controller;

import javafx.scene.Node;

public interface DashboardPaneHost {

    void showDetail(Node node);

    void showActions(Node node);

    void showEmptyDetail(String title, String message);

    void showEmptyActions(String message);

    void refreshDashboardData();
}
