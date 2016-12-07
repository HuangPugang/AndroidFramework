package org.hpdroid.util;

import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by paul on 16/10/21.
 */

public class GlideHelper {

    public static void display(String url, ImageView imageView) {
        Glide.with(CtxHelper.getApp())
                .load(url)
                .crossFade()
                .into(imageView);
    }

    public static void display(String url, ImageView imageView, int defaultHolder) {
        Glide.with(CtxHelper.getApp())
                .load(url)
                .placeholder(defaultHolder)
                .crossFade()
                .into(imageView);
    }

    public static void display(int urlDrawableRes,ImageView imageView){
        Glide.with(CtxHelper.getApp()).load(urlDrawableRes).into(imageView);
    }

}
