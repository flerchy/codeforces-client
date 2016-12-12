package com.example.flerchy.codeforcesclient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.gson.JsonArray;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    OkHttpClient client = new OkHttpClient();
    List<String> titles = new ArrayList<>();
    List<String> contents = new ArrayList<>();
    ArrayList<HashMap<String, Spanned>> myArrList = new ArrayList<>();


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
            public void onResponse(Call call, final Response response) throws IOException {
                    JSONParser parser = new JSONParser();
                    final String responseData = response.body().string();
                    HashMap<String, Spanned> map;
                    Log.d("response:", responseData);
                    ResponseObject respobj = parser.parse(responseData);

                    Log.d("response status:", respobj.getStatus());
                    int i = 0;
                    for(Result r : respobj.getResults()) {
                        if (r.getComment() != null) {
                            contents.add(r.getComment().getText());

                            Log.d("result:", r.getComment().getText());
                        } else {
                            contents.add("");
                        }
                        titles.add(r.getBlogEntry().getTitle());
                        Log.d("result:", r.getBlogEntry().getTitle());
                        map = new HashMap<>();
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            map.put("Title", Html.fromHtml(titles.get(i), Html.FROM_HTML_MODE_COMPACT));
                            map.put("Contents",  Html.fromHtml(contents.get(i), Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            map.put("Title", Html.fromHtml(titles.get(i)));
                            map.put("Contents",  Html.fromHtml(contents.get(i)));
                        }

                        myArrList.add(map);
                        i++;

                    }

                }

            @Override
            public void onFailure(Call call, IOException e) {

            }


        });



            // находим список
            ListView lvMain = (ListView) findViewById(R.id.lvMain);

            // создаем адаптер
           SimpleAdapter adapter = new SimpleAdapter(this, myArrList, android.R.layout.simple_list_item_2,
                new String[] {"Title", "Contents"},
                new int[] {android.R.id.text1, android.R.id.text2});

            lvMain.setAdapter(adapter);

        }
    }

