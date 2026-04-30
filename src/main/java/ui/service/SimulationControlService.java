package ui.service;

import model.domain.Advisor;
import simulation.model.SimulationConfig;
import simulation.model.SimulationSnapshot;

import java.util.List;

public interface SimulationControlService {

    List<Advisor> loadAdvisors();

    void configure(SimulationConfig config);

    void start();

    void stop();

    void clearSimulationData();

    boolean isRunning();

    SimulationSnapshot getSnapshot();

    SimulationSnapshot advanceOneTick();
}
