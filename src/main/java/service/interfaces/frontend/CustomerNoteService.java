package service.interfaces.frontend;

import model.domain.CustomerNote;

import java.util.List;

public interface CustomerNoteService {
    CustomerNote findById(int id);

    List<CustomerNote> findByCustomerId(int customerId);

    void save(CustomerNote customerNote);

    void update(CustomerNote customerNote);
}
