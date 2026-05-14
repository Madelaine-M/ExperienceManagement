package service.implementation.suggestion;


import model.domain.FeedbackIncident;
import model.domain.Incident;
import model.enums.FeedbackCategory;
import model.enums.IncidentType;
import model.enums.Packages;
import service.interfaces.suggestions.PackageResolver;
import service.interfaces.suggestions.SuggestionResolver;
import service.interfaces.suggestions.PackageSuggestionFactory;
import service.interfaces.suggestions.SuggestionStrategy;

import java.util.Map;

//AI used for resolver concept
public class FeedbackSuggestionResolver implements SuggestionResolver {

    private final Map<FeedbackCategory, PackageSuggestionFactory> categoryFactoryMap;
    private final PackageResolver packageResolver;

    public FeedbackSuggestionResolver(Map<FeedbackCategory, PackageSuggestionFactory> categoryFactoryMap,
                                      PackageResolver packageResolver) {
        this.categoryFactoryMap = categoryFactoryMap;
        this.packageResolver = packageResolver;
    }

    @Override
    public boolean supports(Incident incident) {
        return incident.getType() == IncidentType.FEEDBACK;
    }

    @Override
    public SuggestionStrategy resolve(Incident incident) {
        FeedbackIncident feedbackIncident = (FeedbackIncident) incident;
        FeedbackCategory category = feedbackIncident.getFeedbackType();

        PackageSuggestionFactory factory = categoryFactoryMap.get(category);
        if (factory == null) {
            throw new IllegalArgumentException("No suggestion factory for feedback category: " + category);
        }

        Packages pkg = packageResolver.getPackage(incident);
        return factory.getStrategy(pkg);
    }
}
