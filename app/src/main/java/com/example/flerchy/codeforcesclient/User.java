package com.example.flerchy.codeforcesclient;

import com.google.gson.annotations.SerializedName;

/**
 * Created by flerchy on 12.12.2016.
 */

class User {
    String handle;
    String firstName = "Anonymous";
    String lastName = "Anonymous";
    String organization = "None";
    @SerializedName("titlePhoto")
    String pic;

    public String getFirstName() {
        if (firstName != null)
            return firstName;
        else return "Anonymous";
    }

    public String getHandle() {
        return handle;
    }

    public String getLastName() {
        if (lastName != null)
            return lastName;
        else return "Anonymous";
    }

    public String getOrganization() {
        if (organization != null)
            return organization;
        else return "None";
    }

    public String getPic() { return pic; }

    User(String h, String fn, String ln, String org, String p) {
        handle = h;
        firstName = fn;
        lastName = ln;
        organization = org;
        pic = p;
    }
}
