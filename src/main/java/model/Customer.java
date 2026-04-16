package model;

import model.enums.CustomerStatus;
import model.enums.CustomerType;
import model.enums.PaymentMethod;

public class Customer {

    private String firstName;
    private String lastName;
    private String email;
    private String birthDate;
    private CustomerStatus status = CustomerStatus.BOOKED;
    private String bookingDate;
    private boolean returning;
    private Integer assignedAdvisorId;
    private float clvScore;
    private String notes = "";
    private String preferences = "";
    private String applyToNextBooking = ""; // incentive wenn etwas beim letzten Mal schiefgealufen ist? --> vormerken, dwas geoofered wurde
    private boolean marketingPurpose;
    private boolean newsletterSubscription;
    private boolean referralCode;
    private PaymentMethod paymentMethod = PaymentMethod.IMMEDIATE;
    private boolean publicPerson;
    private CustomerType customerType;

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public void setStatus(CustomerStatus status) {
        this.status = status;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public boolean isReturning() {
        return returning;
    }

    public void setReturning(boolean returning) {
        this.returning = returning;
    }

    public Integer getAssignedAdvisorId() {
        return assignedAdvisorId;
    }

    public void setAssignedAdvisorId(Integer assignedAdvisorId) {
        this.assignedAdvisorId = assignedAdvisorId;
    }

    public float getClvScore() {
        return clvScore;
    }

    public void setClvScore(float clv_score) {
        this.clvScore= clv_score;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getApplyToNextBooking() {
        return applyToNextBooking;
    }

    public void setApplyToNextBooking(String applyToNextBooking) {
        this.applyToNextBooking = applyToNextBooking;
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
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

    public Customer() {}

    public Customer(int id, String firstName, String lastName, String email, String birthDate,
                    String bookingDate, CustomerType customerType) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthDate = birthDate;
        this.bookingDate = bookingDate;
        this.customerType = customerType;
    }

    public Customer(int id, String firstName, String lastName, String email, String birthDate, CustomerStatus status,
                    String bookingDate, boolean returning, Integer assignedAdvisorId, float clvScore, String notes,
                    String preferences, String applyToNextBooking, boolean marketingPurpose,
                    boolean newsletterSubscription, boolean referralCode, PaymentMethod paymentMethod,
                    boolean publicPerson, CustomerType customerType) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthDate = birthDate;
        this.status = status;
        this.bookingDate = bookingDate;
        this.returning = returning;
        this.assignedAdvisorId = assignedAdvisorId;
        this.clvScore = clvScore;
        this.notes = notes;
        this.preferences = preferences;
        this.applyToNextBooking = applyToNextBooking;
        this.marketingPurpose = marketingPurpose;
        this.newsletterSubscription = newsletterSubscription;
        this.referralCode = referralCode;
        this.paymentMethod = paymentMethod;
        this.publicPerson = publicPerson;
        this.customerType = customerType;
    }

}
