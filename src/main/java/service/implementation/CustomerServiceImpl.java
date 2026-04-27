package service.implementation;

import model.Customer;
import model.CustomerDetailView;
import model.CustomerOverview;
import model.Incident;
import org.jetbrains.annotations.NotNull;
import repository.interfaces.CustomerLookup;

import repository.interfaces.CustomerSearch;
import repository.interfaces.CustomerUpdate;
import repository.interfaces.CustomerView;
import service.interfaces.frontend.CustomerService;
import service.interfaces.frontend.IncidentService;

import java.util.List;

public class CustomerServiceImpl implements CustomerService {

    private final CustomerLookup customerLookup;
    private final CustomerSearch customerSearch;
    private final CustomerUpdate customerUpdate;
    private final IncidentService incidentService;
    private final CustomerView customerView;

    public CustomerServiceImpl(CustomerLookup customerLookup, CustomerSearch customerSearch, CustomerUpdate customerUpdate, IncidentService incidentService, CustomerView customerView, CustomerOverview customerOverview) {
        this.customerLookup=customerLookup;
        this.customerSearch=customerSearch;
        this.customerUpdate=customerUpdate;
        this.incidentService=incidentService;
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
    public List<Customer> findByAdvisor(int advisorId) {
        return customerSearch.findByAdvisor(advisorId);
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

    public boolean CheckIncident (@NotNull Customer customer){//logging
        List<Incident> incidents = incidentService.findByAdvisorId(customer.getAssignedAdvisorId());
        for(Incident incident : incidents){
            if (incident.getCustomerId() == customer.getId()){
                return true;
            }
        }
        return false;
    }
}
