package repository.interfaces;

import model.CustomerCvProfile;

public interface CustomerCvProfileLookup {

    CustomerCvProfile findByCustomerId(int customerId);
}
