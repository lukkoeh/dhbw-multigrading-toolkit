package org.grp8.dhbwmultigradingtoolkit;

/**
 * A Class to represent a Student as an Object, also for population of TableViews.
 */
public class Student {
    private String no;
    private String firstname;
    private String lastname;

    public Student(String no, String firstname, String lastname) {
        this.no = no;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
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
}
