package service.implementation.incidents.action;

import config.SuggestionServiceConfig;
import model.domain.ActionItem;
import model.domain.Incident;
import model.enums.ActionStatus;
import repository.interfaces.ActionLookup;
import repository.interfaces.ActionUpdate;
import repository.interfaces.FlightRepository;
import service.implementation.suggestion.FlightPackageResolver;
import service.interfaces.internal.CreateSuggestedActionService;
import service.interfaces.suggestions.SuggestionService;

import java.util.List;

public class CreateSuggestedActionServiceImpl implements CreateSuggestedActionService {
    private final ActionLookup actionLookup;
    private final ActionUpdate actionUpdate;
    private final SuggestionService suggestionService;

    public CreateSuggestedActionServiceImpl(ActionLookup actionLookup,
                                            ActionUpdate actionUpdate,
                                            FlightRepository flightRepository) {
        this(
                actionLookup,
                actionUpdate,
                SuggestionServiceConfig.build(new FlightPackageResolver(flightRepository))
        );
    }

    public CreateSuggestedActionServiceImpl(ActionLookup actionLookup,
                                            ActionUpdate actionUpdate,
                                            SuggestionService suggestionService) {
        this.actionLookup = actionLookup;
        this.actionUpdate = actionUpdate;
        this.suggestionService = suggestionService;
    }

    @Override
    public ActionItem createSuggestedAction(Incident incident) {
        if (incident == null) {
            return null;
        }

        List<ActionItem> existing = actionLookup.findByIncidentId(incident.getId());
        if (existing != null && !existing.isEmpty()) {
            return existing.get(0);
        }

        ActionItem actionItem = new ActionItem(
                incident.getId(),
                defaultDescription(incident),
                null,
                null,
                ActionStatus.SUGGESTED,
                incident.getScoreImpact(),
                expectedRecommendationImpact(incident),
                expectedRebookingImpact(incident)
        );

        applySuggestionsSafely(actionItem, incident);
        actionUpdate.save(actionItem);
        return actionItem;
    }

    private void applySuggestionsSafely(ActionItem actionItem, Incident incident) {
        try {
            suggestionService.applySuggestions(actionItem, incident);
        } catch (RuntimeException exception) {
            if (actionItem.getSuggestion1() == null || actionItem.getSuggestion1().isBlank()) {
                actionItem.setSuggestion1("Reach out proactively to the customer and confirm the recovery plan.");
            }
            if (actionItem.getSuggestion2() == null || actionItem.getSuggestion2().isBlank()) {
                actionItem.setSuggestion2("Offer a tailored goodwill gesture and document the follow-up.");
            }
        }
    }

    private String defaultDescription(Incident incident) {
        return incident.getDescription() == null || incident.getDescription().isBlank()
                ? "Review incident and choose a recommended recovery action."
                : incident.getDescription();
    }

    private int expectedRecommendationImpact(Incident incident) {
        return Math.max(1, incident.getScoreImpact() / 2);
    }

    private int expectedRebookingImpact(Incident incident) {
        return Math.max(1, incident.getRevenueRisk() / 1000);
    }
}
