package ww.smartexpress.driver.utils;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;

import ww.smartexpress.driver.BuildConfig;
import ww.smartexpress.driver.R;

public final class BindingUtils {
    @BindingAdapter("android:src")
    public static void setImageUrl(ImageView view, String url) {
        if (url == null){
            view.setImageResource(R.drawable.ic_xml_grey600_48dp);
            return;
        }
        if(url.contains("https:")){
            Glide.with(view.getContext())
                    .load(url)
                    .into(view);
            return;
        }
        Glide.with(view.getContext())
                .load(BuildConfig.MEDIA_URL+ "/v1/file/download" + url)
                .error(R.drawable.ic_xml_grey600_48dp)
                .placeholder(R.drawable.ic_xml_grey600_48dp)
                .into(view);
    }

    @BindingAdapter("android:src")
    public static void setImageUrl(ImageView view, int url) {
        view.setImageResource(url);
    }
}
