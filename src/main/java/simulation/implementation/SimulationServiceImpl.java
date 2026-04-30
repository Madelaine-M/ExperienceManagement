package simulation.implementation;

import simulation.SimulationService;
import simulation.engine.SimulationEngine;
import simulation.model.SimulationConfig;
import simulation.model.SimulationMetrics;
import simulation.model.SimulationSnapshot;
import simulation.model.SimulationStatus;
import simulation.model.SimulationTickResult;
import simulation.validation.SimulationConfigValidator;

import java.time.LocalDateTime;

public class SimulationServiceImpl implements SimulationService {
    private final SimulationConfigValidator configValidator;
    private final SimulationEngine simulationEngine;

    private SimulationStatus status = SimulationStatus.STOPPED;
    private SimulationConfig config;
    private SimulationMetrics metrics = emptyMetrics();
    private LocalDateTime startedAt;
    private LocalDateTime stoppedAt;
    private LocalDateTime lastUpdatedAt;

    public SimulationServiceImpl(SimulationConfigValidator configValidator,
                                 SimulationEngine simulationEngine) {
        this.configValidator = configValidator;
        this.simulationEngine = simulationEngine;
    }

    @Override
    public synchronized void configure(SimulationConfig config) {
        ensureStoppedForConfiguration();
        configValidator.validate(config);
        this.config = config;
        this.metrics = emptyMetrics();
        this.startedAt = null;
        this.stoppedAt = null;
        this.lastUpdatedAt = LocalDateTime.now();
    }

    @Override
    public synchronized void start() {
        ensureConfigured();
        if (status == SimulationStatus.RUNNING) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        if (startedAt == null) {
            startedAt = now;
        }
        stoppedAt = null;
        status = SimulationStatus.RUNNING;
        lastUpdatedAt = now;
    }

    @Override
    public synchronized void stop() {
        if (status == SimulationStatus.STOPPED) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        status = SimulationStatus.STOPPED;
        stoppedAt = now;
        lastUpdatedAt = now;
    }

    @Override
    public synchronized void reset() {
        status = SimulationStatus.STOPPED;
        config = null;
        metrics = emptyMetrics();
        startedAt = null;
        stoppedAt = null;
        lastUpdatedAt = LocalDateTime.now();
        simulationEngine.reset();
    }

    @Override
    public synchronized SimulationSnapshot getSnapshot() {
        return new SimulationSnapshot(
                status,
                config,
                metrics,
                startedAt,
                stoppedAt,
                lastUpdatedAt
        );
    }

    @Override
    public synchronized boolean isRunning() {
        return status == SimulationStatus.RUNNING;
    }

    @Override
    public synchronized SimulationSnapshot advanceOneTick() {
        ensureConfigured();
        ensureRunning();

        SimulationTickResult tickResult = simulationEngine.tick(getSnapshot());
        metrics = mergeMetrics(metrics, tickResult);
        lastUpdatedAt = tickResult.getProcessedAt() != null ? tickResult.getProcessedAt() : LocalDateTime.now();

        return getSnapshot();
    }

    private void ensureConfigured() {
        if (config == null) {
            throw new IllegalStateException("Simulation must be configured before it can be started.");
        }
    }

    private void ensureRunning() {
        if (status != SimulationStatus.RUNNING) {
            throw new IllegalStateException("Simulation must be running before a tick can be advanced.");
        }
    }

    private void ensureStoppedForConfiguration() {
        if (status == SimulationStatus.RUNNING) {
            throw new IllegalStateException("Simulation must be stopped before reconfiguration.");
        }
    }

    private SimulationMetrics emptyMetrics() {
        return new SimulationMetrics(0, 0, 0, 0, 0);
    }

    private SimulationMetrics mergeMetrics(SimulationMetrics currentMetrics, SimulationTickResult tickResult) {
        return new SimulationMetrics(
                currentMetrics.getCreatedCustomers() + tickResult.getCreatedCustomers(),
                currentMetrics.getAdvancedJourneys() + tickResult.getAdvancedJourneys(),
                currentMetrics.getGeneratedDelayIncidents() + tickResult.getGeneratedDelayIncidents(),
                currentMetrics.getGeneratedFeedbacks() + tickResult.getGeneratedFeedbacks(),
                currentMetrics.getGeneratedFeedbackIncidents() + tickResult.getGeneratedFeedbackIncidents()
        );
    }
}
