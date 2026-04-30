package service.interfaces.internal;

import model.domain.Customer;
import service.implementation.cv.CVCustomer;

public interface CVScoreCalcService {
    int calculate(CVCustomer customer);
}
