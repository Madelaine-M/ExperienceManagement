package service.interfaces.internal;

import model.domain.ActionItem;
import model.domain.Incident;

public interface CreateSuggestedActionService {

    ActionItem createSuggestedAction(Incident incident);
}
