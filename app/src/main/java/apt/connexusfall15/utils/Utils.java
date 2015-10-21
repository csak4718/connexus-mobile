package apt.connexusfall15.utils;

import android.app.Activity;
import android.content.Intent;

import apt.connexusfall15.activity.ViewSingleStreamActivity;
import apt.connexusfall15.activity.ImageUploadActivity;
import apt.connexusfall15.activity.MySubscribedStreamsActivity;
import apt.connexusfall15.activity.SearchActivity;
import apt.connexusfall15.activity.SearchNearbyActivity;
import apt.connexusfall15.activity.ViewAllStreamsActivity;

/**
 * Created by de-weikung on 10/18/15.
 */
public class Utils {
    public static void gotoViewAllStreamsActivity(Activity activity){
        Intent it = new Intent(activity, ViewAllStreamsActivity.class);
        activity.startActivity(it);
    }

    public static void gotoViewSingleStreamActivity(Activity activity, String streamKey, String streamName){
        Intent it = new Intent(activity, ViewSingleStreamActivity.class);
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

    public static void gotoSearchActivity(Activity activity, String searchTerm){
        Intent it = new Intent(activity, SearchActivity.class);
        it.putExtra("searchTerm", searchTerm);
        activity.startActivity(it);
    }

    public static void gotoSearchNearbyActivity(Activity activity){
        Intent it = new Intent(activity, SearchNearbyActivity.class);
        activity.startActivity(it);
    }

    public static void gotoMySubscribedStreamsActivity(Activity activity){
        Intent it = new Intent(activity, MySubscribedStreamsActivity.class);
        activity.startActivity(it);
    }
}
