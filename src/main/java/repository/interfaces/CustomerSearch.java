package repository.interfaces;

import model.Customer;

import java.util.List;

public interface CustomerSearch {

    Customer findById(int id);

    List<Customer> findByAdvisor(int advisorId);

    List<Customer> findByStatus(String status);

}
