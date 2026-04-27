package service.interfaces.internal;

import model.enums.IncidentType;

public interface ExpectedImpactCalcService {

    int calculateDefaultScoreImpact(IncidentType type);

    int calculateRevenueImpact(); //Klasse für Packages muss eingeführt werden

    int calculateRecImpact();

    int calculateRebImpact();
}
