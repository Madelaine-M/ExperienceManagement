package model.domain;

import model.enums.CustomerType;
import model.enums.Packages;
import model.enums.PaymentMethod;

import java.util.List;
import java.util.Optional;

public class CVCustomer {
    private final Customer customer;
    private final CustomerCvProfile cvProfile;
    private final List<Flight> flights;

    public CVCustomer(Customer customer, CustomerCvProfile cvProfile, List<Flight> flights) {
        this.customer = customer;
        this.cvProfile = cvProfile;
        this.flights = flights != null ? List.copyOf(flights) : List.of();
    }

    public boolean isReturningCustomer() {
        return customer.isReturning();
    }

    public boolean hasLastFlight() {
        return flights.size() > 1;
    }

    public Optional<Packages> getLastBookingPackage() {
        if (flights.size() <= 1) {
            return Optional.empty();
        }
        return Optional.ofNullable(flights.get(1).getBookingPackage());
    }

    public Packages getCurrentBookingPackage() {
        if (flights.isEmpty() || flights.get(0).getBookingPackage() == null) {
            return Packages.STANDARD;
        }
        return flights.get(0).getBookingPackage();
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