package apt.connexusfall15.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import apt.connexusfall15.R;

/**
 * Created by de-weikung on 10/22/15.
 */
public class ImageWithTextAdapter extends BaseAdapter{
    private Context mContext;
    private ArrayList<String> imageURLs;
    private ArrayList<String> textList;

    public ImageWithTextAdapter(Context c, ArrayList<String> imageURLs, ArrayList<String> textList) {
        mContext = c;
        this.imageURLs = imageURLs;
        this.textList = textList;
    }

    public int getCount() {
        return imageURLs.size();
    }

    //    TODO: minor
    public Object getItem(int position) {
        return null;
    }

    //    TODO: minor
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        TextView textView;

        if (convertView == null) {  // if it's not recycled, initialize some attributes
            LayoutInflater a = LayoutInflater.from(mContext);
            convertView = a.inflate(R.layout.image_with_text_adapter, null);
        }
        imageView = (ImageView) convertView.findViewById(R.id.adapter_imageView);
        textView = (TextView) convertView.findViewById(R.id.adapter_textView);

        Picasso.with(mContext).load(imageURLs.get(position)).resize(50, 50).into(imageView);
        if(textList.get(position) != null) {
            textView.setText(textList.get(position));
        }
        return convertView;
    }
}
