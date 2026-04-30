package service.implementation;

import model.domain.ActionItem;
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
    public List<ActionItem> findSuggestedActionsByAdvisorId(int advisorId) {
        return actionManagement.findSuggestedActionsByAdvisorId(advisorId);
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
}
