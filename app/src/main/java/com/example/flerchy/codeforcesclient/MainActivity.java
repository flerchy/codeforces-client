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
    List<String> titles = new ArrayList<>();
    List<String> contents = new ArrayList<>();
    RefreshFeedTask rfTask;
    ListView lvMain;
    boolean serviceIsRunning;
    ArrayList<HashMap<String, Spanned>> myArrList;
    String FILENAME = "json_log";
    Context context;

    @Override
    public ArrayList<HashMap<String, Spanned>> onRetainCustomNonConfigurationInstance() {
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

        public String readSavedData(FileInputStream fIn) {
            StringBuffer datax = new StringBuffer("");
            try {
                InputStreamReader isr = new InputStreamReader(fIn);
                BufferedReader buffreader = new BufferedReader(isr);

                String readString = buffreader.readLine();
                while (readString != null) {
                    datax.append(readString);
                    readString = buffreader.readLine();
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

        ArrayList<HashMap<String, Spanned>> newArrList = new ArrayList<>();
        @Override
        protected ArrayList<HashMap<String, Spanned>> doInBackground(Request... requests) {
            try {
                Response response = client.newCall(requests[0]).execute();
                JSONParser parser = new JSONParser();
                HashMap<String, Spanned> map;
                final String responseData = response.body().string();

                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(FILENAME, Context.MODE_PRIVATE));
                outputStreamWriter.write(responseData);
                outputStreamWriter.close();
                FileInputStream fis = null;
                try {
                    fis = openFileInput(FILENAME);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                String respFromFile = readSavedData(fis);
                Log.e("resp from file1", respFromFile);
                Log.e("respString", responseData);
                try {
                    fis.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
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
                JSONParser parser = new JSONParser();
                HashMap<String, Spanned> map;

                FileInputStream fis = null;
                try {
                    fis = openFileInput(FILENAME);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                String respFromFile = readSavedData(fis);
                Log.e("resp from file2", respFromFile);
                try {
                    fis.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                respobj = parser.parse(respFromFile);
                int i = 0;
                for (Result r : respobj.getResults()) {
                    if (r.getComment() != null) {
                        contents.add(r.getComment().getText());
                    } else {
                        contents.add("");
                    }
                    titles.add(r.getBlogEntry().getTitle());
                    map = new HashMap<>();
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        map.put("Title", Html.fromHtml(titles.get(i), Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH));
                        map.put("Contents", Html.fromHtml(contents.get(i), Html.FROM_HTML_OPTION_USE_CSS_COLORS));
                    } else {
                        map.put("Title", Html.fromHtml(titles.get(i)));
                        map.put("Contents", Html.fromHtml(contents.get(i)));
                    }
                    newArrList.add(map);
                    i++;
                }
            }
            return newArrList;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, Spanned>> hashMaps) {
            super.onPostExecute(hashMaps);
            Log.e("after error", String.valueOf(myArrList.size()));
            myArrList = hashMaps;
            lvMain = (ListView) findViewById(R.id.lvMain);
            SimpleAdapter adapter = new SimpleAdapter(this.mContext, newArrList, android.R.layout.simple_list_item_2,
                    new String[] {"Title", "Contents"},
                    new int[] {android.R.id.text1, android.R.id.text2});
            myArrList = newArrList;
            lvMain.setAdapter(adapter);
        }
    }
}

