package service.interfaces.frontend;

import model.domain.Customer;
import model.view.CustomerDetailView;
import model.view.CustomerOverview;

import java.util.List;

public interface CustomerService {

    Customer findById(int id);

    List<Customer> findAll();

    List<Customer> findByAdvisorId(int advisorId);

    List<Customer> findByStatus(String status);

    void save(Customer customer);

    void update(Customer customer);

    List<CustomerOverview> findOverviewsByAdvisorId(int advisorId);

    CustomerDetailView findDetailByCustomerId(int customerId);
}
