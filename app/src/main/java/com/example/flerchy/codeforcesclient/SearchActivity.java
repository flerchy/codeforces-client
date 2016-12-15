package com.example.flerchy.codeforcesclient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {
    FindUserTask fuTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    public void onSearchClick(View view) throws InterruptedException {
        fuTask = new FindUserTask();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://codeforces.com/api/user.info").newBuilder();
        urlBuilder.addQueryParameter("handles", ((EditText) findViewById(R.id.user_name)).getText().toString());
        String url = urlBuilder.build().toString();
        Log.d("url:", url);
        Request request = new Request.Builder()
                .url(url)
                .build();

        fuTask.execute(request);

    }


    class FindUserTask extends AsyncTask<Request, Void, List<String>> {

        private OkHttpClient client = new OkHttpClient();
        private ResponseObject respobj = new ResponseObject();

        @Override
        protected List<String> doInBackground(Request... requests) {
            List<String> userString = new ArrayList<>();
            String userFirstName = null;
            String userLastName = new String();
            String userOrg = new String();
            String userPic = new String();
            try {
                Response response = client.newCall(requests[0]).execute();
                JSONParser parser = new JSONParser();
                final String responseData = response.body().string();
                Log.d("response:", responseData);
                respobj = parser.parse(responseData);
                Log.d("response status:", respobj.getStatus());
                Log.d("response status:", respobj.getStatus());
                if (respobj.getStatus().equals("FAILED")) {
                    userFirstName = "No user found";
                } else {
                    Result r = respobj.getResults()[0];
                    if (r != null) {
                        userFirstName = r.getFirstName();
                        userLastName = r.getLastName();
                        userOrg = r.getOrganization();
                        userPic = r.getPic();
                        Log.d("result:", userFirstName);
                        Log.d("result:", r.getHandle());
                    }
                }

            } catch (IOException e) {
                Log.e("FAIL:", "FAIL");
                e.printStackTrace();
                userFirstName = "No internet";
            }

            userString.add(userFirstName);
            userString.add(userLastName);
            userString.add(userOrg);
            userString.add(userPic);
            return userString;
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);
            String userFirstName = strings.get(0);
            String userLastName = strings.get(1);
            String userOrg = strings.get(2);

            new DownloadImageTask((ImageView) findViewById(R.id.user_pic))
                    .execute(strings.get(3));

            TextView firstName = (TextView) findViewById(R.id.user_first_name);
            TextView lastName = (TextView) findViewById(R.id.user_last_name);
            TextView org = (TextView) findViewById(R.id.user_org);

            firstName.setVisibility(View.VISIBLE);
            firstName.setText(userFirstName);

            lastName.setVisibility(View.VISIBLE);
            lastName.setText(userLastName);

            org.setVisibility(View.VISIBLE);
            org.setText(userOrg);
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}

