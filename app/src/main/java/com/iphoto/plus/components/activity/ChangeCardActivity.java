package com.iphoto.plus.components.activity;

import com.iphoto.plus.R;
import com.iphoto.plus.base.BaseActivity;
import com.iphoto.plus.databinding.ActivityChangeCardBinding;

public class ChangeCardActivity extends BaseActivity<ActivityChangeCardBinding> {

    @Override
    public int getLayoutId() {
        return R.layout.activity_change_card;
    }

    @Override
    public void initView() {

    }

    @Override
    protected void initData() {
        finish();
    }
}