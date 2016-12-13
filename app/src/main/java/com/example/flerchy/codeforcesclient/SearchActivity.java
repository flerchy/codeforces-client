package com.example.flerchy.codeforcesclient;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.sql.Ref;
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
    RefreshFeedTask rfTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    public void onSearchClick(View view) throws InterruptedException {
        rfTask = new RefreshFeedTask();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://codeforces.com/api/user.info").newBuilder();
        urlBuilder.addQueryParameter("handles", ((EditText) findViewById(R.id.user_name)).getText().toString());
        String url = urlBuilder.build().toString();
        Log.d("url:", url);
        Request request = new Request.Builder()
                .url(url)
                .build();

        rfTask.execute(request);

    }

    class RefreshFeedTask extends AsyncTask<Request, Void, List<String>> {

        private OkHttpClient client = new OkHttpClient();
        private ResponseObject respobj = new ResponseObject();

        @Override
        protected List<String> doInBackground(Request... requests) {
            List<String> userString = new ArrayList<>();
            String userFirstName = new String();
            try {
                Response response = client.newCall(requests[0]).execute();
                JSONParser parser = new JSONParser();
                final String responseData = response.body().string();
                Log.d("response:", responseData);
                respobj = parser.parse(responseData);
                Log.d("response status:", respobj.getStatus());
            } catch (IOException e) {
                Log.e("FAIL:", "FAIL");
                e.printStackTrace();
            }
            Log.d("response status:", respobj.getStatus());
            Result r = respobj.getResults()[0];
            if (r != null) {
                userFirstName = r.getFirstName();
                Log.d("result:", userFirstName);
                Log.d("result:", r.getHandle());
            }
            userString.add(userFirstName);
            Log.d("userString:", userString.get(0));
            return userString;
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);
            String userFirstName = strings.get(0);
            Log.d("userString:", userFirstName);
            TextView tvSearch = (TextView) findViewById(R.id.tvSearch);
            tvSearch.setText(userFirstName);
            Log.d("userFirstName=", userFirstName);
        }
    }
}

