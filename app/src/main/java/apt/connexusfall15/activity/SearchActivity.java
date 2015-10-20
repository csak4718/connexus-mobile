package apt.connexusfall15.activity;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import apt.connexusfall15.R;
import apt.connexusfall15.adapter.ImageAdapter;
import apt.connexusfall15.utils.Utils;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends ActionBarActivity {
    private static final String TAG  = "Search Activity";
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        String searchTerm = getIntent().getStringExtra("searchTerm");

        final String request_url = "http://connexus-fall15.appspot.com/Search_mobile?searchTerm="+searchTerm;
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(request_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                final ArrayList<String> coverUrls = new ArrayList<String>();
                final ArrayList<String> streamKeyList = new ArrayList<>();
                final ArrayList<String> streamNameList = new ArrayList<String>();
                try {
                    JSONObject jObject = new JSONObject(new String(responseBody));
                    JSONArray displayCoverUrl = jObject.getJSONArray("displayStreams");
                    JSONArray arrStreamKey = jObject.getJSONArray("streamKeyList");
                    JSONArray arrStreamName = jObject.getJSONArray("streamNameList");

                    for (int i = 0; i < displayCoverUrl.length(); i++) {
                        coverUrls.add(displayCoverUrl.getString(i));
                        streamKeyList.add(arrStreamKey.getString(i));
                        streamNameList.add(arrStreamName.getString(i));
//                        System.out.println(displayCoverUrl.getString(i));
                    }
                    GridView gridview = (GridView) findViewById(R.id.gridview_searchStreams);
                    gridview.setAdapter(new ImageAdapter(context, coverUrls));
                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {
                            Utils.gotoDisplayImages(SearchActivity.this, streamKeyList.get(position), streamNameList.get(position));
                        }
                    });
                } catch (JSONException j) {
                    System.out.println("JSON Error");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(TAG, "There was a problem in retrieving the url : " + error.toString());
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
