package repository.interfaces;

import model.domain.Incident;
import model.enums.IncidentStatus;

import java.util.List;

public interface IncidentManagement {

    List<Incident> findByStatus(IncidentStatus status);

    List<Incident> findUnassigned();

    List<Incident> findByAdvisorId(Integer advisorId);

    List<Incident> findPendingActionItems();

}
