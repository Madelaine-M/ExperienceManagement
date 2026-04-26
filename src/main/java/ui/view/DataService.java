package ui.view;

import model.Customer;
import model.Incident;
import service.Backend;

import java.util.List;

public class DataService {
    private final Backend backend;

    public DataService(Backend backend) {
        this.backend = backend;
    }

    public List<Customer> getCustomers() {
        return backend.getCustomerRepository().findAll();
    }

    public List<Incident> getIncidentsByCustomerId(int customerId) {
        return backend.getIncidentRepository().findAllByCustomerId(customerId);
    }
}
