package com.iphoto.plus.view;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iphoto.plus.R;
import com.iphoto.plus.adapter.GroupSetAdapter;
import com.iphoto.plus.bean.CategoryResultBean;
import com.iphoto.plus.util.DimenUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class GroupSetPop extends PopupWindow implements PopupWindow.OnDismissListener, OnItemClickListener
        , View.OnClickListener {
    private static GroupSetPop mGroupSetPop;
    private static Context mContext;
    private static View mParent;
    private static List<CategoryResultBean> mDataList;
    private static List<CategoryResultBean> mCheckedList = new ArrayList<>();
    private PopupWindow mPopupWindow;
    private GroupSetPop.ViewHolder mViewHolder;
    private GroupSetAdapter mAdapter;
    private int checkSize;

    public static GroupSetPop newInstance(Context context, View parent, List<CategoryResultBean> dataList) {
        mContext = context;
        mParent = parent;
        mDataList = dataList;
        if (mGroupSetPop == null) {
            mGroupSetPop = new GroupSetPop();
        }
        return mGroupSetPop;
    }

    public GroupSetPop() {

        init();
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.popu_group_set_layout, null);
        mViewHolder = new GroupSetPop.ViewHolder(view);
        mPopupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setAnimationStyle(R.style.PopCityStyle);
        mPopupWindow.setOnDismissListener(this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(mContext, 2);
        mViewHolder.mGroups.setLayoutManager(layoutManager);
        mAdapter = new GroupSetAdapter(R.layout.item_group, mDataList);
        mViewHolder.mGroups.addItemDecoration(new SpaceItemDecoration(0, (int) DimenUtil.dpToPx(mContext, 10)));
        mCheckedList = AppDB.getInstance().categoryDao().findTrueValues(true);
        checkSize = mCheckedList.size();
        mViewHolder.mTvNum.setText("已选择" +checkSize + "/" + mDataList.size());
        mAdapter.setOnItemClickListener(this);
        mViewHolder.mTvSave.setOnClickListener(this);
        mViewHolder.mTvAll.setOnClickListener(this);
        mViewHolder.mIvBack.setOnClickListener(this);
        mViewHolder.mGroups.setAdapter(mAdapter);
        setBackgroundAlpha(0.3F);
        mPopupWindow.showAtLocation(mParent, Gravity.CENTER, 0, 0);
    }


    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha 屏幕透明度0.0-1.0 1表示完全不透明
     */
    public void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow().getAttributes();
        lp.alpha = bgAlpha;
        ((Activity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        ((Activity) mContext).getWindow().setAttributes(lp);
    }

    @Override
    public void onDismiss() {
        setBackgroundAlpha(1.0F);
        mPopupWindow = null;
        mGroupSetPop = null;
    }

    @Override
    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
        TextView tvTag = view.findViewById(R.id.rb_button);
        if (mDataList.get(position).getCheck()) {
            mDataList.get(position).setCheck(false);
            AppDB.getInstance().categoryDao().update(mDataList.get(position));
            tvTag.setBackgroundResource(R.drawable.select_group_item_bg);
            tvTag.setTextColor(view.getResources().getColor(R.color.select_group_text_color));
           // mCheckedList.remove(mDataList.get(position));
            checkSize--;
        } else {
            view.findViewById(R.id.rb_button).setBackgroundResource(R.drawable.shape_item_group_checked);
            tvTag.setTextColor(view.getResources().getColor(R.color.yp_group_text_checked));
            mDataList.get(position).setCheck(true);
            AppDB.getInstance().categoryDao().update(mDataList.get(position));
            checkSize++;
          //  mCheckedList.add(mDataList.get(position));
        }
        mViewHolder.mTvNum.setText("已选择" + checkSize + "/" + mDataList.size());
        //  mAdapter.setCheckedData(mCheckedList);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                mPopupWindow.dismiss();
                break;
            case R.id.tv_all:

                break;
            case R.id.tv_save:
//                for (CategoryResultBean bean : mCheckedList) {
//                    CategoryResultBean byName = AppDB.getInstance().categoryDao().findByName(bean.getName());
//                    byName.setCheck(true);
//                    AppDB.getInstance().categoryDao().update(byName);
//                }
                mPopupWindow.dismiss();
                break;

        }
    }

    private static class ViewHolder {
        public View rootView;
        public RecyclerView mGroups;
        public TextView mTvSave;
        public TextView mTvAll;
        public TextView mTvNum;
        public ImageView mIvBack;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.mGroups = (RecyclerView) rootView.findViewById(R.id.rv_view);
            this.mTvSave = (TextView) rootView.findViewById(R.id.tv_save);
            this.mIvBack = (ImageView) rootView.findViewById(R.id.iv_back);
            this.mTvAll = (TextView) rootView.findViewById(R.id.tv_all);
            this.mTvNum = (TextView) rootView.findViewById(R.id.tv_num);
        }

    }
}
