package config;

import model.enums.FeedbackCategory;
import model.enums.Packages;
import service.implementation.suggestion.DelaySuggestionResolver;
import service.implementation.suggestion.FeedbackSuggestionResolver;
import service.implementation.suggestion.OnboardingSuggestionResolver;
import service.implementation.suggestion.delay.DelayGoldSuggestionStrategy;
import service.implementation.suggestion.delay.DelayShortOperationalSuggestionStrategy;
import service.implementation.suggestion.delay.DelayStandardSuggestionStrategy;
import service.implementation.suggestion.delay.DelayVipSuggestionStrategy;
import service.implementation.suggestion.flight.FlightFeedbackGoldSuggestionStrategy;
import service.implementation.suggestion.flight.FlightFeedbackStandardSuggestionStrategy;
import service.implementation.suggestion.flight.FlightFeedbackVipSuggestionStrategy;
import service.implementation.suggestion.food.FoodGoldSuggestionStrategy;
import service.implementation.suggestion.food.FoodStandardSuggestionStrategy;
import service.implementation.suggestion.food.FoodVipSuggestionStrategy;
import service.implementation.suggestion.hotel.HotelGoldSuggestionStrategy;
import service.implementation.suggestion.hotel.HotelStandardSuggestionStrategy;
import service.implementation.suggestion.hotel.HotelVipSuggestionStrategy;
import service.interfaces.suggestions.PackageResolver;
import service.interfaces.suggestions.PackageSuggestionFactory;
import service.interfaces.suggestions.SuggestionResolver;
import service.interfaces.suggestions.SuggestionService;
import service.interfaces.suggestions.SuggestionStrategy;

import java.util.List;
import java.util.Map;

public class SuggestionServiceConfig {

    private SuggestionServiceConfig() {
    }

    public static SuggestionService build(PackageResolver packageResolver) {
        PackageSuggestionFactory foodFactory = new PackageSuggestionFactory(Map.of(
                Packages.VIP, new FoodVipSuggestionStrategy(),
                Packages.GOLD, new FoodGoldSuggestionStrategy(),
                Packages.STANDARD, new FoodStandardSuggestionStrategy()
        ));

        PackageSuggestionFactory hotelFactory = new PackageSuggestionFactory(Map.of(
                Packages.VIP, new HotelVipSuggestionStrategy(),
                Packages.GOLD, new HotelGoldSuggestionStrategy(),
                Packages.STANDARD, new HotelStandardSuggestionStrategy()
        ));

        PackageSuggestionFactory flightFeedbackFactory = new PackageSuggestionFactory(Map.of(
                Packages.VIP, new FlightFeedbackVipSuggestionStrategy(),
                Packages.GOLD, new FlightFeedbackGoldSuggestionStrategy(),
                Packages.STANDARD, new FlightFeedbackStandardSuggestionStrategy()
        ));

        PackageSuggestionFactory longDelayFactory = new PackageSuggestionFactory(Map.of(
                Packages.VIP, new DelayVipSuggestionStrategy(),
                Packages.GOLD, new DelayGoldSuggestionStrategy(),
                Packages.STANDARD, new DelayStandardSuggestionStrategy()
        ));

        SuggestionStrategy shortDelayStrategy = new DelayShortOperationalSuggestionStrategy();

        SuggestionResolver feedbackResolver = new FeedbackSuggestionResolver(
                Map.of(
                        FeedbackCategory.FOOD, foodFactory,
                        FeedbackCategory.HOTEL, hotelFactory,
                        FeedbackCategory.FLIGHT, flightFeedbackFactory
                ),
                packageResolver
        );

        SuggestionResolver delayResolver = new DelaySuggestionResolver(
                shortDelayStrategy,
                longDelayFactory,
                packageResolver
        );

        SuggestionResolver onboardingResolver = new OnboardingSuggestionResolver();

        return new SuggestionService(List.of(feedbackResolver, delayResolver, onboardingResolver));
    }
}