package service.calculation;

import model.Customer;

//Rename to actual thing
public class CVCalculator {
    private final Customer customer;
    private int score;
    public CVCalculator(Customer customer) {
        this.customer = customer;
    }
    public int calculate() {
        return score;
    }
}
