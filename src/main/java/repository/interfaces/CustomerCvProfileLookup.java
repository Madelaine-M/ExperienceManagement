package repository.interfaces;

import model.domain.CustomerCvProfile;

public interface CustomerCvProfileLookup {

    CustomerCvProfile findByCustomerId(int customerId);
}
