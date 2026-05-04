package service.implementation.cv.cvRules;

import model.domain.CVCustomer;
import model.enums.Packages;
import service.interfaces.internal.CVScoringRuleService;

public class CurrentBookingStatusRule implements CVScoringRuleService {
    @Override
    public int evaluate(CVCustomer cvc) {
        if (cvc.getCurrentBookingPackage() != Packages.STANDARD) {
            return 15;
        }
        return 5;
    }
}