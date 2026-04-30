package service.interfaces.frontend;

import model.domain.Incident;
import model.view.IncidentDetailView;
import model.view.IncidentOverview;
import model.enums.IncidentStatus;

import java.util.List;

public interface IncidentService {

   void save(Incident incident);

    Incident findById(int id);

    List<Incident> findAllByCustomerId(int customerId);

    List<Incident> findByStatus(IncidentStatus status);

    List<Incident> findUnassigned();

    List<Incident> findByAdvisorId(int advisorId);

    List<Incident> findPendingActionItems();

    void updateStatus(int id, IncidentStatus status);

    List<IncidentOverview> findOpenOverviewsByAdvisorId(int advisorId);

    IncidentDetailView findDetailByIncidentId(int incidentId);

}
