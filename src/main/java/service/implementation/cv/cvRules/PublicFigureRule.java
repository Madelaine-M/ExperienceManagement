package service.implementation.cv.cvRules;

import service.implementation.cv.CVCustomer;
import service.interfaces.internal.CVScoringRuleService;

public class PublicFigureRule implements CVScoringRuleService {
    @Override
    public int evaluate(CVCustomer cvc) {
        if (cvc.isPublicFigure()){
            return 10;
        }
        return 0;
    }
}
