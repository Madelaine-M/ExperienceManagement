package service.implementation.cv.cvRules;

import org.jetbrains.annotations.NotNull;
import service.implementation.cv.CVCustomer;
import service.interfaces.internal.CVScoringRuleService;

public class NewsletterSubscriptionRule implements CVScoringRuleService {
    @Override
    public int evaluate(@NotNull CVCustomer cvc) {
        if (cvc.hasNewsletterSubscription()){
            return 5;
        }
        return 0;
    }
}
