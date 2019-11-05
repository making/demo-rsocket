package com.example.demobackpressure.name;

import java.util.StringJoiner;

public class Name {

    private String lastName;

    private String firstName;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Name.class.getSimpleName() + "[", "]")
            .add("lastName='" + lastName + "'")
            .add("firstName='" + firstName + "'")
            .toString();
    }
}
