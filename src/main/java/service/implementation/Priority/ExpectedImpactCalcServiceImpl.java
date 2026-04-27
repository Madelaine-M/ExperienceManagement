package service.implementation.Priority;

import model.enums.IncidentType;
import service.interfaces.internal.ExpectedImpactCalcService;

public class ExpectedImpactCalcServiceImpl implements ExpectedImpactCalcService {
    @Override
    public int calculateDefaultScoreImpact(IncidentType type) {
        return 0;
    }

    @Override
    public int calculateRevenueImpact() {
        return 0;
    }

    @Override
    public int calculateRecImpact() {
        return 0;
    }

    @Override
    public int calculateRebImpact() {
        return 0;
    }
}
