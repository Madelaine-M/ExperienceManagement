package simulation.engine;

import model.domain.Incident;
import model.enums.IncidentStatus;
import repository.interfaces.IncidentLookup;

import java.util.List;

class OpenIncidentBlockPolicy {
    private final IncidentLookup incidentLookup;

    OpenIncidentBlockPolicy(IncidentLookup incidentLookup) {
        this.incidentLookup = incidentLookup;
    }

    boolean hasOpenIncident(int customerId) {
        List<Incident> incidents = incidentLookup.findAllByCustomerId(customerId);
        for (Incident incident : incidents) {
            if (incident.getStatus() == IncidentStatus.OPEN) {
                return true;
            }
        }
        return false;
    }
}
