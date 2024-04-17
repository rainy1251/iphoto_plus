package com.iphoto.plus.util;

import android.content.Context;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.iphoto.plus.R;


public class GlideUtils {

    public static void show(Context context, ImageView imageView, Object url) {
        Glide.with(context).load(url)
                .apply(new RequestOptions())
                .into(imageView);
    }

    public static void showSta(Context context, ImageView imageView, Object url) {
        Glide.with(context).load(url+"&type=R80hS")
                .placeholder(R.mipmap.placeholder)
                .dontAnimate()
                .into(imageView);
    }

    public static void showHead(Context context, ImageView imageView, Object url) {
        Glide.with(context).load(url)
                .apply(new RequestOptions().error(R.mipmap.placeholder).circleCrop())
                .into(imageView);
    }
}