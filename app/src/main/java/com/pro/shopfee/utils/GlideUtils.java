package com.pro.shopfee.utils;

import android.widget.ImageView;

import com.pro.shopfee.R;
import com.bumptech.glide.Glide;

public class GlideUtils {

    public static void loadUrlBanner(String url, ImageView imageView) {
        if (StringUtil.isEmpty(url)) {
            imageView.setImageResource(R.drawable.image_no_available);
            return;
        }
        try {
            Glide.with(imageView.getContext())
                    .load(url)
                    .error(R.drawable.image_no_available)
                    .dontAnimate()
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadUrl(String url, ImageView imageView) {
        if (StringUtil.isEmpty(url)) {
            imageView.setImageResource(R.drawable.image_no_available);
            return;
        }
        try {
            Glide.with(imageView.getContext())
                    .load(url)
                    .error(R.drawable.image_no_available)
                    .dontAnimate()
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}