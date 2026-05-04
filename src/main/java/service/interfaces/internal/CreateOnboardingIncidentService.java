package service.interfaces.internal;

import model.domain.OnboardingIncident;

public interface CreateOnboardingIncidentService {
    OnboardingIncident createOnboardingIncident(int customerId);
}
