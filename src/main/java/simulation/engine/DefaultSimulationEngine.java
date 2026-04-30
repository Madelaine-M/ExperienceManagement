package simulation.engine;

import repository.interfaces.CustomerCvProfileUpdate;
import repository.interfaces.CustomerLookup;
import repository.interfaces.CustomerUpdate;
import repository.interfaces.FeedbackLookup;
import repository.interfaces.FeedbackUpdate;
import repository.interfaces.FlightRepository;
import service.interfaces.internal.AdvisorAssignmentService;
import service.interfaces.internal.CreateDelayIncidentService;
import service.interfaces.internal.CreateFeedbackIncidentService;
import service.interfaces.internal.CreateSuggestedActionService;
import simulation.model.SimulationSnapshot;
import simulation.model.SimulationTickResult;

import java.time.LocalDateTime;
import java.util.Random;

public class DefaultSimulationEngine implements SimulationEngine {
    private final SimulationRuntimeState runtimeState = new SimulationRuntimeState();
    private final CustomerCreationProcessor customerCreationProcessor;
    private final JourneyAdvancementProcessor journeyAdvancementProcessor;

    public DefaultSimulationEngine(CustomerUpdate customerUpdate,
                                   CustomerLookup customerLookup,
                                   CustomerCvProfileUpdate customerCvProfileUpdate,
                                   FlightRepository flightRepository,
                                   FeedbackLookup feedbackLookup,
                                   FeedbackUpdate feedbackUpdate,
                                   AdvisorAssignmentService advisorAssignmentService,
                                   CreateDelayIncidentService createDelayIncidentService,
                                   CreateFeedbackIncidentService createFeedbackIncidentService,
                                   CreateSuggestedActionService createSuggestedActionService,
                                   Random random) {
        this.customerCreationProcessor = new CustomerCreationProcessor(
                customerUpdate,
                customerCvProfileUpdate,
                flightRepository,
                feedbackUpdate,
                advisorAssignmentService,
                random
        );
        this.journeyAdvancementProcessor = new JourneyAdvancementProcessor(
                customerLookup,
                customerUpdate,
                flightRepository,
                feedbackLookup,
                feedbackUpdate,
                createDelayIncidentService,
                createFeedbackIncidentService,
                createSuggestedActionService,
                random
        );
    }

    @Override
    public SimulationTickResult tick(SimulationSnapshot snapshot) {
        if (snapshot == null || snapshot.getConfig() == null) {
            return new SimulationTickResult(0, 0, 0, 0, 0, LocalDateTime.now());
        }

        if (!runtimeState.matches(snapshot.getConfig())) {
            runtimeState.reset(snapshot.getConfig());
        }

        LocalDateTime now = LocalDateTime.now();
        int createdCustomers = customerCreationProcessor.processDueCustomers(snapshot, runtimeState, now);
        SimulationTickResult journeyResult = journeyAdvancementProcessor.processJourneyAdvancement(snapshot, runtimeState, now);

        return new SimulationTickResult(
                createdCustomers,
                journeyResult.getAdvancedJourneys(),
                journeyResult.getGeneratedDelayIncidents(),
                journeyResult.getGeneratedFeedbacks(),
                journeyResult.getGeneratedFeedbackIncidents(),
                now
        );
    }

    @Override
    public void reset() {
        runtimeState.reset(null);
    }
}
