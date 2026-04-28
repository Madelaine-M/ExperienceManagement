package config;


import model.enums.FeedbackCategory;
import model.enums.Packages;
import service.implementation.suggestion.delay.*;
import service.implementation.suggestion.flight.*;
import service.implementation.suggestion.food.*;
import service.implementation.suggestion.hotel.*;
import service.interfaces.suggestions.*;
import service.implementation.suggestion.*;


import java.util.List;
import java.util.Map;

public class SuggestionServiceConfig {

    public static SuggestionService build(PackageResolver packageResolver) {

        PackageSuggestionFactory foodFactory = new PackageSuggestionFactory(Map.of(
                Packages.VIP,      new FoodVipSuggestionStrategy(),
                Packages.GOLD,     new FoodGoldSuggestionStrategy(),
                Packages.STANDARD, new FoodStandardSuggestionStrategy()
        ));

        PackageSuggestionFactory hotelFactory = new PackageSuggestionFactory(Map.of(
                Packages.VIP,      new HotelVipSuggestionStrategy(),
                Packages.GOLD,     new HotelGoldSuggestionStrategy(),
                Packages.STANDARD, new HotelStandardSuggestionStrategy()
        ));

        PackageSuggestionFactory flightFeedbackFactory = new PackageSuggestionFactory(Map.of(
                Packages.VIP,      new FlightFeedbackVipSuggestionStrategy(),
                Packages.GOLD,     new FlightFeedbackGoldSuggestionStrategy(),
                Packages.STANDARD, new FlightFeedbackStandardSuggestionStrategy()
        ));

        PackageSuggestionFactory delayFactory = new PackageSuggestionFactory(Map.of(
                Packages.VIP,      new DelayVipSuggestionStrategy(),
                Packages.GOLD,     new DelayGoldSuggestionStrategy(),
                Packages.STANDARD, new DelayStandardSuggestionStrategy()
        ));

        SuggestionResolver feedbackResolver = new FeedbackSuggestionResolver(
                Map.of(
                        FeedbackCategory.FOOD,   foodFactory,
                        FeedbackCategory.HOTEL,  hotelFactory,
                        FeedbackCategory.FLIGHT, flightFeedbackFactory
                ),
                packageResolver
        );

        SuggestionResolver delayResolver = new DelaySuggestionResolver(
                delayFactory,
                packageResolver
        );

        return new SuggestionService(List.of(feedbackResolver, delayResolver));
    }
}
