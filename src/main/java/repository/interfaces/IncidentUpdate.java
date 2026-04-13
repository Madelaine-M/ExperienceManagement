package repository.interfaces;

import model.Incident;
import model.enums.IncidentStatus;

public interface IncidentUpdate {

    void save(Incident incident);

    void updateStatus(int id, IncidentStatus status);

}
