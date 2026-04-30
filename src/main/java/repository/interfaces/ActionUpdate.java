package repository.interfaces;

import model.domain.ActionItem;

public interface ActionUpdate {

    void save(ActionItem actionItem);

    void updateStatus(int id, String status);

}
