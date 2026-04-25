package repository.interfaces;

import model.CustomerNote;

import java.util.List;

public interface CustomerNoteLookup {

    CustomerNote findById(int id);

    List<CustomerNote> findByCustomerId(int customerId);
}
