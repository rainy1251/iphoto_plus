package com.iphoto.plus.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.iphoto.plus.R;
import com.iphoto.plus.util.Contents;
import com.iphoto.plus.util.SPUtils;

public class PxPop extends PopupWindow implements PopupWindow.OnDismissListener,
        View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    @SuppressLint("StaticFieldLeak")
    private static PxPop mPxPop;
    private static Context mContext;
    private static View mParent;
    private PopupWindow mPopupWindow;
    private ViewHolder mViewHolder;

    public static PxPop newInstance(Context context, View parent) {
        mContext = context;
        mParent = parent;
        if (mPxPop == null) {
            mPxPop = new PxPop();
        }
        return mPxPop;
    }

    private PxPop() {
        init();
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.popu_px_layout, null);
        mViewHolder = new ViewHolder(view);
        mPopupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setAnimationStyle(R.style.PopCityStyle);
        mPopupWindow.setOnDismissListener(this);

        setPhotoType();
        mViewHolder.mTvCancel.setOnClickListener(this);
        mViewHolder.rgGroup.setOnCheckedChangeListener(this);

        mPopupWindow.showAtLocation(mParent, Gravity.CENTER | Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void onDismiss() {
        mPopupWindow = null;
        mPxPop = null;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_cancel) {
            mPopupWindow.dismiss();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {

            case R.id.rb_b:
                SPUtils.save(Contents.PHOTO_PX, Contents.Standard);
                if (mCheckPxListener != null) {
                    mCheckPxListener.checkPx("标清▼");
                }
                mPopupWindow.dismiss();
                break;
            case R.id.rb_g:
                SPUtils.save(Contents.PHOTO_PX, Contents.Original);
                if (mCheckPxListener != null) {
                    mCheckPxListener.checkPx("原图▼");
                }
                mPopupWindow.dismiss();
                break;

        }
    }


    private static class ViewHolder {
        public View rootView;
        public TextView mTvCancel;
        public RadioButton rbB;
        public RadioButton rbG;
        public RadioGroup rgGroup;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.rbB = (RadioButton) rootView.findViewById(R.id.rb_b);
            this.rbG = (RadioButton) rootView.findViewById(R.id.rb_g);
            this.rgGroup = (RadioGroup) rootView.findViewById(R.id.rg_group);
            this.mTvCancel = (TextView) rootView.findViewById(R.id.tv_cancel);

        }

    }

    public interface CheckPxListener {
        void checkPx(String pxType);
    }

    public void setCheckPxListener(CheckPxListener mCheckPxListener) {
        this.mCheckPxListener = mCheckPxListener;
    }

    private CheckPxListener mCheckPxListener;

    /**
     * 设置照片尺寸
     */
    private void setPhotoType() {
        String value = SPUtils.getPxString(Contents.PHOTO_PX);
        if (value.equals(Contents.Original)) {
            mViewHolder.rbG.setChecked(true);
            mViewHolder.rbG.setTextColor(mContext.getResources().getColor(R.color.yp_group_text_checked));
        } else {
            mViewHolder.rbB.setChecked(true);
            mViewHolder.rbB.setTextColor(mContext.getResources().getColor(R.color.yp_group_text_checked));
        }
    }
}
