package service.implementation;

import model.ActionItem;
import org.jetbrains.annotations.NotNull;
import repository.interfaces.ActionLookup;
import repository.interfaces.ActionManagement;
import repository.interfaces.ActionUpdate;
import service.interfaces.frontend.ActionService;

import java.util.List;


public class ActionServiceImpl implements ActionService {
    private final ActionLookup actionLookup;
    private final ActionManagement actionManagement;
    private final ActionUpdate actionUpdate;
    public ActionServiceImpl(ActionLookup actionLookup, ActionManagement actionManagement,  ActionUpdate actionUpdate) {
        this.actionLookup = actionLookup;
        this.actionManagement = actionManagement;
        this.actionUpdate = actionUpdate;
    }
    @Override
    public List<ActionItem> findPriorityActionsForAdvisor(int advisorId) {
        return actionManagement.findPriorityActionsForAdvisor(advisorId);
    }

    @Override
    public void save(ActionItem actionItem) {
        actionUpdate.save(actionItem);
    }

    @Override
    public void updateStatus(int id, String status) {
        actionUpdate.updateStatus(id, status);
    }

    @Override
    public ActionItem findById(int id) {
        return actionLookup.findById(id);
    }

    @Override
    public List<ActionItem> findByIncidentId(int incidentId) {
        return actionLookup.findByIncidentId(incidentId);
    }

    @Override
    public String getSystemRec(@org.jetbrains.annotations.NotNull ActionItem actionItem) { //Logging?
        return ("Rec1: "+actionItem.getSugegstion1() +
                "OR"
                + "Rec2: "+actionItem.getSuggestion2());
    }

    @Override
    public String getImpact(@NotNull ActionItem actionItem) { //Logging?
        return "Recommendation: " + actionItem.getExpectedRec() +
                "Book again: " + actionItem.getExpectedRebooking();
    }
}
