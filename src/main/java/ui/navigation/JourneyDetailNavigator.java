package ui.navigation;

public interface JourneyDetailNavigator {
    void openJourney(int customerId, Integer incidentId);

    void openFlightDetail(int flightId);

    void goBack();
}
