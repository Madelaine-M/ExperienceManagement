package service.calculation;
//Expected Impact if nothing is done, and if recommended is done

import model.enums.IncidentType;
import model.enums.Packages;

public class ExpectedImpactCalculatorImpl implements ExpectedImpactCalculatorService {

    @Override
    public double calculateDefaultScoreImpact(IncidentType type) {
        if (type == IncidentType.FEEDBACK) {
            return 0.0; //Man muss iwie von Incident auf Feedback kommen, damit man TotalScore aufrufen kann
        }
        else {
            return getDelayImpact();
        }
    }

    @Override
    public double calculateRevenueImpact() {
        return 0;
    }

    @Override
    public double calculateRecImpact() {
        return 0;
    }

    @Override
    public double calculateRebImpact() {
        return 0;
    }

    private double getDelayImpact(){ //Room for complicated Predicting Algorithm
        return 0.0;
    }
}
