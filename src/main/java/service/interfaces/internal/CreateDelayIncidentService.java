package service.interfaces.internal;

import model.domain.DelayIncident;

public interface CreateDelayIncidentService {

    DelayIncident createDelayIncident(int customerId, int delayMinutes, String description);
}
