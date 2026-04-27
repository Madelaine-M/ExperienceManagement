package service.interfaces.suggestions;


import model.enums.Packages;

import java.util.Map;

public class PackageSuggestionFactory {

    private final Map<Packages, SuggestionStrategy> strategyMap;

    public PackageSuggestionFactory(Map<Packages, SuggestionStrategy> strategyMap) {
        this.strategyMap = strategyMap;
    }

    public SuggestionStrategy getStrategy(Packages pkg) {
        SuggestionStrategy strategy = strategyMap.get(pkg);
        if (strategy == null) {
            throw new IllegalArgumentException("No suggestion strategy found for package: " + pkg);
        }
        return strategy;
    }
}
