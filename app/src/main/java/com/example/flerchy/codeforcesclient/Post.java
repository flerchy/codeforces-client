package com.example.flerchy.codeforcesclient;

import com.google.gson.annotations.SerializedName;

/**
 * Created by flerchy on 12.12.2016.
 */

public class Post {
    String title;
    @SerializedName("authorHandle")
    String author;
    String content;


    String getTitle() {
        return title;
    }

}
