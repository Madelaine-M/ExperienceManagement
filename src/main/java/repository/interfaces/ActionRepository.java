package repository.interfaces;

import model.ActionItem;
import model.enums.PriorityLevel;

import java.util.List;

public interface ActionRepository {

    void save(ActionItem actionItem);

    ActionItem findById(int id);

    List<ActionItem> findByIncidentId(int incidentId);

    List<ActionItem> findByStatus(String status);

    List<ActionItem> filterByPriority(PriorityLevel prio);

    void deleteById(int id);
}
