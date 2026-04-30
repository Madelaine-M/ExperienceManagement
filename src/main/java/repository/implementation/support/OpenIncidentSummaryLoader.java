package repository.implementation.support;

import model.enums.IncidentType;
import service.implementation.Priority.ExpectedImpactCalcServiceImpl;
import service.implementation.Priority.PriorityScoreSupport;
import service.interfaces.internal.ExpectedImpactCalcService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OpenIncidentSummaryLoader {
    private final ExpectedImpactCalcService expectedImpactCalcService;

    public OpenIncidentSummaryLoader() {
        this(new ExpectedImpactCalcServiceImpl());
    }

    public OpenIncidentSummaryLoader(ExpectedImpactCalcService expectedImpactCalcService) {
        this.expectedImpactCalcService = expectedImpactCalcService;
    }

    public OpenIncidentSummary loadForCustomer(Connection conn, int customerId) throws SQLException {
        return loadForCustomer(conn, customerId, 0.0f);
    }

    public OpenIncidentSummary loadForCustomer(Connection conn, int customerId, float customerCvScore) throws SQLException {
        String sql = """
            SELECT id,
                   type,
                   description,
                   created_at
            FROM incidents
            WHERE customer_id = ?
              AND status = 'OPEN'
            ORDER BY created_at ASC, id ASC;
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                List<OpenIncidentCandidate> candidates = new ArrayList<>();
                while (rs.next()) {
                    LocalDateTime createdAt = rs.getTimestamp("created_at") != null
                            ? rs.getTimestamp("created_at").toLocalDateTime()
                            : null;
                    String incidentType = rs.getString("type");
                    if (incidentType == null) {
                        continue;
                    }
                    double priorityScore = PriorityScoreSupport.calculate(
                            IncidentType.valueOf(incidentType),
                            customerCvScore,
                            expectedImpactCalcService
                    );
                    candidates.add(new OpenIncidentCandidate(
                            rs.getInt("id"),
                            priorityScore,
                            rs.getString("description"),
                            createdAt
                    ));
                }

                OpenIncidentCandidate best = candidates.stream()
                        .max(Comparator
                                .comparingDouble(OpenIncidentCandidate::priorityScore)
                                .thenComparing(OpenIncidentCandidate::createdAt, Comparator.nullsFirst(Comparator.reverseOrder()))
                                .thenComparingInt(OpenIncidentCandidate::incidentId))
                        .orElse(null);
                if (best != null) {
                    return new OpenIncidentSummary(true, best.incidentId(), best.priorityScore(), best.description());
                }
            }
        }

        return new OpenIncidentSummary(false, null, null, null);
    }

    private record OpenIncidentCandidate(int incidentId,
                                         double priorityScore,
                                         String description,
                                         LocalDateTime createdAt) {
    }
}
