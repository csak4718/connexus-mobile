package apt.connexusfall15.utils;

import android.app.Activity;
import android.content.Intent;

import apt.connexusfall15.activity.ViewAllStreamsActivity;

/**
 * Created by de-weikung on 10/18/15.
 */
public class Utils {
    public static void gotoViewAllStreamsActivity(Activity activity){
        Intent it = new Intent(activity, ViewAllStreamsActivity.class);
        activity.startActivity(it);
    }
}
