package repository.interfaces;

import model.ActionItem;

import java.util.List;

public interface ActionRepository {

    void save(ActionItem actionItem);

    ActionItem findById(int id);

    List<ActionItem> findByIncidentId(int incidentId);

    void deleteById(int id);
}
