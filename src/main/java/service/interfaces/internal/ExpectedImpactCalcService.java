package service.interfaces.internal;

import model.enums.IncidentType;

public interface ExpectedImpactCalcService {

    int calculateDefaultScoreImpact(IncidentType type);

    int calculateRevenueImpact(int impact); //Klasse für Packages muss eingeführt werden

    int calculateRecImpact(int impact);

    int calculateRebImpact(int impact);
}
