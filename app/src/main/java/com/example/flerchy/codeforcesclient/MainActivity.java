package com.example.flerchy.codeforcesclient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.JsonArray;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Callable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String url = "http://codeforces.com/api/recentActions?maxCount=5";

        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                    JSONParser parser = new JSONParser();
                    final String responseData = response.body().string();

                    Log.d("response:", responseData);
                    ResponseObject respobj = parser.parse(responseData);

                    Log.d("response status:", respobj.getStatus());
                    Log.d("result:", respobj.getResults()[0].getBlogEntry().getTitle());
            }

        });
    }
}

