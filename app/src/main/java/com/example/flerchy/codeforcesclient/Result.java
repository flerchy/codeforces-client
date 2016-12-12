package com.example.flerchy.codeforcesclient;

import com.google.gson.annotations.SerializedName;

/**
 * Created by flerchy on 12.12.2016.
 */
class Result {

    @SerializedName("timeSeconds")
    private int time;
    private Post blogEntry;
    private Comment comment;
    private String handle;
    private String firstName;
    private String lastName;
    private String organization;

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

    int getTime() {
        return time;
    }

    Post getBlogEntry() {
        return blogEntry;
    }

    Comment getComment() {
        return comment;
    }
}
