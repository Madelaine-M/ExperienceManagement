package service.implementation.cv.cvRules;

import model.domain.CVCustomer;
import service.interfaces.internal.CVScoringRuleService;

public class ReturningCustomerRule implements CVScoringRuleService {

    @Override
    public int evaluate(CVCustomer cvc) {
        if (cvc.isReturningCustomer()) {
            return 10;
        }
        return 0;
    }
}