package service.interfaces.suggestions;


import model.domain.ActionItem;
import model.domain.Incident;

import java.util.List;

public class SuggestionService {

    private final List<SuggestionResolver> resolvers;

    public SuggestionService(List<SuggestionResolver> resolvers) {
        this.resolvers = resolvers;
    }

    public void applySuggestions(ActionItem actionItem, Incident incident) {
        resolvers.stream()
                .filter(r -> r.supports(incident))
                .findFirst()
                .map(r -> r.resolve(incident))
                .orElseThrow(() -> new IllegalArgumentException(
                        "No resolver found for incident type: " + incident.getType()))
                .apply(actionItem, incident);
    }
}
