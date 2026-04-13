package repository.interfaces;

import model.ActionItem;

public interface ActionUpdate {

    void save(ActionItem actionItem);

    void updateStatus(int id, String status);

}
