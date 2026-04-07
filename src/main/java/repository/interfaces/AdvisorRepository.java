package repository.interfaces;

import model.Advisor;

import java.util.List;

public interface AdvisorRepository {

    void save(Advisor advisor);

    Advisor findById(int id);

    List<Advisor> findAll();

    void deleteById(int id);

    List<Advisor> findBySpeciality(String speciality);
}
