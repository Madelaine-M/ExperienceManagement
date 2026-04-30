package simulation;

import simulation.model.SimulationConfig;
import simulation.model.SimulationSnapshot;

public interface SimulationService {

    void configure(SimulationConfig config);

    void start();

    void stop();

    void reset();

    SimulationSnapshot getSnapshot();

    boolean isRunning();

    SimulationSnapshot advanceOneTick();
}
