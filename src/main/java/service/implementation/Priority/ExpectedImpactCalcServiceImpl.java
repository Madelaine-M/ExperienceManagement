package service.implementation.Priority;

import model.enums.IncidentType;
import service.interfaces.internal.ExpectedImpactCalcService;

public class ExpectedImpactCalcServiceImpl implements ExpectedImpactCalcService {
    @Override
    public double calculateDefaultScoreImpact(IncidentType type) {
        return 0;
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
}
