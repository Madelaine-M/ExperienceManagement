package service.implementation.priority;

import model.domain.Incident;
import model.enums.IncidentType;
import service.interfaces.internal.CustomerCvScoreService;
import service.interfaces.internal.ExpectedImpactCalcService;
import service.interfaces.internal.PriorityCalcService;

public class PriorityCalcServiceImpl implements PriorityCalcService {
    private final CustomerCvScoreService customerCvScoreService;
    private final ExpectedImpactCalcService expectedImpactCalcService;

    public PriorityCalcServiceImpl(CustomerCvScoreService customerCvScoreService,
                                   ExpectedImpactCalcService expectedImpactCalcService) {
        this.customerCvScoreService = customerCvScoreService;
        this.expectedImpactCalcService = expectedImpactCalcService;
    }

    @Override
    public double calculate(Incident incident) {
        return calculate(incident.getType(), calculateCustomerCvScore(incident.getCustomerId()));
    }

    @Override
    public float calculateCustomerCvScore(int customerId) {
        if (customerCvScoreService == null) {
            return 0.0f;
        }
        return customerCvScoreService.calculateForCustomerId(customerId);
    }

    @Override
    public double calculate(IncidentType incidentType, float customerCvScore) {
        return PriorityScoreSupport.calculate(incidentType, customerCvScore, expectedImpactCalcService);
    }
}