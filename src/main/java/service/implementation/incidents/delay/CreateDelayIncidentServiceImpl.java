package service.implementation.incidents.delay;

import model.domain.Customer;
import model.domain.DelayIncident;
import model.domain.Flight;
import model.enums.IncidentStatus;
import model.enums.IncidentType;
import repository.interfaces.CustomerLookup;
import repository.interfaces.FlightRepository;
import repository.interfaces.IncidentLookup;
import repository.interfaces.IncidentUpdate;
import service.interfaces.internal.CreateDelayIncidentService;
import service.interfaces.internal.ExpectedImpactCalcService;
import service.interfaces.internal.PriorityCalcService;

public class CreateDelayIncidentServiceImpl implements CreateDelayIncidentService {
    private final CustomerLookup customerLookup;
    private final FlightRepository flightRepository;
    private final IncidentLookup incidentLookup;
    private final IncidentUpdate incidentUpdate;
    private final ExpectedImpactCalcService expectedImpactCalcService;
    private final PriorityCalcService priorityCalcService;

    public CreateDelayIncidentServiceImpl(CustomerLookup customerLookup,
                                          FlightRepository flightRepository,
                                          IncidentLookup incidentLookup,
                                          IncidentUpdate incidentUpdate,
                                          ExpectedImpactCalcService expectedImpactCalcService,
                                          PriorityCalcService priorityCalcService) {
        this.customerLookup = customerLookup;
        this.flightRepository = flightRepository;
        this.incidentLookup = incidentLookup;
        this.incidentUpdate = incidentUpdate;
        this.expectedImpactCalcService = expectedImpactCalcService;
        this.priorityCalcService = priorityCalcService;
    }

    @Override
    public DelayIncident createDelayIncident(int customerId, int delayMinutes, String description) {
        if (delayMinutes <= 0) {
            return null;
        }

        Customer customer = customerLookup.findById(customerId);
        if (customer == null) {
            return null;
        }

        Flight currentFlight = flightRepository.findCurrentByCustomerId(customerId);
        Integer flightId = currentFlight != null ? currentFlight.getId() : null;

        if (incidentLookup.existsOpenDelayIncidentForCustomerFlight(customerId, flightId)) {
            return null;
        }

        DelayIncident delayIncident = new DelayIncident();
        delayIncident.setCustomerId(customerId);
        delayIncident.setAssignedAdvisorId(customer.getAssignedAdvisorId());
        delayIncident.setDelayMinutes(delayMinutes);
        delayIncident.setDescription(buildDescription(currentFlight, delayMinutes, description));
        delayIncident.setFlightId(flightId);
        delayIncident.setStatus(IncidentStatus.OPEN);
        delayIncident.setCreatedAt(java.time.LocalDateTime.now());

        int scoreImpact = expectedImpactCalcService.calculateDefaultScoreImpact(IncidentType.DELAY);
        delayIncident.setScoreImpact(scoreImpact);
        delayIncident.setRevenueRisk(expectedImpactCalcService.calculateRevenueImpact(scoreImpact));

        incidentUpdate.save(delayIncident);
        return delayIncident;
    }

    private String buildDescription(Flight currentFlight, int delayMinutes, String description) {
        if (description != null && !description.isBlank()) {
            return description;
        }

        String flightNumber = currentFlight == null || currentFlight.getFlightNumber() == null
                ? "current flight"
                : currentFlight.getFlightNumber();

        return "Pre-flight delay of " + delayMinutes + " minutes detected for " + flightNumber + ".";
    }
}
