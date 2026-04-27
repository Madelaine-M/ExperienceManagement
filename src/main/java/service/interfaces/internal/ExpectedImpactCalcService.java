package service.interfaces.internal;

import model.enums.IncidentType;

public interface ExpectedImpactCalcService {

    double calculateDefaultScoreImpact(IncidentType type);

    double calculateRevenueImpact(); //Klasse für Packages muss eingeführt werden

    double calculateRecImpact();

    double calculateRebImpact();
}
