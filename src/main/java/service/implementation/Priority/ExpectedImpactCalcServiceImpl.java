package service.implementation.Priority;

import model.enums.IncidentType;
import service.interfaces.internal.ExpectedImpactCalcService;

public class ExpectedImpactCalcServiceImpl implements ExpectedImpactCalcService {
    @Override
    public int calculateDefaultScoreImpact(IncidentType type) {
        return 0;
    }

    @Override
    public int calculateRevenueImpact(int impact) {
        return impact;
    }

    @Override
    public int calculateRecImpact(int impact) {
        return impact;
    }

    @Override
    public int calculateRebImpact(int impact) {
        return impact;
    }
}
