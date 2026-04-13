package repository.interfaces;

import model.Incident;
import model.enums.IncidentStatus;

import java.util.List;

public interface IncidentManagement {

    List<Incident> findByStatus(IncidentStatus status);

    List<Incident> findUnassigned();

    List<Incident> findByAdvisorId(Long advisorId);

    List<Incident> findPendingActionItems();

}
