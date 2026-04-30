package repository.interfaces;

import model.domain.Customer;

import java.util.List;

public interface CustomerSearch {

    Customer findById(int id);

    List<Customer> findByAdvisorId(int advisorId);

    List<Customer> findByStatus(String status);

}
