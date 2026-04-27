package service.interfaces.internal;

import model.Customer;
import service.implementation.cv.CVCustomer;


public interface CVScoringRuleService {
    int evaluate(CVCustomer cvc);
}
