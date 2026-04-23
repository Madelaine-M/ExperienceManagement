package repository.interfaces;

import model.CustomerDetailView;
import model.CustomerOverview;

import java.util.List;

public interface CustomerView {

    List<CustomerOverview> findOverviewsByAdvisorId(int advisorId);

    CustomerDetailView findDetailByCustomerId(int customerId);
}
