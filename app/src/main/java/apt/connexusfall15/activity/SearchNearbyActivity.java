package apt.connexusfall15.activity;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

import apt.connexusfall15.R;
import apt.connexusfall15.adapter.ImageAdapter;
import apt.connexusfall15.adapter.ImageWithTextAdapter;
import apt.connexusfall15.utils.Utils;
import cz.msebera.android.httpclient.Header;

public class SearchNearbyActivity extends ActionBarActivity implements LocationListener{
    private static final String TAG  = "Search Nearby Activity";
    private final static int MIN_TIME = 5000;
    private final static float MIN_DIST = 5;
    LocationManager locationManager;
    private double latitude;
    private double longitude;
    private TextView txvAlert;
    Context context = this;
    int pictures_viewed = 0;
    ArrayList<String> imgUrls = new ArrayList<String>();
    ArrayList<String> distanceList = new ArrayList<>();
    JSONArray displayImgUrl = new JSONArray();
    JSONArray arrDistance = new JSONArray();
    JSONArray arrStreamKey = new JSONArray();
    JSONArray arrStreamName = new JSONArray();
    ArrayList<String> streamKeyList = new ArrayList<>();
    ArrayList<String> streamNameList = new ArrayList<String>();

    int first_set_viewed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_nearby);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        txvAlert = (TextView) findViewById(R.id.txv_alert);

    }

    @Override
    protected void onResume(){
        super.onResume();
        final String userEmail = getIntent().getStringExtra("userEmail");
        Button viewAllStreamsButton = (Button) findViewById(R.id.button_view_all_streams);
        Button viewmorePicture = (Button) findViewById(R.id.btn_more_pic_nearby);
        viewAllStreamsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        viewmorePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pictures_viewed = pictures_viewed+16;
                try{
                    if( pictures_viewed <= displayImgUrl.length()) {
                        imgUrls.clear();
                        distanceList.clear();
                        streamKeyList.clear();
                        streamNameList.clear();
                        for (int i = pictures_viewed; i < displayImgUrl.length() && i < pictures_viewed+16; i++) {
                            imgUrls.add(displayImgUrl.getString(i));
                            distanceList.add(arrDistance.getString(i));
                            streamKeyList.add(arrStreamKey.getString(i));
                            streamNameList.add(arrStreamName.getString(i));
                        }
                    }
                }catch(JSONException j){
                    System.out.println("JSON Error");
                }
                GridView gridview = (GridView) findViewById(R.id.gridview_search_nearby);
                gridview.setAdapter(new ImageWithTextAdapter(context, imgUrls, distanceList));
                gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View v,
                                            int position, long id) {
                        Utils.gotoViewSingleStreamActivity(SearchNearbyActivity.this, streamKeyList.get(position), streamNameList.get(position), userEmail);
                    }
                });
            }
        });

        // find the best provider
        String best = locationManager.getBestProvider(new Criteria(), true);
        if (best != null){
            locationManager.requestLocationUpdates(best, MIN_TIME, MIN_DIST, this);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            latitude = location.getLatitude();
            longitude = location.getLongitude();
//            Log.d(TAG, "Latitude: " + String.valueOf(latitude));
//            Log.d(TAG, "Longitude: " + String.valueOf(longitude));
            postToServer(userEmail);

        }
        else txvAlert.setVisibility(View.VISIBLE);


    }

    private void postToServer(final String userEmail){
        String url = "http://connexus-fall15.appspot.com/Search_Nearby_mobile?latitude="+String.valueOf(latitude)+"&longitude="+String.valueOf(longitude);
//        Log.d(TAG, String.valueOf(latitude));
//        Log.d(TAG, String.valueOf(longitude));

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
//                final ArrayList<String> imgUrls = new ArrayList<String>();
                try {
                    JSONObject jObject = new JSONObject(new String(response));
                    displayImgUrl = jObject.getJSONArray("displayImages");
                    arrDistance = jObject.getJSONArray("distanceList");
                    arrStreamKey = jObject.getJSONArray("streamKeyList");
                    arrStreamName = jObject.getJSONArray("streamNameList");

                    if (pictures_viewed == 0 && first_set_viewed == 0) {
                        first_set_viewed = 1;
                        for (int i = 0; i < displayImgUrl.length() && i < 16; i++) {
                            imgUrls.add(displayImgUrl.getString(i));
                            distanceList.add(arrDistance.getString(i));
                            streamKeyList.add(arrStreamKey.getString(i));
                            streamNameList.add(arrStreamName.getString(i));
                        }
                    }
                    GridView gridview = (GridView) findViewById(R.id.gridview_search_nearby);
                    gridview.setAdapter(new ImageWithTextAdapter(context, imgUrls, distanceList));
                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {
                            Utils.gotoViewSingleStreamActivity(SearchNearbyActivity.this, streamKeyList.get(position), streamNameList.get(position), userEmail);
                        }
                    });
                } catch (JSONException j) {
                    System.out.println("JSON Error Post to Server");
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e("postToServer", e.toString());
            }
        });
    }

    @Override
    protected void onPause(){
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

//        Log.d(TAG, "LatitudeCHANGED: " + String.valueOf(latitude));
//        Log.d(TAG, "LongitudeCHANGED: " + String.valueOf(longitude));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_nearby, menu);
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
