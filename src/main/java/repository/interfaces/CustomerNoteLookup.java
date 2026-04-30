package repository.interfaces;

import model.domain.CustomerNote;

import java.util.List;

public interface CustomerNoteLookup {

    CustomerNote findById(int id);

    List<CustomerNote> findByCustomerId(int customerId);
}
