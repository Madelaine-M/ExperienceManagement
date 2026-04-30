package simulation.model;

import java.time.LocalDateTime;

public class SimulationSnapshot {
    private final SimulationStatus status;
    private final SimulationConfig config;
    private final SimulationMetrics metrics;
    private final LocalDateTime startedAt;
    private final LocalDateTime stoppedAt;
    private final LocalDateTime lastUpdatedAt;

    public SimulationSnapshot(SimulationStatus status,
                              SimulationConfig config,
                              SimulationMetrics metrics,
                              LocalDateTime startedAt,
                              LocalDateTime stoppedAt,
                              LocalDateTime lastUpdatedAt) {
        this.status = status;
        this.config = config;
        this.metrics = metrics;
        this.startedAt = startedAt;
        this.stoppedAt = stoppedAt;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public SimulationStatus getStatus() {
        return status;
    }

    public SimulationConfig getConfig() {
        return config;
    }

    public SimulationMetrics getMetrics() {
        return metrics;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getStoppedAt() {
        return stoppedAt;
    }

    public LocalDateTime getLastUpdatedAt() {
        return lastUpdatedAt;
    }
}
