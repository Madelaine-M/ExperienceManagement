package service.implementation.cv.cvRules;


import org.jetbrains.annotations.NotNull;
import service.implementation.cv.CVCustomer;
import service.interfaces.internal.CVScoringRuleService;

public class PublicFigureRule implements CVScoringRuleService {
    @Override
    public int evaluate(@NotNull CVCustomer cvc) {
        if (cvc.isPublicFigure()){
            return 10;
        }
        return 0;
    }
}
