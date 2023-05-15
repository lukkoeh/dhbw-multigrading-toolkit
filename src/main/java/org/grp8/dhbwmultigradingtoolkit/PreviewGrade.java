package org.grp8.dhbwmultigradingtoolkit;

public class PreviewGrade {
    private String matriculation;
    private String firstname;
    private String lastname;
    private String points;
    private String percent;
    private String feedback;

    public PreviewGrade(String matriculation, String firstname, String lastname, String points, String percent, String feedback) {
        this.matriculation = matriculation;
        this.firstname = firstname;
        this.lastname = lastname;
        this.points = points;
        this.percent = percent;
        this.feedback = feedback;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getMatriculation() {
        return matriculation;
    }

    public void setMatriculation(String matriculation) {
        this.matriculation = matriculation;
    }
}
