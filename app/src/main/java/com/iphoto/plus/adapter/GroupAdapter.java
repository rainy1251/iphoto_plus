package com.iphoto.plus.adapter;

import android.annotation.SuppressLint;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.iphoto.plus.R;
import com.iphoto.plus.bean.CategoryResultBean;
import com.iphoto.plus.util.DimenUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import me.jessyan.autosize.utils.ScreenUtils;

public class GroupAdapter extends BaseQuickAdapter<CategoryResultBean,BaseViewHolder> {
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    private String groupId;
    private List<CategoryResultBean> mData;

    public GroupAdapter(int layoutResId) {
        super(layoutResId);
    }

    public GroupAdapter(int layoutResId, @Nullable List<CategoryResultBean> data) {
        super(layoutResId, data);
        mData =data;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void convert(@NonNull BaseViewHolder vh, CategoryResultBean data) {
        vh.setText(R.id.rb_button, data.getName().replaceAll("\\s", ""));
        TextView view = vh.findView(R.id.rb_button);
        assert view != null;
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        int[] screenSize = ScreenUtils.getScreenSize(getContext());
        layoutParams.width= (screenSize[0]-(int)DimenUtil.dpToPx(getContext(),30))/2;
        view.setLayoutParams(layoutParams);
        if (data.getId().equals(getGroupId())) {
            vh.setBackgroundResource(R.id.rb_button,R.drawable.shape_item_group_checked);
            vh.setTextColorRes(R.id.rb_button,R.color.yp_group_text_checked);
        }else{
            vh.setBackgroundResource(R.id.rb_button,R.drawable.select_group_item_bg);
            vh.setTextColorRes(R.id.rb_button,R.color.select_group_text_color);
        }
    }


    public void clean(){
        mData.clear();
        notifyDataSetChanged();
    }

}
