package apt.connexusfall15.activity;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

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

// TODO: More button. And show stream name under each cover url
public class SearchActivity extends ActionBarActivity {
    private static final String TAG  = "Search Activity";
    Context context = this;
    int result_viewed = 0;
    JSONArray displayCoverUrl = new JSONArray();
    JSONArray arrStreamKey = new JSONArray();
    JSONArray arrStreamName = new JSONArray();
    ArrayList<String> coverUrls = new ArrayList<String>();
    ArrayList<String> streamKeyList = new ArrayList<>();
    ArrayList<String> streamNameList = new ArrayList<String>();
    int search_result =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        final String userEmail = getIntent().getStringExtra("userEmail");
        String searchTerm = getIntent().getStringExtra("searchTerm");
        search(searchTerm, userEmail);

        final EditText edt_search = (EditText) findViewById(R.id.edt_search);
        Button searchButton = (Button) findViewById(R.id.btn_search);
        Button MoreResult = (Button) findViewById(R.id.btn_more_results);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchTerm = edt_search.getText().toString();
                search(searchTerm, userEmail);
            }
        });
        MoreResult.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (search_result == 1){
                    result_viewed = result_viewed + 8;
                    try {
                        if(result_viewed+8 <= displayCoverUrl.length()){
                            for (int i = result_viewed; i < displayCoverUrl.length() && i < result_viewed + 8; i++) {
                                coverUrls.clear();
                                streamKeyList.clear();
                                streamNameList.clear();
                                coverUrls.add(displayCoverUrl.getString(i));
                                streamKeyList.add(arrStreamKey.getString(i));
                                streamNameList.add(arrStreamName.getString(i));
//                              System.out.println(displayCoverUrl.getString(i));
                            }
                        }
                    }catch(JSONException j) {
                        System.out.println("JSON Error");
                    }
                    GridView gridview = (GridView) findViewById(R.id.gridview_searchStreams);
                    gridview.setAdapter(new ImageAdapter(context, coverUrls));
                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {
                            Utils.gotoViewSingleStreamActivity(SearchActivity.this, streamKeyList.get(position), streamNameList.get(position), userEmail);
                        }
                    });
                }
            }
        });
    }

    public void search(final String searchTerm, final String userEmail) {
        final String request_url = "http://connexus-fall15.appspot.com/Search_mobile?searchTerm="+searchTerm;
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(request_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject jObject = new JSONObject(new String(responseBody));
                    displayCoverUrl = jObject.getJSONArray("displayStreams");
                    arrStreamKey = jObject.getJSONArray("streamKeyList");
                    arrStreamName = jObject.getJSONArray("streamNameList");

                    TextView txvResult = (TextView) findViewById(R.id.txv_result);
                    txvResult.setText(String.valueOf(displayCoverUrl.length()) + "results for "+ searchTerm + ",\nclick on an image to view stream");

                    for (int i = 0; i < displayCoverUrl.length() && i < 8 ; i++) {
                        coverUrls.clear();
                        streamKeyList.clear();
                        streamNameList.clear();
                        coverUrls.add(displayCoverUrl.getString(i));
                        streamKeyList.add(arrStreamKey.getString(i));
                        streamNameList.add(arrStreamName.getString(i));
//                        System.out.println(displayCoverUrl.getString(i));
                    }
                    search_result = 1;
                    GridView gridview = (GridView) findViewById(R.id.gridview_searchStreams);
                    gridview.setAdapter(new ImageAdapter(context, coverUrls));
                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {
                            Utils.gotoViewSingleStreamActivity(SearchActivity.this, streamKeyList.get(position), streamNameList.get(position), userEmail);
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
