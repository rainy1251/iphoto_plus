package com.iphoto.plus.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iphoto.plus.R;
import com.iphoto.plus.adapter.GroupAdapter;
import com.iphoto.plus.bean.CategoryResultBean;
import com.iphoto.plus.util.Contents;
import com.iphoto.plus.util.DimenUtil;
import com.iphoto.plus.util.HideUtil;
import com.iphoto.plus.util.MyToast;
import com.iphoto.plus.util.SPUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class GroupPop extends PopupWindow implements PopupWindow.OnDismissListener, OnItemClickListener,
        View.OnClickListener, TextView.OnEditorActionListener {

    @SuppressLint("StaticFieldLeak")
    private static GroupPop mGroupPop;
    private static Context mContext;
    private static View mParent;
    private static List<CategoryResultBean> mDataList;
    private PopupWindow mPopupWindow;
    private ViewHolder mViewHolder;
    private GroupAdapter mAdapter;

    public static GroupPop newInstance(Context context, View parent, List<CategoryResultBean> dataList) {
        mContext = context;
        mParent = parent;
        mDataList = dataList;
        CategoryResultBean noneBean = new CategoryResultBean("0", "0", 0, "无标签",false);
        mDataList.add(0, noneBean);
        if (mGroupPop == null) {
            mGroupPop = new GroupPop();
        }
        return mGroupPop;
    }

    private GroupPop() {
        init();
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.popu_group_layout, null);
        mViewHolder = new ViewHolder(view);
        mPopupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setAnimationStyle(R.style.PopCityStyle);
        mPopupWindow.setOnDismissListener(this);
        String checkId = SPUtils.getString(Contents.categoryId);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(mContext, 2);
        mViewHolder.mGroups.setLayoutManager(layoutManager);
        mAdapter = new GroupAdapter(R.layout.item_group, mDataList);
        mViewHolder.mGroups.addItemDecoration(new SpaceItemDecoration(0, (int) DimenUtil.dpToPx(mContext, 10)));
        mAdapter.setOnItemClickListener(this);
        mAdapter.setGroupId(checkId);
        mViewHolder.mTvCancel.setOnClickListener(this);
        mViewHolder.mIvSearch.setOnClickListener(this);
        mViewHolder.mIvSet.setOnClickListener(this);
        mViewHolder.mEtWord.setOnEditorActionListener(this);
        mViewHolder.mGroups.setAdapter(mAdapter);
        setBackgroundAlpha(0.3F);
        mPopupWindow.showAtLocation(mParent, Gravity.CENTER | Gravity.BOTTOM, 0, 0);
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
        mGroupPop = null;
    }

    @Override
    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
        if (mCheckGroupListener != null) {
            mCheckGroupListener.checkGroup(mDataList.get(position));
        }
        mPopupWindow.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                mPopupWindow.dismiss();
                break;
            case R.id.iv_set:
                List<CategoryResultBean> allData = AppDB.getInstance().categoryDao().getAll();
                GroupSetPop.newInstance(mContext,mParent,allData);
                mPopupWindow.dismiss();
                break;
            case R.id.iv_search:
                HideUtil.hideSoftKeyboard((Activity) mContext);
                String word = mViewHolder.mEtWord.getText().toString().trim();
                List<CategoryResultBean> all = AppDB.getInstance().categoryDao().getAll();
                List<CategoryResultBean> list = new ArrayList<>();
                for (CategoryResultBean bean : all) {
                    if (bean.getName().contains(word)) {
                        list.add(bean);
                    }
                }
                if (list.size() == 0) {
                    MyToast.show("无此标签，请重新输入");
                    mAdapter.clean();
                    mAdapter.setEmptyView(R.layout.category_empty_view);
                    mAdapter.notifyDataSetChanged();
                } else {
                    mAdapter.clean();
                    mAdapter.addData(list);
                }
                mViewHolder.mEtWord.setText("");
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            //关闭软键盘
            HideUtil.hideSoftKeyboard((Activity) mContext);
            String word = mViewHolder.mEtWord.getText().toString().trim();
            List<CategoryResultBean> all = AppDB.getInstance().categoryDao().getAll();
            List<CategoryResultBean> list = new ArrayList<>();
            for (CategoryResultBean bean : all) {
                if (bean.getName().contains(word) || bean.getName().contains(word.toUpperCase())) {
                    list.add(bean);
                }
            }
            if (list.size() == 0) {
                MyToast.show("无此分组，请重新输入");
                mAdapter.clean();
                mAdapter.setEmptyView(R.layout.category_empty_view);
                mAdapter.notifyDataSetChanged();
            } else {
                mAdapter.clean();
                mAdapter.addData(list);
            }
            mViewHolder.mEtWord.setText("");
            return true;
        }
        return false;
    }


    private static class ViewHolder {
        public View rootView;
        public RecyclerView mGroups;
        public TextView mTvCancel;
        public ImageView mIvSearch;
        public ImageView mIvSet;
        public EditText mEtWord;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.mGroups = (RecyclerView) rootView.findViewById(R.id.rv_view);
            this.mTvCancel = (TextView) rootView.findViewById(R.id.tv_cancel);
            this.mIvSearch = (ImageView) rootView.findViewById(R.id.iv_search);
            this.mIvSet = (ImageView) rootView.findViewById(R.id.iv_set);
            this.mEtWord = (EditText) rootView.findViewById(R.id.et_word);

        }

    }

    public interface CheckGroupListener {
        void checkGroup(CategoryResultBean bean);
    }

    public void setCheckGroupListener(CheckGroupListener mCheckGroupListener) {
        this.mCheckGroupListener = mCheckGroupListener;
    }

    private CheckGroupListener mCheckGroupListener;

    public interface SearchGroupListener {
        void searchGroup(CategoryResultBean bean);
    }

    public void setSearchGroupListener(SearchGroupListener searchGroupListener) {
        this.mSearchGroupListener = searchGroupListener;
    }

    private SearchGroupListener mSearchGroupListener;


}
