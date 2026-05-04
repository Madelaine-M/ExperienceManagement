package service.implementation.cv;

import model.domain.CVCustomer;
import model.domain.Customer;
import model.domain.CustomerCvProfile;
import model.domain.Flight;
import repository.interfaces.CustomerCvProfileLookup;
import repository.interfaces.CustomerLookup;
import repository.interfaces.FlightRepository;
import service.interfaces.internal.CVScoreCalcService;
import service.interfaces.internal.CustomerCvScoreService;

import java.util.ArrayList;
import java.util.List;

public class CustomerCvScoreServiceImpl implements CustomerCvScoreService {
    private final CustomerLookup customerLookup;
    private final CustomerCvProfileLookup customerCvProfileLookup;
    private final FlightRepository flightRepository;
    private final CVScoreCalcService cvScoreCalcService;

    public CustomerCvScoreServiceImpl(CustomerLookup customerLookup,
                                      CustomerCvProfileLookup customerCvProfileLookup,
                                      FlightRepository flightRepository,
                                      CVScoreCalcService cvScoreCalcService) {
        this.customerLookup = customerLookup;
        this.customerCvProfileLookup = customerCvProfileLookup;
        this.flightRepository = flightRepository;
        this.cvScoreCalcService = cvScoreCalcService;
    }

    @Override
    public float calculateForCustomerId(int customerId) {
        Customer customer = customerLookup.findById(customerId);
        if (customer == null) {
            return 0.0f;
        }

        Flight currentFlight = flightRepository.findCurrentByCustomerId(customerId);
        if (currentFlight == null) {
            return 0.0f;
        }

        CustomerCvProfile cvProfile = customerCvProfileLookup.findByCustomerId(customerId);
        Flight lastFlight = flightRepository.findLatestPreviousByCustomerId(customerId);

        List<Flight> flights = new ArrayList<>();
        flights.add(currentFlight);
        if (lastFlight != null) {
            flights.add(lastFlight);
        }

        return cvScoreCalcService.calculate(new CVCustomer(customer, cvProfile, flights));
    }
}