package model.domain;

public class Advisor {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String speciality;
    private int workloadScore;

    public Advisor() {
    }

    public Advisor(String firstName, String lastName, String email, String speciality, int workloadScore) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.speciality = speciality;
        this.workloadScore = workloadScore;
    }

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

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public int getWorkloadScore() {
        return workloadScore;
    }

    public void setWorkloadScore(int workloadScore) {
        this.workloadScore = workloadScore;
    }
}
