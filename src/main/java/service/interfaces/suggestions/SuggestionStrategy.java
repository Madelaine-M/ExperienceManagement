package service.interfaces.suggestions;

import model.ActionItem;
import model.Incident;

public interface SuggestionStrategy {
    void apply(ActionItem actionItem, Incident incident);
}
