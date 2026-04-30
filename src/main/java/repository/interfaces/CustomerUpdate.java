package repository.interfaces;

import model.domain.Customer;

public interface CustomerUpdate {

    void save(Customer customer);

    void update(Customer customer);

}
