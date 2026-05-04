package service.implementation.cv.cvRules;

import model.enums.Packages;
import service.implementation.cv.CVCustomer;
import service.interfaces.internal.CVScoringRuleService;

public class LastBookingTierRule implements CVScoringRuleService {


    @Override
    public int evaluate(CVCustomer cvc) {
        if (!cvc.isReturningCustomer()){
            return 0;
        }
        if (cvc.getLastBookingPackage().orElse(null) != Packages.STANDARD){
            return 20;
        }
        else {
            return 0;
        }
    }
}
