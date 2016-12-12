package com.example.flerchy.codeforcesclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by flerchy on 12.12.2016.
 */

class JSONParser {
    private final static Gson gson;

    static {
        gson = new GsonBuilder().create();
    }

    ResponseObject parse(final String json) {
        return gson.fromJson(json, ResponseObject.class);
    }
}