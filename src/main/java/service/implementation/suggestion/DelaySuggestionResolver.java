package service.implementation.suggestion;

import model.domain.DelayIncident;
import model.domain.Incident;
import model.enums.IncidentType;
import model.enums.Packages;
import service.interfaces.suggestions.PackageResolver;
import service.interfaces.suggestions.PackageSuggestionFactory;
import service.interfaces.suggestions.SuggestionResolver;
import service.interfaces.suggestions.SuggestionStrategy;

public class DelaySuggestionResolver implements SuggestionResolver {

    private static final int LONG_DELAY_THRESHOLD_MINUTES = 60;

    private final SuggestionStrategy shortDelayStrategy;
    private final PackageSuggestionFactory longDelayFactory;
    private final PackageResolver packageResolver;

    public DelaySuggestionResolver(SuggestionStrategy shortDelayStrategy,
                                   PackageSuggestionFactory longDelayFactory,
                                   PackageResolver packageResolver) {
        this.shortDelayStrategy = shortDelayStrategy;
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

        if (delayIncident.getDelayMinutes() != null
                && delayIncident.getDelayMinutes() > LONG_DELAY_THRESHOLD_MINUTES) {
            Packages pkg = packageResolver.getPackage(incident);
            return longDelayFactory.getStrategy(pkg);
        }

        return shortDelayStrategy;
    }
}