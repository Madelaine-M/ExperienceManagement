package service.implementation.suggestion;

import model.Flight;
import model.Incident;
import model.enums.Packages;
import repository.interfaces.FlightRepository;
import service.interfaces.suggestions.PackageResolver;

public class FlightPackageResolver implements PackageResolver {

    private final FlightRepository flightRepository;

    public FlightPackageResolver(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    @Override
    public Packages getPackage(Incident incident) {
        Flight flight = flightRepository.findCurrentByCustomerId(incident.getCustomerId());

        if (flight == null) {
            throw new IllegalStateException(
                    "No current flight found for customerId: " + incident.getCustomerId()
            );
        }

        Packages pkg = flight.getBookingPackage();

        if (pkg == null) {
            throw new IllegalStateException(
                    "No booking package found on flight for customerId: " + incident.getCustomerId()
            );
        }

        return pkg;
    }
}
