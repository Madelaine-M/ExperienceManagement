package repository.implementation;

import database.connection.ConnectionProvider;
import database.connection.DatabaseConnectionProvider;
import model.domain.Advisor;
import model.domain.CustomerCvProfile;
import model.view.CustomerDetailView;
import model.domain.CustomerNote;
import model.view.CustomerOverview;
import model.domain.Flight;
import model.workflow.RecoveryActionSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.RepositoryException;
import repository.implementation.mapper.CustomerViewMapper;
import repository.implementation.support.CustomerNoteLoader;
import repository.implementation.support.FlightViewLoader;
import repository.implementation.support.OpenIncidentSummary;
import repository.implementation.support.OpenIncidentSummaryLoader;
import repository.implementation.support.PreviousFlightsSummaryFormatter;
import repository.interfaces.AdvisorRepository;
import repository.interfaces.CustomerCvProfileLookup;
import repository.interfaces.CustomerView;
import service.implementation.cv.CVScoreCalcServiceImpl;
import service.implementation.cv.CustomerCvScoreServiceImpl;
import service.interfaces.internal.CustomerCvScoreService;
import support.RecoveryActionNoteCodec;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseCustomerView implements CustomerView {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseCustomerView.class);
    private final CustomerViewMapper customerViewMapper;
    private final OpenIncidentSummaryLoader openIncidentSummaryLoader;
    private final FlightViewLoader flightViewLoader;
    private final CustomerNoteLoader customerNoteLoader;
    private final PreviousFlightsSummaryFormatter previousFlightsSummaryFormatter;
    private final CustomerCvProfileLookup customerCvProfileLookup;
    private final AdvisorRepository advisorRepository;
    private final CustomerCvScoreService customerCvScoreService;
    private final ConnectionProvider connectionProvider;

    public DatabaseCustomerView() {
        this(
                new DatabaseConnectionProvider(),
                new CustomerViewMapper(),
                new OpenIncidentSummaryLoader(),
                new FlightViewLoader(),
                new CustomerNoteLoader(),
                new PreviousFlightsSummaryFormatter(),
                new DatabaseCustomerCvProfileRepository(),
                new DatabaseAdvisorRepository(),
                new CustomerCvScoreServiceImpl(
                        new DatabaseCustomerRepository(),
                        new DatabaseCustomerCvProfileRepository(),
                        new DatabaseFlightRepository(),
                        new CVScoreCalcServiceImpl()
                )
        );
    }

    public DatabaseCustomerView(ConnectionProvider connectionProvider,
                                CustomerViewMapper customerViewMapper,
                                OpenIncidentSummaryLoader openIncidentSummaryLoader,
                                FlightViewLoader flightViewLoader,
                                CustomerNoteLoader customerNoteLoader,
                                PreviousFlightsSummaryFormatter previousFlightsSummaryFormatter,
                                CustomerCvProfileLookup customerCvProfileLookup,
                                AdvisorRepository advisorRepository,
                                CustomerCvScoreService customerCvScoreService) {
        this.connectionProvider = connectionProvider;
        this.customerViewMapper = customerViewMapper;
        this.openIncidentSummaryLoader = openIncidentSummaryLoader;
        this.flightViewLoader = flightViewLoader;
        this.customerNoteLoader = customerNoteLoader;
        this.previousFlightsSummaryFormatter = previousFlightsSummaryFormatter;
        this.customerCvProfileLookup = customerCvProfileLookup;
        this.advisorRepository = advisorRepository;
        this.customerCvScoreService = customerCvScoreService;
    }

    @Override
    public List<CustomerOverview> findOverviewsByAdvisorId(int advisorId) {
        List<CustomerOverview> overviews = new ArrayList<>();
        String sql = """
            SELECT c.id,
                   c.first_name,
                   c.last_name,
                   c.status,
                   c.is_returning,
                   f.booking_package
            FROM customers c
            LEFT JOIN flights f ON c.id = f.customer_id
                               AND f.is_current = 1
            WHERE c.assigned_advisor_id = ?
            ORDER BY c.last_name ASC, c.first_name ASC, c.id ASC;
            """;

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, advisorId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    CustomerOverview overview = customerViewMapper.mapOverview(rs);
                    float cvScore = customerCvScoreService.calculateForCustomerId(overview.getCustomerId());
                    overview.setCvScore(cvScore);
                    applyCvProfile(overview);
                    applyOpenIncidentSummary(conn, overview, cvScore);
                    overviews.add(overview);
                }
            }
        } catch (SQLException e) {
            logger.error("Error while loading customer overviews for advisor {}", advisorId, e);
            throw new RepositoryException("Failed to load customer overviews for advisor " + advisorId, e);
        }

        return overviews;
    }

    @Override
    public CustomerDetailView findDetailByCustomerId(int customerId) {
        String sql = """
            SELECT id,
                   first_name,
                   last_name,
                   email,
                   status,
                   is_returning,
                   preferences,
                   apply_to_next_booking
            FROM customers
            WHERE id = ?;
            """;

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    CustomerDetailView detail = customerViewMapper.mapDetail(rs);
                    float cvScore = customerCvScoreService.calculateForCustomerId(detail.getCustomerId());
                    detail.setCvScore(cvScore);
                    applyCvProfile(detail);
                    applyOpenIncidentSummary(conn, detail, cvScore);
                    List<CustomerNote> notes = customerNoteLoader.loadByCustomerId(conn, customerId);
                    detail.setNotes(notes);
                    RecoveryActionSummary latestRecoveryAction = RecoveryActionNoteCodec.findLatestRecoveryAction(notes);
                    detail.setLatestRecoveryAction(enrichRecoveryAction(latestRecoveryAction));
                    Flight currentFlight = flightViewLoader.loadCurrentFlight(conn, customerId);
                    detail.setCurrentFlight(currentFlight);
                    if (currentFlight != null) {
                        detail.setBookingDate(currentFlight.getBookingDate());
                        detail.setBookingPackage(currentFlight.getBookingPackage());
                    }

                    List<Flight> previousFlights = flightViewLoader.loadPreviousFlights(conn, customerId);
                    detail.setPreviousFlightsList(previousFlights);
                    detail.setPreviousFlights(previousFlightsSummaryFormatter.format(previousFlights));

                    return detail;
                }
            }
        } catch (SQLException e) {
            logger.error("Error while loading customer detail view for customer {}", customerId, e);
            throw new RepositoryException("Failed to load customer detail for customer " + customerId, e);
        }

        return null;
    }

    private void applyOpenIncidentSummary(Connection conn, CustomerOverview overview, float cvScore) throws SQLException {
        OpenIncidentSummary summary = openIncidentSummaryLoader.loadForCustomer(conn, overview.getCustomerId(), cvScore);
        overview.setHasOpenIncident(summary.hasOpenIncident());
        overview.setOpenIncidentId(summary.openIncidentId());
        overview.setHighestPriorityScore(summary.highestPriorityScore());
        overview.setIncidentDescription(summary.incidentDescription());
    }

    private void applyOpenIncidentSummary(Connection conn, CustomerDetailView detail, float cvScore) throws SQLException {
        OpenIncidentSummary summary = openIncidentSummaryLoader.loadForCustomer(conn, detail.getCustomerId(), cvScore);
        detail.setHasOpenIncident(summary.hasOpenIncident());
        detail.setOpenIncidentId(summary.openIncidentId());
        detail.setHighestPriorityScore(summary.highestPriorityScore());
        detail.setIncidentDescription(summary.incidentDescription());
    }

    private void applyCvProfile(CustomerOverview overview) {
        CustomerCvProfile profile = customerCvProfileLookup.findByCustomerId(overview.getCustomerId());
        if (profile != null) {
            overview.setCustomerType(profile.getCustomerType());
        }
    }

    private void applyCvProfile(CustomerDetailView detail) {
        CustomerCvProfile profile = customerCvProfileLookup.findByCustomerId(detail.getCustomerId());
        if (profile != null) {
            detail.setCustomerType(profile.getCustomerType());
            detail.setPaymentMethod(profile.getPaymentMethod());
            detail.setPublicPerson(profile.isPublicPerson());
        }
    }

    private RecoveryActionSummary enrichRecoveryAction(RecoveryActionSummary recoveryAction) {
        if (recoveryAction == null) {
            return null;
        }

        Advisor advisor = advisorRepository.findById(recoveryAction.getAdvisorId());
        String advisorName = advisor == null
                ? "Advisor #" + recoveryAction.getAdvisorId()
                : (advisor.getFirstName() + " " + advisor.getLastName()).trim();

        return new RecoveryActionSummary(
                recoveryAction.getAdvisorId(),
                advisorName,
                recoveryAction.getSentAt(),
                recoveryAction.getOptionNumber(),
                recoveryAction.getSelectedRecommendation(),
                recoveryAction.getSubject(),
                recoveryAction.getMailBody()
        );
    }
}
