package service.implementation.suggestion.food;

import model.domain.ActionItem;
import model.domain.Incident;
import service.interfaces.suggestions.SuggestionStrategy;

public class FoodStandardSuggestionStrategy implements SuggestionStrategy {
    @Override
    public void apply(ActionItem actionItem, Incident incident) {
        actionItem.setSuggestion1("Vintage Wine Cellar Starter-Kit");
        actionItem.setSuggestion2("Complimentary Meal Voucher on next flight");
    }
}
