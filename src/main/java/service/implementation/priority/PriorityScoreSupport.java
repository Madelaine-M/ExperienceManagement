package service.implementation.priority;

import model.enums.IncidentType;
import service.interfaces.internal.ExpectedImpactCalcService;

public final class PriorityScoreSupport {
    private PriorityScoreSupport() {
    }

    public static double calculate(IncidentType incidentType,
                                   float customerCvScore,
                                   ExpectedImpactCalcService expectedImpactCalcService) {
        int defaultScoreImpact = expectedImpactCalcService.calculateDefaultScoreImpact(incidentType);
        int revenueImpact = expectedImpactCalcService.calculateRevenueImpact(defaultScoreImpact);

        return (getBaseSeverity(incidentType) * customerCvScore)
                + (defaultScoreImpact * 10)
                + (revenueImpact / 2000.0);
    }

    private static double getBaseSeverity(IncidentType type) {
        return type == IncidentType.DELAY ? 1.3 : 1.5;
    }

}