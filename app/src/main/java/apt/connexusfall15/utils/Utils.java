package apt.connexusfall15.utils;

import android.app.Activity;
import android.content.Intent;

import apt.connexusfall15.activity.DisplayImages;
import apt.connexusfall15.activity.ImageUploadActivity;
import apt.connexusfall15.activity.ViewAllStreamsActivity;

/**
 * Created by de-weikung on 10/18/15.
 */
public class Utils {
    public static void gotoViewAllStreamsActivity(Activity activity){
        Intent it = new Intent(activity, ViewAllStreamsActivity.class);
        activity.startActivity(it);
    }

    public static void gotoDisplayImages(Activity activity, String streamKey, String streamName){
        Intent it = new Intent(activity, DisplayImages.class);
        it.putExtra("streamKey", streamKey);
        it.putExtra("streamName", streamName);
        activity.startActivity(it);
    }

    public static void gotoImageUploadActivity(Activity activity, String streamKey, String streamName){
        Intent it = new Intent(activity, ImageUploadActivity.class);
        it.putExtra("streamKey", streamKey);
        it.putExtra("streamName", streamName);
        activity.startActivity(it);
    }
}
