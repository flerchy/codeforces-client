package com.example.flerchy.codeforcesclient;

/**
 * Created by flerchy on 12.12.2016.
 */

class User {
    String handle;
    String firstName;
    String lastName;
    String organization;

    public String getFirstName() {
        return firstName;
    }

    public String getHandle() {
        return handle;
    }

    public String getLastName() {
        return lastName;
    }

    public String getOrganization() {
        return organization;
    }

    User(String h, String fn, String ln, String org) {
        handle = h;
        firstName = fn;
        lastName = ln;
        organization = org;
    }
}
