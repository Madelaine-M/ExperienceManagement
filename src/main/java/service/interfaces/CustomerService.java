package service.interfaces;

import model.Customer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CustomerService {

    Customer findById(int id);

    List<Customer> findAll();

    //Customer findById(int id); Dopplung im Repo

    List<Customer> findByAdvisor(int advisorId);

    List<Customer> findByStatus(String status);

    void save(Customer customer);

    void update(Customer customer);

    boolean CheckIncident (Customer customer);
    //Brauche Klassen von Madelaine die Non Incident fall abbildet und den die mini Overview
}
