package service.implementation.cv.cvRules;

import model.domain.CVCustomer;
import service.interfaces.internal.CVScoringRuleService;

public class PaymentMethodRule implements CVScoringRuleService {
    @Override
    public int evaluate(CVCustomer cvc) {
        return switch (cvc.getPaymentMethod()) {
            case IMMEDIATE -> 10;
            case MONTHS -> 5;
            case LONGTIME -> 0;
        };
    }
}