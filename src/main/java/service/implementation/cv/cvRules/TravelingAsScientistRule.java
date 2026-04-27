package service.implementation.cv.cvRules;

import model.enums.CustomerType;
import service.implementation.cv.CVCustomer;
import service.interfaces.internal.CVScoringRuleService;

public class TravelingAsScientistRule implements CVScoringRuleService {
    @Override
    public int evaluate(CVCustomer cvc) {
        if (cvc.isTravelingAsScientist() == CustomerType.SCIENTIST){
            return 5;
        }
        return 0;
    }
}
