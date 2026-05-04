package service.implementation.Priority;

import model.enums.IncidentType;
import service.interfaces.internal.ExpectedImpactCalcService;

public class ExpectedImpactCalcServiceImpl implements ExpectedImpactCalcService {
    @Override
    public int calculateDefaultScoreImpact(IncidentType type) {
        return switch (type) {
            case FEEDBACK -> 2;
            case DELAY -> 3;
            case ONBOARDING -> 2;
        };
    }

    @Override
    public int calculateRevenueImpact(int impact) {
        return impact * 5000;
    }

    @Override
    public int calculateRecImpact(int impact) {
        return Math.max(1, impact);
    }

    @Override
    public int calculateRebImpact(int impact) {
        return Math.max(1, impact);
    }
}
