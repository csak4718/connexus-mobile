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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import apt.connexusfall15.R;
import apt.connexusfall15.adapter.ImageAdapter;
import apt.connexusfall15.utils.Utils;
import cz.msebera.android.httpclient.Header;




public class ViewSingleStreamActivity extends ActionBarActivity {
    Context context = this;
    private static final String TAG  = "ViewSingleStreamActivity";
    int pictures_viewed = 0;
    ArrayList<String> imageURLs = new ArrayList<String>();
    JSONArray displayImages = new JSONArray();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_single_stream);
        final String userEmail = getIntent().getStringExtra("userEmail");
        final String streamKey = getIntent().getStringExtra("streamKey");
        final String streamName = getIntent().getStringExtra("streamName");

        TextView txv_streamName = (TextView) findViewById(R.id.stream_name);
        txv_streamName.setText("View A Stream: "+streamName);
        Button viewmorePicture = (Button) findViewById(R.id.btn_more_pic_single_stream);
        Button viewAllStreams = (Button) findViewById(R.id.btn_view_all_streams);
        viewAllStreams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Utils.gotoViewAllStreamsActivity(ViewSingleStreamActivity.this, userEmail);
                finish();
            }
        });
        viewmorePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pictures_viewed = pictures_viewed+16;
                try{
                    if( pictures_viewed <= displayImages.length()) {
                        imageURLs.clear();
                        for (int i = pictures_viewed; i < displayImages.length() && i < pictures_viewed+16; i++) {
                        imageURLs.add(displayImages.getString(i));
//                        imageCaps.add(displayCaption.getString(i));
                        System.out.println(displayImages.getString(i));
                    }
                    }
                }
                catch(JSONException j) {
                    System.out.println("JSON Error on More");
                }

                GridView gridview = (GridView) findViewById(R.id.gridview);
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
            }
        });
        final Button imageUpload = (Button) findViewById(R.id.btn_to_upload_activity);
        imageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.gotoImageUploadActivity(ViewSingleStreamActivity.this, streamKey, streamName);
            }
        });



        // final String request_url = "http://localhost:8080/View_single_mobile";
        // final String request_url = "http://connexus-fall15.appspot.com/View_single_mobile";
        final String request_url = "http://connexus-fall15.appspot.com/View_single_mobile?streamKey="+streamKey;
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(request_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
//                final ArrayList<String> imageURLs = new ArrayList<String>();
//                final ArrayList<String> imageCaps = new ArrayList<String>();
                try {
                    JSONObject jObject = new JSONObject(new String(response));
                    displayImages = jObject.getJSONArray("displayImages");
                    String ownerEmail = jObject.getString("ownerEmail");
//                    Log.d(TAG, "UserEmail: "+userEmail);
                    if (userEmail != null){
                        if (userEmail.equals(ownerEmail)) imageUpload.setVisibility(View.VISIBLE);
                        else imageUpload.setVisibility(View.GONE);
                    }
                    else imageUpload.setVisibility(View.GONE);

//                    JSONArray displayCaption = jObject.getJSONArray("imageCaptionList");
                    for (int i = 0; i < displayImages.length() && i < 16; i++) {
                        imageURLs.add(displayImages.getString(i));
//                        imageCaps.add(displayCaption.getString(i));
                        System.out.println(displayImages.getString(i));
                    }
                    GridView gridview = (GridView) findViewById(R.id.gridview);
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
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e(TAG, "There was a problem in retrieving the url : " + e.toString());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_single_stream, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
