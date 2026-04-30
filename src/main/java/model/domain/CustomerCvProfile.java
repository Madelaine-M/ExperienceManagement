package model.domain;

import model.enums.CustomerType;
import model.enums.PaymentMethod;

public class CustomerCvProfile {
    private int customerId;
    private boolean marketingPurpose;
    private boolean newsletterSubscription;
    private boolean referralCode;
    private PaymentMethod paymentMethod = PaymentMethod.IMMEDIATE;
    private boolean publicPerson;
    private CustomerType customerType;

    public CustomerCvProfile() {
    }

    public CustomerCvProfile(int customerId, boolean marketingPurpose, boolean newsletterSubscription,
                             boolean referralCode, PaymentMethod paymentMethod, boolean publicPerson,
                             CustomerType customerType) {
        this.customerId = customerId;
        this.marketingPurpose = marketingPurpose;
        this.newsletterSubscription = newsletterSubscription;
        this.referralCode = referralCode;
        this.paymentMethod = paymentMethod;
        this.publicPerson = publicPerson;
        this.customerType = customerType;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public boolean isMarketingPurpose() {
        return marketingPurpose;
    }

    public void setMarketingPurpose(boolean marketingPurpose) {
        this.marketingPurpose = marketingPurpose;
    }

    public boolean isNewsletterSubscription() {
        return newsletterSubscription;
    }

    public void setNewsletterSubscription(boolean newsletterSubscription) {
        this.newsletterSubscription = newsletterSubscription;
    }

    public boolean isReferralCode() {
        return referralCode;
    }

    public void setReferralCode(boolean referralCode) {
        this.referralCode = referralCode;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public boolean isPublicPerson() {
        return publicPerson;
    }

    public void setPublicPerson(boolean publicPerson) {
        this.publicPerson = publicPerson;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }
}
