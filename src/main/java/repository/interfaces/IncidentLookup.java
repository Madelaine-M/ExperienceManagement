package repository.interfaces;

import model.Incident;

import java.util.List;

public interface IncidentLookup {

    Incident findById(int id);

    List<Incident> findAllByCustomerId(int customerId);

}
