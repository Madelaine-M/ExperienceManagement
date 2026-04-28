package service.implementation.cv.cvRules;

import service.implementation.cv.CVCustomer;
import service.interfaces.internal.CVScoringRuleService;

public class NewsletterSubscriptionRule implements CVScoringRuleService {
    @Override
    public int evaluate(CVCustomer cvc) {
        if (cvc.hasNewsletterSubscription()){
            return 5;
        }
        return 0;
    }
}
