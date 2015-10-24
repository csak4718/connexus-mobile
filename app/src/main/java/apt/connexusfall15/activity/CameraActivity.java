package apt.connexusfall15.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;

import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import apt.connexusfall15.R;
import apt.connexusfall15.utils.CameraPreview;
import apt.connexusfall15.utils.Utils;


public class CameraActivity extends ActionBarActivity {
    private final static String TAG = "CameraActivity";
    Context context = this;
    private Camera mCamera;
    private FrameLayout preview;
    private Button confirmButton;
    private Button cancelButton;

    private Button captureButton;
    private CameraPreview mPreview;
    private String userEmail;
//    private Camera.ShutterCallback shutter = new Camera.ShutterCallback() {
//        @Override
//        public void onShutter() {
//            mCamera.stopPreview();
//        }
//    };
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(final byte[] data, final Camera camera) {
            File pictureFileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

            if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
                Toast.makeText(context, "Can't create directory to save image.",
                        Toast.LENGTH_LONG).show();
                return;

            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
            String date = dateFormat.format(new Date());
            String photoFile = "Picture_" + date + ".jpg";

            String filename = pictureFileDir.getPath() + File.separator + photoFile;

            File pictureFile = new File(filename);

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
//                Toast.makeText(context, "New Image saved:" + photoFile,
//                        Toast.LENGTH_LONG).show();
            } catch (Exception error) {
                Toast.makeText(context, "Image could not be saved.",
                        Toast.LENGTH_LONG).show();
            }
            Intent returnIntent = new Intent();
            returnIntent.putExtra("path", filename);
            setResult(RESULT_OK, returnIntent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        userEmail = getIntent().getStringExtra("userEmail");

        Button viewAllButton = (Button) findViewById(R.id.btn_viewAll);
        viewAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.gotoViewAllStreamsActivity(CameraActivity.this, userEmail);
                finish();
                releaseCamera();
            }
        });

        confirmButton = (Button) findViewById(R.id.button_confirm);
        cancelButton = (Button) findViewById(R.id.button_cancel);
        confirmButton.setEnabled(false);
        cancelButton.setEnabled(false);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                releaseCamera();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.startPreview();
                confirmButton.setEnabled(false);
                cancelButton.setEnabled(false);
                captureButton.setEnabled(true);
            }
        });

        boolean hasCamera = checkCameraHardware(context);
        if (hasCamera){
            // Create an instance of Camera
            mCamera = getCameraInstance();
            if (mCamera != null){
                // Create our Preview view and set it as the content of our activity.
                mPreview = new CameraPreview(context, mCamera);
                preview = (FrameLayout) findViewById(R.id.camera_preview);
                preview.addView(mPreview);

                // Add a listener to the Capture button
                captureButton = (Button) findViewById(R.id.button_capture);
                captureButton.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // get an image from the camera
                                mCamera.takePicture(null, null, mPicture);
                                confirmButton.setEnabled(true);
                                cancelButton.setEnabled(true);
                                captureButton.setEnabled(false);

                            }
                        }
                );
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();              // release the camera immediately on pause event
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_camera, menu);
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
