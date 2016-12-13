package com.example.flerchy.codeforcesclient;

import com.google.gson.annotations.SerializedName;

/**
 * Created by flerchy on 12.12.2016.
 */

class User {
    String handle;
    String firstName;
    String lastName;
    String organization;
    @SerializedName("titlePhoto")
    String pic;

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

    public String getPic() { return pic; }

    User(String h, String fn, String ln, String org, String p) {
        handle = h;
        firstName = fn;
        lastName = ln;
        organization = org;
        pic = p;
    }
}
