package service.interfaces.internal;

import model.domain.CVCustomer;

public interface CVScoreCalcService {
    int calculate(CVCustomer cvCustomer);
}