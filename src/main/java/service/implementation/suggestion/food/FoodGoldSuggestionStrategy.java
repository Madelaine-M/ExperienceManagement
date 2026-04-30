package service.implementation.suggestion.food;

import model.domain.ActionItem;
import model.domain.Incident;
import service.interfaces.suggestions.SuggestionStrategy;

public class FoodGoldSuggestionStrategy implements SuggestionStrategy {
    @Override
    public void apply(ActionItem actionItem, Incident incident) {
        actionItem.setSuggestion1("Pre-Paid Table Reservation at a high class restaurant the day before the next flight");
        actionItem.setSuggestion2("Vintage Wine Cellar Starter-Kit");
    }
}
