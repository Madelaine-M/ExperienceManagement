package service.implementation.incidents.onboarding;

import model.domain.Customer;
import model.domain.Incident;
import model.domain.OnboardingIncident;
import model.enums.IncidentStatus;
import model.enums.IncidentType;
import repository.interfaces.CustomerLookup;
import repository.interfaces.IncidentLookup;
import repository.interfaces.IncidentUpdate;
import service.interfaces.internal.CreateOnboardingIncidentService;
import service.interfaces.internal.ExpectedImpactCalcService;

public class CreateOnboardingIncidentServiceImpl implements CreateOnboardingIncidentService {
    private final CustomerLookup customerLookup;
    private final IncidentLookup incidentLookup;
    private final IncidentUpdate incidentUpdate;
    private final ExpectedImpactCalcService expectedImpactCalcService;

    public CreateOnboardingIncidentServiceImpl(CustomerLookup customerLookup,
                                               IncidentLookup incidentLookup,
                                               IncidentUpdate incidentUpdate,
                                               ExpectedImpactCalcService expectedImpactCalcService) {
        this.customerLookup = customerLookup;
        this.incidentLookup = incidentLookup;
        this.incidentUpdate = incidentUpdate;
        this.expectedImpactCalcService = expectedImpactCalcService;
    }

    @Override
    public OnboardingIncident createOnboardingIncident(int customerId) {
        Customer customer = customerLookup.findById(customerId);
        if (customer == null || hasOpenOnboardingIncident(customerId)) {
            return null;
        }

        OnboardingIncident incident = new OnboardingIncident();
        incident.setCustomerId(customerId);
        incident.setAssignedAdvisorId(customer.getAssignedAdvisorId());
        incident.setDescription("Customer has remained in onboarding longer than expected and may need support.");
        incident.setStatus(IncidentStatus.OPEN);
        incident.setCreatedAt(java.time.LocalDateTime.now());

        int scoreImpact = expectedImpactCalcService.calculateDefaultScoreImpact(IncidentType.ONBOARDING);
        incident.setScoreImpact(scoreImpact);
        incident.setRevenueRisk(expectedImpactCalcService.calculateRevenueImpact(scoreImpact));

        incidentUpdate.save(incident);
        return incident;
    }

    private boolean hasOpenOnboardingIncident(int customerId) {
        for (Incident incident : incidentLookup.findAllByCustomerId(customerId)) {
            if (incident.getType() == IncidentType.ONBOARDING && incident.getStatus() == IncidentStatus.OPEN) {
                return true;
            }
        }
        return false;
    }
}
