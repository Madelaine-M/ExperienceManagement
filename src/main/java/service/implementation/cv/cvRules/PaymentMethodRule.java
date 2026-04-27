package service.implementation.cv.cvRules;

import service.implementation.cv.CVCustomer;
import service.interfaces.internal.CVScoringRuleService;

public class PaymentMethodRule implements CVScoringRuleService {
    @Override
    public int evaluate(CVCustomer cvc) {
        switch (cvc.getPaymentMethod()) {
            case IMMEDIATE -> {
                return 10;
            }
            case MONTHS ->  {
                return 5;
            }
            case LONGTIME ->  {
                return 0;
            }
        }
        return 0;
    }
}
