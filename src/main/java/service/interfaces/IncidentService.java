package service.interfaces;

import model.Incident;
import model.IncidentDetailView;
import model.IncidentOverview;
import model.enums.IncidentStatus;

import java.util.List;

public interface IncidentService {

   void save(Incident incident);

    Incident findById(int id);

    List<Incident> findAllByCustomerId(int customerId);

    List<Incident> findByStatus(IncidentStatus status);

    List<Incident> findUnassigned();

    List<Incident> findByAdvisorId(Long advisorId);

    List<Incident> findPendingActionItems();

    void updateStatus(int id, IncidentStatus status);

    List<IncidentOverview> findAllPrioritizedOverviews();

    IncidentDetailView findDetailByIncidentId(int incidentId);

}
