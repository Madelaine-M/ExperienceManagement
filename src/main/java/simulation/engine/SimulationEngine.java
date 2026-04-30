package simulation.engine;

import simulation.model.SimulationSnapshot;
import simulation.model.SimulationTickResult;

public interface SimulationEngine {

    SimulationTickResult tick(SimulationSnapshot snapshot);

    void reset();
}
