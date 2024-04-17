package com.iphoto.plus.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;


import com.iphoto.plus.R;
import com.iphoto.plus.bean.AlbumListBean;
import com.iphoto.plus.util.Contents;
import com.iphoto.plus.util.DateFormatUtils;
import com.iphoto.plus.util.GlideUtils;
import com.iphoto.plus.view.QrCodeDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.king.zxing.util.CodeUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ActivityListAdapter extends BaseQuickAdapter<AlbumListBean.DataBean.ListBean, BaseViewHolder> {

    private final Context mContext;

    public ActivityListAdapter(Context context, int layoutResId, @Nullable List<AlbumListBean.DataBean.ListBean> data) {
        super(layoutResId, data);
        mContext = context;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder vh, AlbumListBean.DataBean.ListBean data) {

        String startAt = DateFormatUtils.formatDate(data.getStartTime());
        String endAt = DateFormatUtils.formatDate(data.getEndTime());
        vh.setText(R.id.tv_title, data.getName());
        vh.setText(R.id.tv_date,String.format("%s\n%S" ,startAt,endAt));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void createQrCode(String id) {
        Bitmap qrCode = CodeUtils.createQRCode(Contents.ACTIVITY_URL + id, 600);
        QrCodeDialog dialog = new QrCodeDialog(mContext, qrCode);
        dialog.show();
    }
}
