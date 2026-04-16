package repository.interfaces;

import model.Flight;

import java.util.List;

public interface FlightRepository {

    void save(Flight flight);

    Flight findById(int id);

    Flight findCurrentByCustomerId(int customerId);

    Flight findLatestPreviousByCustomerId(int customerId);

    List<Flight> findByCustomerId(int customerId);

    List<Flight> findPreviousByCustomerId(int customerId);
}
