package apt.connexusfall15.activity;

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
import android.widget.Button;
import android.widget.TextView;

import apt.connexusfall15.R;
import apt.connexusfall15.utils.Utils;

public class SearchNearbyActivity extends ActionBarActivity implements LocationListener{
    private static final String TAG  = "Search Nearby Activity";
    private final static int MIN_TIME = 5000;
    private final static float MIN_DIST = 5;
    LocationManager locationManager;
    private double latitude;
    private double longitude;
    private TextView txvAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_nearby);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        txvAlert = (TextView) findViewById(R.id.txv_alert);

        Button viewAllStreamsButton = (Button) findViewById(R.id.button_view_all_streams);
        viewAllStreamsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.gotoViewAllStreamsActivity(SearchNearbyActivity.this);
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();

        // find the best provider
        String best = locationManager.getBestProvider(new Criteria(), true);
        if (best != null){
            locationManager.requestLocationUpdates(best, MIN_TIME, MIN_DIST, this);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.d(TAG, "Latitude: " + String.valueOf(latitude));
            Log.d(TAG, "Longitude: " + String.valueOf(longitude));
        }
        else txvAlert.setVisibility(View.VISIBLE);


    }

    @Override
    protected void onPause(){
        super.onPause();
        locationManager.removeUpdates(this);
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

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        Log.d(TAG, "LatitudeCHANGED: " + String.valueOf(latitude));
        Log.d(TAG, "LongitudeCHANGED: " + String.valueOf(longitude));
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
}
