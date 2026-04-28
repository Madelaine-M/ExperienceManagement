package ui.controller;

import javafx.scene.control.ListCell;

import java.util.List;

public interface DashboardModeHandler {

    String getListTitle();

    List<Object> loadItems(int advisorId);

    ListCell<Object> createListCell();

    String getEmptyDetailMessage();

    String getEmptyActionsMessage();

    void handleSelection(Object selection, DashboardPaneHost host);
}
