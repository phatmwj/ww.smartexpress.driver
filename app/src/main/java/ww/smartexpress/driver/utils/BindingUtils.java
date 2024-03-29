package ww.smartexpress.driver.utils;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;

import ww.smartexpress.driver.BuildConfig;
import ww.smartexpress.driver.R;

public final class BindingUtils {
    @BindingAdapter("android:src")
    public static void setImageUrl(ImageView view, String url) {
        Glide.with(view.getContext())
                .load(BuildConfig.MEDIA_URL+ "/v1/file/download" + url)
                .error(R.drawable.user_avatar)
                .placeholder(R.drawable.user_avatar)
                .into(view);
    }

    @BindingAdapter("android:src")
    public static void setImageUrl(ImageView view, int url) {
        view.setImageResource(url);
    }
}
