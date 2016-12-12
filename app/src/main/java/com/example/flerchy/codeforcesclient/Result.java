package com.example.flerchy.codeforcesclient;

import com.google.gson.annotations.SerializedName;

/**
 * Created by flerchy on 12.12.2016.
 */
public class Result {

    @SerializedName("timeSeconds")
    private int time;
    private Post blogEntry;


    int getTime() {
        return time;
    }

    Post getBlogEntry() {
        return blogEntry;
    }

}
