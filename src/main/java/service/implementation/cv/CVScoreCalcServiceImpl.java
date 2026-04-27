package service.implementation.cv;

import model.Customer;
import service.interfaces.internal.CVScoreCalcService;

import java.util.List;

import service.implementation.cv.cvRules.*;
import service.interfaces.internal.CVScoringRuleService;

public class CVScoreCalcServiceImpl implements CVScoreCalcService {
    private final List<CVScoringRuleService> rules = List.of(
        new LastBookingTierRule(),
        new CurrentBookingStatusRule(),
        new ReturningCustomerRule(),
        new MarketingConsentRule(),
        new BookingConnectionRule(),
        new PaymentMethodRule(),
        new PublicFigureRule(),
        new TravelingAsScientistRule(),
        new NewsletterSubscriptionRule()
    );
    private static int score = 0;
    @Override
    public int calculate(CVCustomer customer) {
        for (CVScoringRuleService rule : rules){
            score += rule.evaluate(customer);
        }
        return score;
    }
}
