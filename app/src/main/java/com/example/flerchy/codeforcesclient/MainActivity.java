package com.example.flerchy.codeforcesclient;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    List<String> titles = new ArrayList<>();
    List<String> contents = new ArrayList<>();
    RefreshFeedTask rfTask;
    ListView lvMain;
    boolean serviceIsRunning;
    ArrayList<HashMap<String, Spanned>> myArrList;

    Context context;

    @Override
    public ArrayList<HashMap<String, Spanned>> onRetainCustomNonConfigurationInstance() {

        Log.d("arrlist", String.valueOf(this.myArrList.size()));
        Log.d("arrlist", String.valueOf(myArrList.size()));
        return this.myArrList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceIsRunning = false;
        myArrList = (ArrayList<HashMap<String, Spanned>>) getLastCustomNonConfigurationInstance();
        setContentView(R.layout.activity_main);
        lvMain = (ListView) findViewById(R.id.lvMain);

        if (myArrList != null) {
            SimpleAdapter adapter = new SimpleAdapter(this, myArrList, android.R.layout.simple_list_item_2,
                    new String[]{"Title", "Contents"},
                    new int[]{android.R.id.text1, android.R.id.text2});
            lvMain.setAdapter(adapter);
        } else {
            myArrList = new ArrayList<>();
        }
        String url = "http://codeforces.com/api/recentActions?maxCount=10";
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
                 Thread t = new Thread(new Runnable() {
                    public void run() {
                        while (serviceIsRunning) {
                            rfTask = new RefreshFeedTask(context);
                            try {
                                String url = "http://codeforces.com/api/recentActions?maxCount=10";
                                Request request = new Request.Builder()
                                    .url(url)
                                    .build();
                                rfTask.execute(request);

                                TimeUnit.SECONDS.sleep(5);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                t.start();
                if (!serviceIsRunning) {
                    t.interrupt();
                }
                return true;
            case R.id.stop_update:
                serviceIsRunning = false;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

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
        private ResponseObject respobj = new ResponseObject();
        private Context mContext;

        public RefreshFeedTask(Context context) {
            mContext = context;
        }

        ArrayList<HashMap<String, Spanned>> newArrList = new ArrayList<>();
        @Override
        protected ArrayList<HashMap<String, Spanned>> doInBackground(Request... requests) {
            try {
                Response response = client.newCall(requests[0]).execute();
                JSONParser parser = new JSONParser();
                HashMap<String, Spanned> map;
                final String responseData = response.body().string();
                respobj = parser.parse(responseData);
                int i = 0;
                for(Result r : respobj.getResults()) {
                    if (r.getComment() != null) {
                        contents.add(r.getComment().getText());
                    } else {
                        contents.add("");
                    }
                    titles.add(r.getBlogEntry().getTitle());
                    map = new HashMap<>();
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        map.put("Title", Html.fromHtml(titles.get(i), Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH));
                        map.put("Contents",  Html.fromHtml(contents.get(i), Html.FROM_HTML_OPTION_USE_CSS_COLORS));
                    } else {
                        map.put("Title", Html.fromHtml(titles.get(i)));
                        map.put("Contents",  Html.fromHtml(contents.get(i)));
                    }
                    newArrList.add(map);
                    i++;
                }
            } catch (IOException e) {
                Log.e("FAIL:", "FAIL");
                e.printStackTrace();
            }
            return newArrList;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, Spanned>> hashMaps) {
            super.onPostExecute(hashMaps);
            Log.e("after error", String.valueOf(myArrList.size()));
            newArrList = hashMaps;
            if (newArrList.size() == 0) {
                newArrList = myArrList;
            }
            lvMain = (ListView) findViewById(R.id.lvMain);
            SimpleAdapter adapter = new SimpleAdapter(this.mContext, newArrList, android.R.layout.simple_list_item_2,
                    new String[] {"Title", "Contents"},
                    new int[] {android.R.id.text1, android.R.id.text2});
            myArrList = newArrList;
            lvMain.setAdapter(adapter);
        }
    }
}

