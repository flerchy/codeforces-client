package com.example.flerchy.codeforcesclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {
    OkHttpClient client = new OkHttpClient();
    public String userFirstName = new String();
    public ResponseObject respobj = new ResponseObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    public void onSearchClick(View view) throws InterruptedException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://codeforces.com/api/user.info").newBuilder();
        urlBuilder.addQueryParameter("handles", ((EditText) findViewById(R.id.user_name)).getText().toString());

        String url = urlBuilder.build().toString();
        Log.d("url:", url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                JSONParser parser = new JSONParser();
                final String responseData = response.body().string();
                Log.d("response:", responseData);
                respobj = parser.parse(responseData);
                Log.d("response status:", respobj.getStatus());
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("FAIL:", "FAIL");
            }

        });

        Log.d("response status:", respobj.getStatus());
        Result r = respobj.getResults()[0];
        if (r != null) {
            userFirstName = r.getFirstName();

            Log.d("result:", userFirstName);

            Log.d("result:", r.getHandle());

        }
        TextView tvSearch = (TextView) findViewById(R.id.tvSearch);
        tvSearch.setText(userFirstName);
        Log.d("userFirstName=", userFirstName);
    }
}

