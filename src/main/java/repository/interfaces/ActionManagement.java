package repository.interfaces;

import model.ActionItem;
import model.enums.PriorityLevel;

import java.util.List;

public interface ActionManagement {

    List<ActionItem> findPriorityActionsForAdvisor(int advisorId);

}
