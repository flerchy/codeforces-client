package com.example.flerchy.codeforcesclient;

import android.util.Log;

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
        if (json.contains("\"firstName\":")) {
            int leftIndex = json.indexOf("[");
            int rightIndex = json.indexOf("]");
            String result = json.substring(0, leftIndex) + "[{\"user\":" +
                            json.substring(leftIndex + 1, rightIndex) +
                            "}]" + json.substring(rightIndex + 1);
            Log.d("res:", result);
            return gson.fromJson(result, ResponseObject.class);
        } else {
            return gson.fromJson(json, ResponseObject.class);
        }
    }
}