package com.example.flerchy.codeforcesclient;

import com.google.gson.annotations.SerializedName;

/**
 * Created by flerchy on 12.12.2016.
 */
class Comment {

    @SerializedName("commentatorHandle")
    private String author;

    private String text;

    public String getAuthor() {
        return author;
    }

    String getText() {
        return text;
    }
}
