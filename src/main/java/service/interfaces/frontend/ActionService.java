package service.interfaces.frontend;

import model.domain.ActionItem;

import java.util.List;

public interface ActionService {

    List<ActionItem> findSuggestedActionsByAdvisorId(int advisorId);

    void save(ActionItem actionItem);

    void updateStatus(int id, String status);

    ActionItem findById(int id);

    List<ActionItem> findByIncidentId(int incidentId);
}
