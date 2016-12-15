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
        if (user.getFirstName() != null)
            return this.getUser().getFirstName();
        else return "Anonymous";
    }

    public String getHandle() {
        return user.handle;
    }

    public String getPic() { return user.pic; }

    public String getLastName() {
        if (user.lastName != null)
            return user.lastName;
        else return "Anonymous";
    }

    public String getOrganization() {
        if (user.organization != null)
            return user.organization;
        else return "None";
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
