package repository.interfaces;

import model.Customer;

public interface CustomerUpdate {

    void save(Customer customer);

    void update(Customer customer);

}
