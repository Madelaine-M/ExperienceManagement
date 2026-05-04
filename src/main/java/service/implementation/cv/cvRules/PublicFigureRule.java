package service.implementation.cv.cvRules;

import model.domain.CVCustomer;
import service.interfaces.internal.CVScoringRuleService;

public class PublicFigureRule implements CVScoringRuleService {
    @Override
    public int evaluate(CVCustomer cvc) {
        if (cvc.isPublicFigure()) {
            return 15;
        }
        return 0;
    }
}