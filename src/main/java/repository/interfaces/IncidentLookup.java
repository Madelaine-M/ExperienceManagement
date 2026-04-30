package repository.interfaces;

import model.domain.Incident;

import java.util.List;

public interface IncidentLookup {

    Incident findById(int id);

    List<Incident> findAllByCustomerId(int customerId);

    boolean existsByFeedbackId(int feedbackId);

    boolean existsBySourceFeedbackItemId(int sourceFeedbackItemId);

    boolean existsOpenDelayIncidentForCustomerFlight(int customerId, Integer flightId);

}
