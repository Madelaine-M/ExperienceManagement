package repository.interfaces;

import model.Customer;
import java.util.List;

public interface CustomerRepository {

    void save(Customer customer);

    Customer findById(int id);

    List<Customer> findAll();

    void deleteById(int id);

    List<Customer> findByAdvisor(int advisorId);

    List<Customer> findByStatus(String status);

    List<Customer> findByClvScore(String clvScore);

}
