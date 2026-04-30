package service.interfaces.internal;

import model.domain.Incident;
import model.enums.IncidentType;


public interface PriorityCalcService {
    double calculate(Incident incident);

    float calculateCustomerCvScore(int customerId);

    double calculate(IncidentType incidentType, float customerCvScore);
}
