package service.implementation.cv.cvRules;

import model.domain.CVCustomer;
import model.enums.CustomerType;
import service.interfaces.internal.CVScoringRuleService;

public class TravelingAsScientistRule implements CVScoringRuleService {
    @Override
    public int evaluate(CVCustomer cvc) {
        if (cvc.getCustomerType() == CustomerType.SCIENTIST) {
            return 10;
        }
        return 0;
    }
}