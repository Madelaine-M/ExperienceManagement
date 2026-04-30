package ui.service;

import model.domain.Customer;
import model.domain.Flight;
import service.interfaces.frontend.CustomerService;
import service.interfaces.frontend.FeedbackService;
import service.interfaces.frontend.FlightService;
import ui.model.FlightDetailData;
import ui.view.DashboardFormatters;

public class FlightDetailServiceImpl implements FlightDetailService {
    private final FlightService flightService;
    private final FeedbackService feedbackService;
    private final CustomerService customerService;

    public FlightDetailServiceImpl(FlightService flightService,
                                   FeedbackService feedbackService,
                                   CustomerService customerService) {
        this.flightService = flightService;
        this.feedbackService = feedbackService;
        this.customerService = customerService;
    }

    @Override
    public FlightDetailData loadFlightDetail(int flightId) {
        Flight flight = flightService.findById(flightId);
        if (flight == null) {
            return null;
        }

        Customer customer = customerService.findById(flight.getCustomerId());
        String customerName = customer == null
                ? "Unknown Customer"
                : DashboardFormatters.formatName(customer.getFirstName(), customer.getLastName());

        return new FlightDetailData(
                flight,
                customerName,
                feedbackService.findAllByFlightId(flightId)
        );
    }
}
