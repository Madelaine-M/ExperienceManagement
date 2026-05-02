package service;

import database.connection.DatabaseConnectionProvider;
import database.connection.ConnectionProvider;
import database.initialization.DatabaseInitializer;
import repository.implementation.DatabaseActionRepository;
import repository.implementation.DatabaseAdvisorRepository;
import repository.implementation.DatabaseCustomerCvProfileRepository;
import repository.implementation.DatabaseCustomerNoteRepository;
import repository.implementation.DatabaseCustomerRepository;
import repository.implementation.DatabaseCustomerView;
import repository.implementation.DatabaseFeedbackAnalytics;
import repository.implementation.DatabaseFeedbackLookup;
import repository.implementation.DatabaseFeedbackUpdate;
import repository.implementation.DatabaseFlightRepository;
import repository.implementation.DatabaseIncidentRepository;
import repository.implementation.DatabaseIncidentView;
import repository.implementation.DatabaseNPSScores;
import repository.implementation.mapper.ActionItemResultSetMapper;
import repository.implementation.mapper.AdvisorResultSetMapper;
import repository.implementation.mapper.CustomerCvProfileResultSetMapper;
import repository.implementation.mapper.CustomerNoteResultSetMapper;
import repository.implementation.mapper.CustomerResultSetMapper;
import repository.implementation.mapper.CustomerViewMapper;
import repository.implementation.mapper.FeedbackItemResultSetMapper;
import repository.implementation.mapper.FeedbackResultSetMapper;
import repository.implementation.mapper.FlightResultSetMapper;
import repository.implementation.mapper.IncidentResultSetMapper;
import repository.implementation.mapper.IncidentViewMapper;
import repository.implementation.support.CustomerNoteLoader;
import repository.implementation.support.FeedbackItemLoader;
import repository.implementation.support.FlightIdResolver;
import repository.implementation.support.FlightViewLoader;
import repository.implementation.support.GeneratedKeyExtractor;
import repository.implementation.support.OpenIncidentSummaryLoader;
import repository.implementation.support.PreviousFlightsSummaryFormatter;
import service.implementation.cv.CVScoreCalcServiceImpl;
import service.implementation.cv.CustomerCvScoreServiceImpl;
import service.interfaces.internal.CustomerCvScoreService;

public class BackendFactory {
    public Backend create() {
        DatabaseInitializer.initialize();

        ConnectionProvider connectionProvider = new DatabaseConnectionProvider();
        GeneratedKeyExtractor generatedKeyExtractor = new GeneratedKeyExtractor();
        FlightIdResolver flightIdResolver = new FlightIdResolver();
        CustomerNoteLoader customerNoteLoader = new CustomerNoteLoader();
        FeedbackItemLoader feedbackItemLoader = new FeedbackItemLoader();
        FlightViewLoader flightViewLoader = new FlightViewLoader();
        PreviousFlightsSummaryFormatter previousFlightsSummaryFormatter = new PreviousFlightsSummaryFormatter();
        OpenIncidentSummaryLoader openIncidentSummaryLoader = new OpenIncidentSummaryLoader();

        DatabaseCustomerCvProfileRepository customerCvProfileRepository = new DatabaseCustomerCvProfileRepository(
                connectionProvider,
                new CustomerCvProfileResultSetMapper()
        );
        DatabaseCustomerRepository customerRepository = new DatabaseCustomerRepository(
                connectionProvider,
                new CustomerResultSetMapper(),
                generatedKeyExtractor
        );
        DatabaseCustomerNoteRepository customerNoteRepository = new DatabaseCustomerNoteRepository(
                connectionProvider,
                new CustomerNoteResultSetMapper(),
                customerNoteLoader,
                generatedKeyExtractor
        );
        DatabaseFlightRepository flightRepository = new DatabaseFlightRepository(
                connectionProvider,
                new FlightResultSetMapper(),
                generatedKeyExtractor
        );
        DatabaseAdvisorRepository advisorRepository = new DatabaseAdvisorRepository(
                connectionProvider,
                new AdvisorResultSetMapper(),
                generatedKeyExtractor
        );
        CustomerCvScoreService customerCvScoreService = new CustomerCvScoreServiceImpl(
                customerRepository,
                customerCvProfileRepository,
                flightRepository,
                new CVScoreCalcServiceImpl()
        );
        DatabaseCustomerView customerViewRepository = new DatabaseCustomerView(
                connectionProvider,
                new CustomerViewMapper(),
                openIncidentSummaryLoader,
                flightViewLoader,
                customerNoteLoader,
                previousFlightsSummaryFormatter,
                customerCvProfileRepository,
                advisorRepository,
                customerCvScoreService
        );
        DatabaseFeedbackLookup feedbackLookupRepository = new DatabaseFeedbackLookup(
                connectionProvider,
                new FeedbackResultSetMapper(),
                feedbackItemLoader
        );
        DatabaseFeedbackUpdate feedbackUpdateRepository = new DatabaseFeedbackUpdate(
                connectionProvider,
                generatedKeyExtractor,
                flightIdResolver
        );
        DatabaseFeedbackAnalytics feedbackAnalyticsRepository = new DatabaseFeedbackAnalytics(
                connectionProvider,
                new FeedbackItemResultSetMapper()
        );
        DatabaseNPSScores npsScoresRepository = new DatabaseNPSScores(connectionProvider);
        DatabaseIncidentRepository incidentRepository = new DatabaseIncidentRepository(
                connectionProvider,
                new IncidentResultSetMapper(),
                generatedKeyExtractor,
                flightIdResolver
        );
        DatabaseIncidentView incidentViewRepository = new DatabaseIncidentView(
                connectionProvider,
                new IncidentViewMapper(),
                flightViewLoader,
                previousFlightsSummaryFormatter,
                customerCvProfileRepository
        );
        DatabaseActionRepository actionRepository = new DatabaseActionRepository(
                connectionProvider,
                new ActionItemResultSetMapper(),
                generatedKeyExtractor
        );

        return new Backend(
                actionRepository,
                actionRepository,
                advisorRepository,
                customerCvProfileRepository,
                customerCvProfileRepository,
                customerRepository,
                customerNoteRepository,
                customerNoteRepository,
                customerRepository,
                customerRepository,
                customerViewRepository,
                customerCvScoreService,
                feedbackAnalyticsRepository,
                feedbackLookupRepository,
                feedbackUpdateRepository,
                npsScoresRepository,
                flightRepository,
                incidentRepository,
                incidentRepository,
                incidentRepository,
                incidentViewRepository
        );
    }
}
