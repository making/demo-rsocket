package com.example.rsocket;

import java.util.StringJoiner;

public class Name {

    private final String firstName;

    private final String lastName;

    public Name(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }


    @Override
    public String toString() {
        return new StringJoiner(", ", Name.class.getSimpleName() + "[", "]")
            .add("firstName='" + firstName + "'")
            .add("lastName='" + lastName + "'")
            .toString();
    }
}
