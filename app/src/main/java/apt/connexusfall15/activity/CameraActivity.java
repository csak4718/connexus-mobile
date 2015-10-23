package apt.connexusfall15.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;

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


import java.io.IOException;

import apt.connexusfall15.R;
import apt.connexusfall15.utils.CameraPreview;


public class CameraActivity extends ActionBarActivity {
    private final static String TAG = "CameraActivity";
    Context context = this;
    private Camera mCamera;
    private FrameLayout preview;
    private Button confirmButton;
    private Button cancelButton;

    private Button captureButton;
    private CameraPreview mPreview;
//    private Camera.ShutterCallback shutter = new Camera.ShutterCallback() {
//        @Override
//        public void onShutter() {
//            mCamera.stopPreview();
//        }
//    };
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(final byte[] data, final Camera camera) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("byteArr", data);
            setResult(RESULT_OK, returnIntent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        confirmButton = (Button) findViewById(R.id.button_confirm);
        cancelButton = (Button) findViewById(R.id.button_cancel);
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
