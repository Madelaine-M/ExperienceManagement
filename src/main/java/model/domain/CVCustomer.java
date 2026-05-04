package model.domain;

import model.enums.CustomerType;
import model.enums.Packages;
import model.enums.PaymentMethod;

import java.util.Optional;

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

    public boolean isReturningCustomer() {
        return customer.isReturning();
    }

    public boolean hasLastFlight() {
        return lastFlight.isPresent();
    }

    public Optional<Packages> getLastBookingPackage() {
        return lastFlight.map(Flight::getBookingPackage);
    }

    public Packages getCurrentBookingPackage() {
        return currentFlight.getBookingPackage();
    }

    public boolean hasMarketingConsent() {
        return cvProfile != null && cvProfile.isMarketingPurpose();
    }

    public boolean hasNewsletterSubscription() {
        return cvProfile != null && cvProfile.isNewsletterSubscription();
    }

    public boolean isBookingConnectedToOtherPerson() {
        return cvProfile != null && cvProfile.isReferralCode();
    }

    public PaymentMethod getPaymentMethod() {
        return cvProfile != null && cvProfile.getPaymentMethod() != null
                ? cvProfile.getPaymentMethod()
                : PaymentMethod.IMMEDIATE;
    }

    public boolean isPublicFigure() {
        return cvProfile != null && cvProfile.isPublicPerson();
    }

    public CustomerType getCustomerType() {
        return cvProfile != null ? cvProfile.getCustomerType() : null;
    }
}