package service.implementation.suggestion.food;

import model.ActionItem;
import model.Incident;
import service.interfaces.suggestions.SuggestionStrategy;

public class FoodVipSuggestionStrategy implements SuggestionStrategy {
    @Override
    public void apply(ActionItem actionItem, Incident incident) {
        actionItem.setSugegstion1("Private Michelin Home-Cooking before the next flight");
        actionItem.setSuggestion2("Pre-Paid Table Reservation at a high class restaurant the day before the next flight");
    }
}
