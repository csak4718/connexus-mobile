package apt.connexusfall15.utils;

import android.app.Activity;
import android.content.Intent;


import apt.connexusfall15.activity.CameraActivity;
import apt.connexusfall15.activity.ViewSingleStreamActivity;
import apt.connexusfall15.activity.ImageUploadActivity;

import apt.connexusfall15.activity.SearchActivity;
import apt.connexusfall15.activity.SearchNearbyActivity;
import apt.connexusfall15.activity.ViewAllStreamsActivity;

/**
 * Created by de-weikung on 10/18/15.
 */
public class Utils {
    public static void gotoViewAllStreamsActivity(Activity activity, String email){
        Intent it = new Intent(activity, ViewAllStreamsActivity.class);
        it.putExtra("userEmail", email);
        activity.startActivity(it);
    }

    public static void gotoViewSingleStreamActivity(Activity activity, String streamKey, String streamName, String userEmail){
        Intent it = new Intent(activity, ViewSingleStreamActivity.class);
        it.putExtra("streamKey", streamKey);
        it.putExtra("streamName", streamName);
        it.putExtra("userEmail", userEmail);
        activity.startActivity(it);
    }

    public static void gotoImageUploadActivity(Activity activity, String streamKey, String streamName, String userEmail){
        Intent it = new Intent(activity, ImageUploadActivity.class);
        it.putExtra("streamKey", streamKey);
        it.putExtra("streamName", streamName);
        it.putExtra("userEmail", userEmail);
        activity.startActivity(it);
        activity.finish();
    }

    public static void gotoSearchActivity(Activity activity, String searchTerm, String userEmail){
        Intent it = new Intent(activity, SearchActivity.class);
        it.putExtra("searchTerm", searchTerm);
        it.putExtra("userEmail", userEmail);
        activity.startActivity(it);
    }

    public static void gotoSearchNearbyActivity(Activity activity, String userEmail){
        Intent it = new Intent(activity, SearchNearbyActivity.class);
        it.putExtra("userEmail", userEmail);
        activity.startActivity(it);
    }

    public static void gotoCameraActivity(Activity activity, int request_code, String userEmail){
        Intent it = new Intent(activity, CameraActivity.class);
        it.putExtra("userEmail", userEmail);
        activity.startActivityForResult(it, request_code);
    }
}
