package simulation.engine;

import model.enums.CustomerStatus;

import java.util.EnumMap;
import java.util.Map;

class JourneyStatusTransitionPolicy {
    private final Map<CustomerStatus, CustomerStatus> transitions = new EnumMap<>(CustomerStatus.class);

    JourneyStatusTransitionPolicy() {
        transitions.put(CustomerStatus.INTERESTED, CustomerStatus.INFO_SESSION_INVITED);
        transitions.put(CustomerStatus.INFO_SESSION_INVITED, CustomerStatus.INFO_SESSION_ATTENDED);
        transitions.put(CustomerStatus.INFO_SESSION_ATTENDED, CustomerStatus.BOOKED);
        transitions.put(CustomerStatus.BOOKED, CustomerStatus.ADVISOR_ASSIGNED);
        transitions.put(CustomerStatus.ADVISOR_ASSIGNED, CustomerStatus.ONBOARDING);
        transitions.put(CustomerStatus.ONBOARDING, CustomerStatus.MEDICAL_CHECK);
        transitions.put(CustomerStatus.MEDICAL_CHECK, CustomerStatus.HOTEL);
        transitions.put(CustomerStatus.HOTEL, CustomerStatus.SHUTTLE);
        transitions.put(CustomerStatus.SHUTTLE, CustomerStatus.PRE_FLIGHT);
        transitions.put(CustomerStatus.PRE_FLIGHT, CustomerStatus.FLIGHT);
        transitions.put(CustomerStatus.FLIGHT, CustomerStatus.LANDING);
        transitions.put(CustomerStatus.LANDING, CustomerStatus.FEEDBACK);
        transitions.put(CustomerStatus.FEEDBACK, CustomerStatus.COMPLETED);
        transitions.put(CustomerStatus.COMPLETED, CustomerStatus.COMPLETED);
    }

    CustomerStatus nextStatus(CustomerStatus currentStatus) {
        if (currentStatus == null) {
            return CustomerStatus.INTERESTED;
        }
        return transitions.getOrDefault(currentStatus, currentStatus);
    }
}
