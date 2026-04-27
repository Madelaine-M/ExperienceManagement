package service.implementation.suggestion.hotel;


import model.ActionItem;
import model.Incident;
import service.interfaces.suggestions.SuggestionStrategy;

public class HotelGoldSuggestionStrategy implements SuggestionStrategy {
    @Override
    public void apply(ActionItem actionItem, Incident incident) {
        actionItem.setSugegstion1("Upgrade to presidential Suite for their next trip");
        actionItem.setSuggestion2("Private Spa-Day after next trip");
    }
}
