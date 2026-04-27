package service.interfaces.frontend;

import model.ActionItem;

import java.util.List;

public interface ActionService {

    List<ActionItem> findPriorityActionsForAdvisor(int advisorId);

    void save(ActionItem actionItem);

    void updateStatus(int id, String status);

    ActionItem findById(int id);

    List<ActionItem> findByIncidentId(int incidentId);

    String getSystemRec(ActionItem actionItem); //potenzieller SOLID Verstoß

    String getImpact(ActionItem actionItem);//potenzieller SOLID Verstoß
}
