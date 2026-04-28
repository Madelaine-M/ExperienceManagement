package service.implementation.suggestion.hotel;

import model.ActionItem;
import model.Incident;
import service.interfaces.suggestions.SuggestionStrategy;

public class HotelStandardSuggestionStrategy implements SuggestionStrategy {
    @Override
    public void apply(ActionItem actionItem, Incident incident) {
        actionItem.setSugegstion1("Private Spa-Day after next trip");
        actionItem.setSuggestion2("Room Upgrade Voucher for next booking");
    }
}
