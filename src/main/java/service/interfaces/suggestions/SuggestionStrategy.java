package service.interfaces.suggestions;

import model.domain.ActionItem;
import model.domain.Incident;

public interface SuggestionStrategy {
    void apply(ActionItem actionItem, Incident incident);
}
