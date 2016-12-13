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
    private User user;

    public String getFirstName() {
        return user.firstName;
    }

    public String getHandle() {
        return user.handle;
    }

    public String getLastName() {
        return user.lastName;
    }

    public String getOrganization() {
        return user.organization;
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

    public User getUser() { return user; }
}
