package repository.interfaces;

import model.domain.CustomerNote;

public interface CustomerNoteUpdate {

    void save(CustomerNote customerNote);

    void update(CustomerNote customerNote);
}
