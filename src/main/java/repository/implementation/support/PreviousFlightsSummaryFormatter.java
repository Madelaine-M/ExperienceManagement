package repository.implementation.support;

import model.Flight;

import java.util.List;

public class PreviousFlightsSummaryFormatter {

    public String format(List<Flight> previousFlights) {
        if (previousFlights.isEmpty()) {
            return "No previous flight data";
        }

        return previousFlights.size() + " previous flight(s)";
    }
}
