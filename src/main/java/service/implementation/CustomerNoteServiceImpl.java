package service.implementation;

import model.domain.CustomerNote;
import repository.interfaces.CustomerNoteLookup;
import repository.interfaces.CustomerNoteUpdate;
import service.interfaces.frontend.CustomerNoteService;

import java.util.List;

public class CustomerNoteServiceImpl implements CustomerNoteService {
    private final CustomerNoteLookup customerNoteLookup;
    private final CustomerNoteUpdate customerNoteUpdate;

    public CustomerNoteServiceImpl(CustomerNoteLookup customerNoteLookup, CustomerNoteUpdate customerNoteUpdate) {
        this.customerNoteLookup = customerNoteLookup;
        this.customerNoteUpdate = customerNoteUpdate;
    }

    @Override
    public CustomerNote findById(int id) {
        return customerNoteLookup.findById(id);
    }

    @Override
    public List<CustomerNote> findByCustomerId(int customerId) {
        return customerNoteLookup.findByCustomerId(customerId);
    }

    @Override
    public void save(CustomerNote customerNote) {
        customerNoteUpdate.save(customerNote);
    }

    @Override
    public void update(CustomerNote customerNote) {
        customerNoteUpdate.update(customerNote);
    }
}
