package service.implementation.suggestion;



import model.DelayIncident;
import model.Incident;
import model.enums.IncidentType;
import model.enums.Packages;
import service.interfaces.suggestions.PackageResolver;
import service.interfaces.suggestions.PackageSuggestionFactory;
import service.interfaces.suggestions.SuggestionResolver;
import service.interfaces.suggestions.SuggestionStrategy;

public class DelaySuggestionResolver implements SuggestionResolver {

    private static final int LONG_DELAY_THRESHOLD_MINUTES = 60;

    private final PackageSuggestionFactory longDelayFactory;
    private final PackageResolver packageResolver;

    public DelaySuggestionResolver(PackageSuggestionFactory longDelayFactory,
                                   PackageResolver packageResolver) {
        this.longDelayFactory = longDelayFactory;
        this.packageResolver = packageResolver;
    }

    @Override
    public boolean supports(Incident incident) {
        return incident.getType() == IncidentType.DELAY;
    }

    @Override
    public SuggestionStrategy resolve(Incident incident) {
        DelayIncident delayIncident = (DelayIncident) incident;

        if (delayIncident.getDelayMinutes() > LONG_DELAY_THRESHOLD_MINUTES) {
            Packages pkg = packageResolver.getPackage(incident);
            return longDelayFactory.getStrategy(pkg);
        }

        throw new IllegalArgumentException("No suggestion strategy for delay under threshold");
    }
}
