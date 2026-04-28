package repository.interfaces;

import model.CustomerCvProfile;

public interface CustomerCvProfileUpdate {

    void save(CustomerCvProfile profile);

    void update(CustomerCvProfile profile);
}
