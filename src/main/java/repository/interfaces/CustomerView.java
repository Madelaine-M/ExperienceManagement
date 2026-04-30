package repository.interfaces;

import model.view.CustomerDetailView;
import model.view.CustomerOverview;

import java.util.List;

public interface CustomerView {

    List<CustomerOverview> findOverviewsByAdvisorId(int advisorId);

    CustomerDetailView findDetailByCustomerId(int customerId);
}
