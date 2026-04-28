package service.implementation.cv.cvRules;

import model.enums.Packages;
import service.implementation.cv.CVCustomer;
import service.interfaces.internal.CVScoringRuleService;

public class CurrentBookingStatusRule implements CVScoringRuleService {
    @Override
    public int evaluate(CVCustomer cvc) {
        if (cvc.getCurrentBookingPackage() != Packages.STANDARD){
            return 10;
        }
        return 5;
    }
}
