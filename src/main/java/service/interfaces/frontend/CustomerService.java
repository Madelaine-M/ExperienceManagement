package service.interfaces.frontend;

import model.Customer;
import model.CustomerDetailView;
import model.CustomerOverview;

import java.util.List;

public interface CustomerService {

    Customer findById(int id);

    List<Customer> findAll();

    //Customer findById(int id); Dopplung im Repo

    List<Customer> findByAdvisor(int advisorId);

    List<Customer> findByStatus(String status);

    void save(Customer customer);

    void update(Customer customer);

    List<CustomerOverview> findOverviewsByAdvisorId(int advisorId);

    CustomerDetailView findDetailByCustomerId(int customerId);
}
