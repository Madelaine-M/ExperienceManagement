package repository.interfaces;

import model.domain.CustomerCvProfile;

public interface CustomerCvProfileUpdate {

    void save(CustomerCvProfile profile);

    void update(CustomerCvProfile profile);
}
