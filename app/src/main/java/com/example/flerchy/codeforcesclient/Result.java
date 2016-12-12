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
