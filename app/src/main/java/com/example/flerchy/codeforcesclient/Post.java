package com.example.flerchy.codeforcesclient;

import com.google.gson.annotations.SerializedName;

/**
 * Created by flerchy on 12.12.2016.
 */

class Post {
    String title;
    @SerializedName("authorHandle")
    String author;


    String getTitle() {
        return title;
    }



}
