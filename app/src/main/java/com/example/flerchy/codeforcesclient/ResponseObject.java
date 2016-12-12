package com.example.flerchy.codeforcesclient;

/**
 * Created by flerchy on 12.12.2016.
 */
public class ResponseObject {
    private String status;

    private Result[] result = new Result[0];

    public String getStatus() {
        return status;
    }

    public Result[] getResults() {
        return result;
    }
}
