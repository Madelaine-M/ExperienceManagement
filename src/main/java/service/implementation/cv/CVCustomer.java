package service.implementation.cv;

import model.Customer;
import model.Flight;

import java.util.Optional;

import model.enums.CustomerType;
import model.enums.Packages;
import model.enums.PaymentMethod;

// AI
public class CVCustomer {
    private final Customer customer;
    private final Flight currentFlight;
    private final Optional<Flight> lastFlight;

    public CVCustomer(Customer customer, Flight currentFlight, Flight lastFlight) {
        this.customer = customer;
        this.currentFlight = currentFlight;
        this.lastFlight = Optional.ofNullable(lastFlight);
    }

    // Aus ReturningCustomerRule
    public boolean isReturningCustomer() {
        return customer.isReturning();
    }

    // Aus LastBookingTierRule
    public boolean hasLastFlight() {
        return lastFlight.isPresent();
    }

    public Optional<Packages> getLastBookingPackage() {
        return lastFlight.map(Flight::getBookingPackage);
    }

    public Packages getCurrentBookingPackage() {
        return currentFlight.getBookingPackage();
    }

    // Aus MarketingConsentRule
    public boolean hasMarketingConsent() {
        return customer.isMarketingPurpose();
    }

    // Aus NewsletterRule
    public boolean hasNewsletterSubscription() {
        return customer.isNewsletterSubscription();
    }

    // Aus BookingConnectionRule
    public boolean isBookingConnectedToOtherPerson() {
        return customer.isReferralCode();
    }

    // Aus PaymentMethodRule
    public PaymentMethod getPaymentMethod() {
        return customer.getPaymentMethod();
    }

    // Aus PublicFigureRule
    public boolean isPublicFigure() {
        return customer.isPublicPerson();
    }

    // Aus TravelingAsScientistRule
    public CustomerType isTravelingAsScientist() {
        return customer.getCustomerType();
    }
}
