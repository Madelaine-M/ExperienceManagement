package service.interfaces.internal;

import model.domain.CVCustomer;

public interface CVScoringRuleService {
    int evaluate(CVCustomer cvc);
}