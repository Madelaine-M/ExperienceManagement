package repository.interfaces;

import model.Incident;
import model.enums.IncidentStatus;
import java.util.List;

public interface IncidentRepository {
    void save(Incident incident);
    Incident findById(int id);
    List<Incident> findAllByCustomerId(int customerId);
    void deleteById(int id);
    // alle offenen Probleme
    List<Incident> findByStatus(IncidentStatus status);
    List<Incident> findUnassigned();
    List<Incident> findByAdvisorId(Long advisorId);
}
