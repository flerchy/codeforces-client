package com.example.flerchy.codeforcesclient;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    ListView lvMain;
    boolean serviceIsRunning;
    String FILENAME = "json_log";
    Context context;
    private Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RefreshFeedTask rfTask;
        super.onCreate(savedInstanceState);
        serviceIsRunning = false;
       setContentView(R.layout.activity_main);
        lvMain = (ListView) findViewById(R.id.lvMain);
        String url = "http://codeforces.com/api/recentActions?maxCount=15";
        Request request = new Request.Builder()
                .url(url)
                .build();
        context = this;
        rfTask = new RefreshFeedTask(context);
        rfTask.execute(request);
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        serviceIsRunning = false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d("id", item.getTitle().toString());

        switch (id) {
            case R.id.action_search:
                Intent myIntent = new Intent(MainActivity.this, SearchActivity.class);
                MainActivity.this.startActivity(myIntent);
                return true;
            case R.id.start_update:
                serviceIsRunning = true;
                mHandler.removeCallbacks(refreshFeed);
                mHandler.postDelayed(refreshFeed, 3000);
                //todo:rewrite with handler
                return true;
            case R.id.stop_update:
                serviceIsRunning = false;
                mHandler.removeCallbacks(refreshFeed);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Runnable refreshFeed = new Runnable() {
        public void run() {
            RefreshFeedTask rfTask;
            rfTask = new RefreshFeedTask(context);
                String url = "http://codeforces.com/api/recentActions?maxCount=10";
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                rfTask.execute(request);
            mHandler.postDelayed(this, 3000);
        }
    };


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(serviceIsRunning) {
            menu.findItem(R.id.start_update).setVisible(false);
            menu.findItem(R.id.stop_update).setVisible(true);
        } else {
            menu.findItem(R.id.stop_update).setVisible(false);
            menu.findItem(R.id.start_update).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    class RefreshFeedTask extends AsyncTask<Request, Void, ArrayList<HashMap<String, Spanned>>> {

        private OkHttpClient client = new OkHttpClient();
        private Context mContext;

        public String readSavedData(FileInputStream fIn) {
            StringBuffer datax = new StringBuffer("");
            try {
                InputStreamReader isr = new InputStreamReader(fIn);
                BufferedReader buffReader = new BufferedReader(isr);

                String readString = buffReader.readLine();
                while (readString != null) {
                    datax.append(readString);
                    readString = buffReader.readLine();
                }

                isr.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return datax.toString();
        }


        public RefreshFeedTask(Context context) {
            mContext = context;
        }

        @Override
        protected ArrayList<HashMap<String, Spanned>> doInBackground(Request... requests) {
            try {
                Response response = client.newCall(requests[0]).execute();
                final String responseData = response.body().string();
                Log.e("ok!", responseData);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(FILENAME, Context.MODE_PRIVATE));
                outputStreamWriter.write(responseData);
                outputStreamWriter.close();
                return parseData(responseData);
            } catch (IOException e) {
                Log.e("exception", "sorry");
                FileInputStream fis = null;
                try {
                    fis = openFileInput(FILENAME);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                String respFromFile = readSavedData(fis);
                try {
                    fis.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                return parseData(respFromFile);
            }
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, Spanned>> hashMaps) {
            super.onPostExecute(hashMaps);
            ArrayList<HashMap<String, Spanned>> newArrList;
            Log.e("last title", String.valueOf(hashMaps.get(0).get("Contents")));
            newArrList = hashMaps;
            lvMain = (ListView) findViewById(R.id.lvMain);
            SimpleAdapter adapter = new SimpleAdapter(this.mContext, newArrList, android.R.layout.simple_list_item_2,
                    new String[] {"Title", "Contents"},
                    new int[] {android.R.id.text1, android.R.id.text2});
            lvMain.setAdapter(adapter);
        }


        public HashMap<String, Spanned> HTMLWrapper(String title, String content) {
            HashMap<String, Spanned> map;
            map = new HashMap<>();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                map.put("Title", Html.fromHtml(title, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH));
                map.put("Contents", Html.fromHtml(content, Html.FROM_HTML_OPTION_USE_CSS_COLORS));
            } else {
                map.put("Title", Html.fromHtml(title));
                map.put("Contents", Html.fromHtml(content));
            }
            return map;
        }

        private ArrayList<HashMap<String, Spanned>> parseData(String respString) {
            List<String> titles = new ArrayList<>();
            List<String> contents = new ArrayList<>();
            ArrayList<HashMap<String, Spanned>> hashMaps = new ArrayList<>();
            JSONParser parser = new JSONParser();
            ResponseObject respObj = parser.parse(respString);
            int i = 0;
            for(Result r : respObj.getResults()) {
                if (r.getComment() != null) {
                    contents.add(r.getComment().getText());
                } else {
                    contents.add("");
                }
                titles.add(r.getBlogEntry().getTitle());
                hashMaps.add(HTMLWrapper(titles.get(i), contents.get(i)));
                i++;
            }
            return hashMaps;
        }

    }
}

