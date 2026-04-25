package repository.implementation.support;

public record OpenIncidentSummary(boolean hasOpenIncident, Integer openIncidentId, Double highestPriorityScore,
                                  String incidentDescription) {
}
