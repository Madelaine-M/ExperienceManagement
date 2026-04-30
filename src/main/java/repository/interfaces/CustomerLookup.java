package repository.interfaces;

import model.domain.Customer;

import java.util.List;

public interface CustomerLookup {

    Customer findById(int id);

    List<Customer> findAll();
}
