package repository.interfaces;

import model.domain.ActionItem;

import java.util.List;

public interface ActionManagement {

    List<ActionItem> findSuggestedActionsByAdvisorId(int advisorId);

}
