package com.iphoto.plus.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iphoto.plus.R;
import com.iphoto.plus.util.Contents;
import com.iphoto.plus.util.SPUtils;

public class ModePop extends PopupWindow implements PopupWindow.OnDismissListener,
        View.OnClickListener {

    @SuppressLint("StaticFieldLeak")
    private static ModePop mModePop;
    private static Context mContext;
    private static View mParent;
    private PopupWindow mPopupWindow;
    private ViewHolder mViewHolder;

    public static ModePop newInstance(Context context, View parent) {
        mContext = context;
        mParent = parent;
        if (mModePop == null) {
            mModePop = new ModePop();
        }
        return mModePop;
    }

    private ModePop() {
        init();
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.popu_mode_layout, null);
        mViewHolder = new ViewHolder(view);
        mPopupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setAnimationStyle(R.style.PopCityStyle);
        mPopupWindow.setOnDismissListener(this);


        setMode(mViewHolder);
        mViewHolder.mTvCancel.setOnClickListener(this);
        mViewHolder.rlDirect.setOnClickListener(this);
        mViewHolder.rlRating.setOnClickListener(this);
        mViewHolder.rlHand.setOnClickListener(this);

        mPopupWindow.showAtLocation(mParent, Gravity.CENTER | Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void onDismiss() {
        mPopupWindow = null;
        mModePop = null;
    }

    private static class ViewHolder {
        public View rootView;
        public ImageView ivDirect;
        public ImageView ivHand;
        public ImageView ivRating;
        public RelativeLayout rlDirect;
        public RelativeLayout rlHand;
        public RelativeLayout rlRating;
        public TextView mTvCancel;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.ivDirect = (ImageView) rootView.findViewById(R.id.iv_direct);
            this.ivHand = (ImageView) rootView.findViewById(R.id.iv_hand);
            this.ivRating = (ImageView) rootView.findViewById(R.id.iv_rating);
            this.mTvCancel = (TextView) rootView.findViewById(R.id.tv_cancel);
            this.rlDirect = (RelativeLayout) rootView.findViewById(R.id.rl_direct);
            this.rlHand = (RelativeLayout) rootView.findViewById(R.id.rl_hand);
            this.rlRating = (RelativeLayout) rootView.findViewById(R.id.rl_rating);


        }

    }

    public interface CheckModeListener {
        void checkMode(String modeType);
    }

    public void setCheckModeListener(CheckModeListener checkModeListener) {
        this.mCheckModeListener = checkModeListener;
    }

    private CheckModeListener mCheckModeListener;

    private void setMode(ViewHolder vh) {
        String mode = SPUtils.getModeString(Contents.UPLOAD_MODE);
        if (mode != null) {
            switch (mode) {
                case Contents.DIRECT_UPLOAD:
                    vh.ivDirect.setImageResource(R.mipmap.check_on);
                    vh.ivHand.setImageResource(R.mipmap.check_off);
                    vh.ivRating.setImageResource(R.mipmap.check_off);
                    break;
                case Contents.HAND_UPLOAD:
                    vh.ivDirect.setImageResource(R.mipmap.check_off);
                    vh.ivHand.setImageResource(R.mipmap.check_on);
                    vh.ivRating.setImageResource(R.mipmap.check_off);
                    break;
                case Contents.RATING_UPLOAD:
                    vh.ivHand.setImageResource(R.mipmap.check_off);
                    vh.ivDirect.setImageResource(R.mipmap.check_off);
                    vh.ivRating.setImageResource(R.mipmap.check_on);

                    break;
            }

        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.rl_direct:
                mViewHolder.ivDirect.setImageResource(R.mipmap.check_on);
                mViewHolder.ivHand.setImageResource(R.mipmap.check_off);
                mViewHolder.ivRating.setImageResource(R.mipmap.check_off);
                SPUtils.save(Contents.UPLOAD_MODE, Contents.DIRECT_UPLOAD);
                if (mCheckModeListener != null) {
                    mCheckModeListener.checkMode("边拍边传");
                }
                mPopupWindow.dismiss();
                break;
            case R.id.rl_hand:

                mViewHolder.ivDirect.setImageResource(R.mipmap.check_off);
                mViewHolder.ivHand.setImageResource(R.mipmap.check_on);
                mViewHolder.ivRating.setImageResource(R.mipmap.check_off);
                SPUtils.save(Contents.UPLOAD_MODE, Contents.HAND_UPLOAD);
                if (mCheckModeListener != null) {
                    mCheckModeListener.checkMode("点选上传");
                }
                mPopupWindow.dismiss();
                break;
            case R.id.rl_rating:

                mViewHolder.ivDirect.setImageResource(R.mipmap.check_off);
                mViewHolder.ivHand.setImageResource(R.mipmap.check_off);
                mViewHolder.ivRating.setImageResource(R.mipmap.check_on);
                SPUtils.save(Contents.UPLOAD_MODE, Contents.RATING_UPLOAD);
                if (mCheckModeListener != null) {
                    mCheckModeListener.checkMode("标记上传");
                }
                mPopupWindow.dismiss();
                break;
            case R.id.tv_cancel:

                mPopupWindow.dismiss();
                break;


        }
    }

}
