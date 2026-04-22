package service.calculation;

import model.enums.IncidentType;
import model.enums.Packages;

public interface ExpectedImpactCalculatorService {

    double calculateDefaultScoreImpact(IncidentType type);

    double calculateRevenueImpact(); //Klasse für Packages muss eingeführt werden

    double calculateRecImpact();

    double calculateRebImpact();
}
