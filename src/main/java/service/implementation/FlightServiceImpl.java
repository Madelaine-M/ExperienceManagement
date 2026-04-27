package service.implementation;

import model.Flight;
import repository.interfaces.FlightRepository;
import service.interfaces.frontend.FlightService;

import java.util.List;

public class FlightServiceImpl implements FlightService {
    private final FlightRepository flightRepository;
    public FlightServiceImpl(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }
    @Override
    public void save(Flight flight) {
        flightRepository.save(flight);
    }

    @Override
    public Flight findById(int id) {
        return flightRepository.findById(id);
    }

    @Override
    public Flight findCurrentByCustomerId(int customerId) {
        return flightRepository.findCurrentByCustomerId(customerId);
    }

    @Override
    public Flight findLatestPreviousByCustomerId(int customerId) {
        return flightRepository.findLatestPreviousByCustomerId(customerId);
    }

    @Override
    public List<Flight> findByCustomerId(int customerId) {
        return flightRepository.findByCustomerId(customerId);
    }

    @Override
    public List<Flight> findPreviousByCustomerId(int customerId) {
        return flightRepository.findPreviousByCustomerId(customerId);
    }
}
