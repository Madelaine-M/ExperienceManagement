package ui.service;

import model.domain.Advisor;
import repository.interfaces.AdvisorRepository;
import simulation.SimulationService;
import simulation.model.SimulationConfig;
import simulation.model.SimulationSnapshot;
import simulation.persistence.SimulationDataCleanupService;

import java.util.List;

public class SimulationControlServiceImpl implements SimulationControlService {
    private final AdvisorRepository advisorRepository;
    private final SimulationService simulationService;
    private final SimulationDataCleanupService simulationDataCleanupService;

    public SimulationControlServiceImpl(AdvisorRepository advisorRepository,
                                        SimulationService simulationService,
                                        SimulationDataCleanupService simulationDataCleanupService) {
        this.advisorRepository = advisorRepository;
        this.simulationService = simulationService;
        this.simulationDataCleanupService = simulationDataCleanupService;
    }

    @Override
    public List<Advisor> loadAdvisors() {
        return advisorRepository.findAll();
    }

    @Override
    public void configure(SimulationConfig config) {
        simulationService.configure(config);
    }

    @Override
    public void start() {
        simulationService.start();
    }

    @Override
    public void stop() {
        simulationService.stop();
    }

    @Override
    public void clearSimulationData() {
        simulationService.stop();
        simulationDataCleanupService.cleanupGeneratedData();
        simulationService.reset();
    }

    @Override
    public boolean isRunning() {
        return simulationService.isRunning();
    }

    @Override
    public SimulationSnapshot getSnapshot() {
        return simulationService.getSnapshot();
    }

    @Override
    public SimulationSnapshot advanceOneTick() {
        return simulationService.advanceOneTick();
    }
}
