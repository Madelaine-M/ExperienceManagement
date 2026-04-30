package service.implementation;

import model.domain.Customer;
import model.view.CustomerDetailView;
import model.view.CustomerOverview;
import repository.interfaces.CustomerLookup;

import repository.interfaces.CustomerSearch;
import repository.interfaces.CustomerUpdate;
import repository.interfaces.CustomerView;
import service.interfaces.frontend.CustomerService;

import java.util.List;
public class CustomerServiceImpl implements CustomerService {

    private final CustomerLookup customerLookup;
    private final CustomerSearch customerSearch;
    private final CustomerUpdate customerUpdate;
    private final CustomerView customerView;

    public CustomerServiceImpl(CustomerLookup customerLookup,
                               CustomerSearch customerSearch,
                               CustomerUpdate customerUpdate,
                               CustomerView customerView) {
        this.customerLookup=customerLookup;
        this.customerSearch=customerSearch;
        this.customerUpdate=customerUpdate;
        this.customerView = customerView;
    }

    @Override
    public Customer findById(int id) {
        return customerLookup.findById(id);
    }

    @Override
    public List<Customer> findAll() {
        return customerLookup.findAll();
    }

    @Override
    public List<Customer> findByAdvisorId(int advisorId) {
        return customerSearch.findByAdvisorId(advisorId);
    }

    @Override
    public List<Customer> findByStatus(String status) {
        return customerSearch.findByStatus(status);
    }

    @Override
    public void save(Customer customer) {
        customerUpdate.save(customer);
    }

    @Override
    public void update(Customer customer) {
        customerUpdate.update(customer);
    }

    @Override
    public List<CustomerOverview> findOverviewsByAdvisorId(int advisorId) {
        return customerView.findOverviewsByAdvisorId(advisorId);
    }

    @Override
    public CustomerDetailView findDetailByCustomerId(int customerId) {
        return customerView.findDetailByCustomerId(customerId);
    }
}
