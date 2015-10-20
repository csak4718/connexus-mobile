package apt.connexusfall15.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import apt.connexusfall15.R;
import cz.msebera.android.httpclient.Header;

public class ImageUploadActivity extends ActionBarActivity {
    private static final String TAG  = "ImageUploadActivity";
    private static final int PICK_IMAGE = 1;
    private static final int CAMERA_REQUEST = 2;
    Context context = this;
    private String streamKey;
    private EditText text;
    private Uri imgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);
        streamKey = getIntent().getStringExtra("streamKey");
        String streamName = getIntent().getStringExtra("streamName");


        text = (EditText) findViewById(R.id.upload_message);

        TextView txv_streamName = (TextView) findViewById(R.id.txv_stream_name);
        txv_streamName.setText("Upload to: " + streamName);

        // Choose image from library
        Button chooseFromLibraryButton = (Button) findViewById(R.id.choose_from_library);
        chooseFromLibraryButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // To do this, go to AndroidManifest.xml to add permission
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        // Start the Intent
                        startActivityForResult(galleryIntent, PICK_IMAGE);
                    }
                }
        );

        Button useCameraButton = (Button) findViewById(R.id.btn_use_camera);
        useCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
                String fname = "p" + System.currentTimeMillis() + ".jpg";
                imgUri = Uri.parse("file://" + dir + "/" + fname);

                Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                i.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);


//                File photo;
//                try
//                {
                    // place where to store camera taken picture
//                    photo = createTemporaryFile("picture", ".jpg");
//                    mImageUri = Uri.fromFile(photo);
//                    i.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                    startActivityForResult(i, CAMERA_REQUEST);
//                }
//                catch(Exception e)
//                {
//                    Log.d(TAG, "Can't create file to take picture!");
//                    Toast.makeText(ImageUploadActivity.this, "Please check SD card! Image shot is impossible!", Toast.LENGTH_SHORT).show();
//                }
            }
        });
    }

    private File createTemporaryFile(String part, String ext) throws Exception {
        File tempDir= getExternalCacheDir();
        return File.createTempFile(part, ext, tempDir);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && data != null && data.getData() != null && resultCode == Activity.RESULT_OK) {
            // User had pick an image.
            Uri selectedImage = data.getData();

            String[] filePathColumn = {MediaStore.Images.ImageColumns.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            // Link to the image
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imageFilePath = cursor.getString(columnIndex);
            cursor.close();

            // Bitmap image created and show thumbnail
            ImageView imgView = (ImageView) findViewById(R.id.thumbnail);
            final Bitmap bitmapImage = BitmapFactory.decodeFile(imageFilePath);
            imgView.setImageBitmap(bitmapImage);

            // Enable the upload button once image has been uploaded
            Button uploadButton = (Button) findViewById(R.id.upload_to_server);
            uploadButton.setClickable(true);
            uploadButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Get photo caption
                            String photoCaption = text.getText().toString();

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                            byte[] b = baos.toByteArray();
//                            byte[] encodedImage = Base64.encode(b, Base64.DEFAULT); // ?? useless?
//                            String encodedImageStr = encodedImage.toString(); // ?? useless?

                            postToServer(b, photoCaption); // argument must be b
                        }
                    }
            );
        }
        else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
//            getContentResolver().notifyChange(mImageUri, null);


//            Bundle extras = data.getExtras();
//            final Bitmap bitmapImage = (Bitmap) extras.get("data");
//            if (imgUri==null) Log.d("TAG", "imgUri = NULL");
            final Bitmap bitmapImage = BitmapFactory.decodeFile(imgUri.getPath());



            ImageView imgView = (ImageView) findViewById(R.id.thumbnail);
            imgView.setImageBitmap(bitmapImage);

//            Picasso.with(this)
//                    .load(mImageUri)
//                    .resize(640, 480)
//                    .centerInside()
//                    .into(imgView);
//            final Bitmap bitmapImage = ((BitmapDrawable) imgView.getDrawable()).getBitmap();
            Button uploadButton = (Button) findViewById(R.id.upload_to_server);
            uploadButton.setClickable(true);
            uploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get photo caption
                    String photoCaption = text.getText().toString();

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    byte[] b = baos.toByteArray();
                    postToServer(b, photoCaption);
                }
            });
        }
    }

//    private void getUploadURL(final byte[] encodedImage, final String photoCaption){
//        AsyncHttpClient httpClient = new AsyncHttpClient();
//        String request_url="http://connexus-fall15.appspot.com/getUploadURL";
//
//        httpClient.get(request_url, new AsyncHttpResponseHandler() {
//            String upload_url;
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
//                try {
//                    JSONObject jObject = new JSONObject(new String(response));
//                    upload_url = jObject.getString("upload_url");
//                    postToServer(encodedImage, photoCaption, upload_url);
//
//                }
//                catch(JSONException j){
//                    System.out.println("JSON Error");
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
//                Log.e("Get_serving_url", "There was a problem in retrieving the url : " + e.toString());
//            }
//        });
//    }

    private void postToServer(byte[] encodedImage, String photoCaption){
//        String url = "http://connexus-fall15.appspot.com/Add_Image_mobile";
        String url = "http://connexus-fall15.appspot.com/Add_Image_mobile?streamKey="+streamKey;
        RequestParams params = new RequestParams();
        params.put("file", new ByteArrayInputStream(encodedImage));
        params.put("photoCaption", photoCaption);
//        params.put("streamKey", streamKey);
        AsyncHttpClient client = new AsyncHttpClient();

        Log.d(TAG, url);

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                Log.w("async", "success!!!!");
                Toast.makeText(context, "Upload Successful", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e("Posting_to_blob","There was a problem in retrieving the url : " + e.toString());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_upload, menu);
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
