package service.implementation.cv.cvRules;

import org.jetbrains.annotations.NotNull;
import service.implementation.cv.CVCustomer;
import service.interfaces.internal.CVScoringRuleService;

public class MarketingConsentRule implements CVScoringRuleService {
    @Override
    public int evaluate(@NotNull CVCustomer cvc) {
        if (cvc.hasMarketingConsent()){
            return 15;
        }
        return 0;
    }
}
