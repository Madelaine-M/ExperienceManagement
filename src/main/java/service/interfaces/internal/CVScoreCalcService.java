package service.interfaces.internal;

import model.Customer;
import service.implementation.cv.CVCustomer;

public interface CVScoreCalcService {
    int calculate(CVCustomer customer);
}
