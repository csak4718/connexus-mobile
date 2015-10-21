package apt.connexusfall15.activity;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import apt.connexusfall15.R;
import apt.connexusfall15.adapter.ImageAdapter;
import apt.connexusfall15.utils.Utils;
import cz.msebera.android.httpclient.Header;


public class ViewAllStreamsActivity extends ActionBarActivity {
    private static final String TAG  = "View All Streams";
    Context context = this;
    private String userEmail_global;
    private Button mySubscribedStreamsButton;
    private Button backToViewAll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_streams);
        final String userEmail = getIntent().getStringExtra("userEmail");

        userEmail_global = userEmail;

        final EditText edt_search = (EditText) findViewById(R.id.edt_search);
        Button searchButton = (Button) findViewById(R.id.btn_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchTerm = edt_search.getText().toString();
                Utils.gotoSearchActivity(ViewAllStreamsActivity.this, searchTerm, userEmail);
            }
        });


        Button searchNearbyButton = (Button) findViewById(R.id.btn_search_nearby);
        searchNearbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.gotoSearchNearbyActivity(ViewAllStreamsActivity.this, userEmail);
            }
        });

        mySubscribedStreamsButton = (Button) findViewById(R.id.btn_my_subscribed_streams);
        mySubscribedStreamsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Utils.gotoMySubscribedStreamsActivity(ViewAllStreamsActivity.this, userEmail);
                postToServer_subscribe(userEmail);
                mySubscribedStreamsButton.setVisibility(View.GONE);
                backToViewAll.setVisibility(View.VISIBLE);
            }
        });
        if (userEmail!=null) mySubscribedStreamsButton.setVisibility(View.VISIBLE);
        else mySubscribedStreamsButton.setVisibility(View.GONE);

        backToViewAll = (Button) findViewById(R.id.btn_back_to_view_all);
        backToViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postToServer_veiwAll(userEmail);
                backToViewAll.setVisibility(View.GONE);
                mySubscribedStreamsButton.setVisibility(View.VISIBLE);
            }
        });


        postToServer_veiwAll(userEmail);
    }

    @Override
    protected void onResume(){
        super.onResume();
        backToViewAll.setVisibility(View.GONE);
        mySubscribedStreamsButton.setVisibility(View.VISIBLE);
        postToServer_veiwAll(userEmail_global);
    }

    private void postToServer_veiwAll(final String userEmail){
        // final String request_url = "http://connexus-fall15.appspot.com/View_all_streams_mobile";
        // final String request_url = "http://localhost:8080/View_all_streams_mobile";
        final String request_url = "http://connexus-fall15.appspot.com/View_all_streams_mobile";
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
                    GridView gridview = (GridView) findViewById(R.id.gridview_viewAllStreams);
                    gridview.setAdapter(new ImageAdapter(context, coverUrls));
                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {
                            Utils.gotoViewSingleStreamActivity(ViewAllStreamsActivity.this, streamKeyList.get(position), streamNameList.get(position), userEmail);
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

    private void postToServer_subscribe(String userEmail) {
        final String request_url = "http://connexus-fall15.appspot.com/MySubscribedImages_mobile?userEmail="+userEmail;
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(request_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                final ArrayList<String> imageURLs = new ArrayList<String>();
//                final ArrayList<String> imageCaps = new ArrayList<String>();
                try {
                    JSONObject jObject = new JSONObject(new String(responseBody));
                    JSONArray displayImages = jObject.getJSONArray("displayImages");
//                    JSONArray displayCaption = jObject.getJSONArray("imageCaptionList");

                    for (int i = 0; i < displayImages.length(); i++) {
                        imageURLs.add(displayImages.getString(i));
//                        imageCaps.add(displayCaption.getString(i));
                        System.out.println(displayImages.getString(i));
                    }
                    GridView gridview = (GridView) findViewById(R.id.gridview_viewAllStreams);
                    gridview.setAdapter(new ImageAdapter(context, imageURLs));
                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {

//                            Toast.makeText(context, imageCaps.get(position), Toast.LENGTH_SHORT).show();

                            Dialog imageDialog = new Dialog(context);
                            imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            imageDialog.setContentView(R.layout.thumbnail);
                            ImageView image = (ImageView) imageDialog.findViewById(R.id.thumbnail_IMAGEVIEW);

                            Picasso.with(context).load(imageURLs.get(position)).resize(300, 300).centerCrop().into(image);

                            imageDialog.show();
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
        getMenuInflater().inflate(R.menu.menu_view_all_streams, menu);
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
