package repository.interfaces;

import model.CustomerNote;

public interface CustomerNoteUpdate {

    void save(CustomerNote customerNote);

    void update(CustomerNote customerNote);
}
