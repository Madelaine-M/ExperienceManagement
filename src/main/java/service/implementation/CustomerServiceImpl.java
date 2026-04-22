package service.implementation;

import model.Customer;
import model.Incident;
import org.jetbrains.annotations.NotNull;
import repository.interfaces.CustomerLookup;

import repository.interfaces.CustomerSearch;
import repository.interfaces.CustomerUpdate;
import service.interfaces.CustomerService;
import service.interfaces.IncidentService;

import java.util.ArrayList;
import java.util.List;

public class CustomerServiceImpl implements CustomerService {

    private final CustomerLookup customerLookup;
    private final CustomerSearch customerSearch;
    private final CustomerUpdate customerUpdate;
    private final IncidentService incidentService;

    public CustomerServiceImpl(CustomerLookup customerLookup, CustomerSearch customerSearch, CustomerUpdate customerUpdate, IncidentService incidentService) {
        this.customerLookup=customerLookup;
        this.customerSearch=customerSearch;
        this.customerUpdate=customerUpdate;
        this.incidentService=incidentService;
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

    public boolean CheckIncident (@NotNull Customer customer){//logging
        List<Incident> incidents = incidentService.findByAdvisorId((long)customer.getAssignedAdvisorId());
        for(Incident incident : incidents){
            if (incident.getCustomerId() == customer.getId()){
                return true;
            }
        }
        return false;
    }
}
