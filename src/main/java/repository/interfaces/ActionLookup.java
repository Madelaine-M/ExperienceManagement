package repository.interfaces;

import model.domain.ActionItem;

import java.util.List;

public interface ActionLookup {

    ActionItem findById(int id);

    List<ActionItem> findByIncidentId(int incidentId);
}
