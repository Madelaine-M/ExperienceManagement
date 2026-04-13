package model;

import model.enums.CustomerStatus;
import model.enums.CustomerType;
import model.enums.Packages;
import model.enums.PaymentMethod;

public class Customer {

    private String firstName;
    private String lastName;
    private String email;
    private String birthDate;
    private CustomerStatus status;
    private Packages bookingPackage;
    private Packages previousBookingPackage;
    private String bookingDate;
    private String flightDate;
    private int flightId;
    private boolean returning;
    private int assignedAdvisorId;
    private float clvScore;
    private String notes;
    private String preferences;
    private String applyToNextBooking; // incentive wenn etwas beim letzten Mal schiefgealufen ist? --> vormerken, dwas geoofered wurde
    private boolean marketingPurpose;
    private boolean newsletterSubscription;
    private boolean referralCode;
    private PaymentMethod paymentMethod;
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

    public Packages getBookingPackage() {
        return bookingPackage;
    }

    public void setBookingPackage(Packages bookingPackage) {
        this.bookingPackage = bookingPackage;
    }

    public Packages getPreviousBookingPackage() {
        return previousBookingPackage;
    }

    public void setPreviousBookingPackage(Packages previousBookingPackage) {
        this.previousBookingPackage = previousBookingPackage;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getFlightDate() {
        return flightDate;
    }

    public void setFlightDate(String flightDate) {
        this.flightDate = flightDate;
    }

    public boolean isReturning() {
        return returning;
    }

    public void setReturning(boolean returning) {
        this.returning = returning;
    }

    public int getAssignedAdvisorId() {
        return assignedAdvisorId;
    }

    public void setAssignedAdvisorId(int assignedAdvisorId) {
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

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    // constructor
    public Customer() {}

    public Customer(String firstName, String lastName, String email, CustomerStatus status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.status = status;
    }


}
