package service.implementation.cv;

import model.domain.Customer;
import model.domain.CustomerCvProfile;
import model.domain.Flight;

import java.util.Optional;

import model.enums.CustomerType;
import model.enums.Packages;
import model.enums.PaymentMethod;

// AI
public class CVCustomer {
    private final Customer customer;
    private final CustomerCvProfile cvProfile;
    private final Flight currentFlight;
    private final Optional<Flight> lastFlight;

    public CVCustomer(Customer customer, CustomerCvProfile cvProfile, Flight currentFlight, Flight lastFlight) {
        this.customer = customer;
        this.cvProfile = cvProfile;
        this.currentFlight = currentFlight;
        this.lastFlight = Optional.ofNullable(lastFlight);
    }

    // ReturningCustomerRule
    public boolean isReturningCustomer() {
        return customer.isReturning();
    }

    // LastBookingTierRule
    public boolean hasLastFlight() {
        return lastFlight.isPresent();
    }

    public Optional<Packages> getLastBookingPackage() {
        return lastFlight.map(Flight::getBookingPackage);
    }

    public Packages getCurrentBookingPackage() {
        return currentFlight.getBookingPackage();
    }

    // MarketingConsentRule
    public boolean hasMarketingConsent() {
        return cvProfile != null && cvProfile.isMarketingPurpose();
    }

    // NewsletterRule
    public boolean hasNewsletterSubscription() {
        return cvProfile != null && cvProfile.isNewsletterSubscription();
    }

    // BookingConnectionRule
    public boolean isBookingConnectedToOtherPerson() {
        return cvProfile != null && cvProfile.isReferralCode();
    }

    // PaymentMethodRule
    public PaymentMethod getPaymentMethod() {
        return cvProfile != null && cvProfile.getPaymentMethod() != null
                ? cvProfile.getPaymentMethod()
                : PaymentMethod.IMMEDIATE;
    }

    // PublicFigureRule
    public boolean isPublicFigure() {
        return cvProfile != null && cvProfile.isPublicPerson();
    }

    // TravelingAsScientistRule
    public CustomerType isTravelingAsScientist() {
        return cvProfile != null ? cvProfile.getCustomerType() : null;
    }
}
