package service.interfaces.frontend;

import model.domain.ActionItem;

import java.util.List;

public interface ActionService {

    void save(ActionItem actionItem);

    void updateStatus(int id, String status);

    ActionItem findById(int id);

    List<ActionItem> findByIncidentId(int incidentId);
}
