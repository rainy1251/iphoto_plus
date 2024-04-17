package com.iphoto.plus.components.activity;

import com.iphoto.plus.R;
import com.iphoto.plus.base.BaseActivity;
import com.iphoto.plus.databinding.ActivityRequestPermissionBinding;

public class RequestPermissionActivity extends BaseActivity<ActivityRequestPermissionBinding> {

    @Override
    public int getLayoutId() {
        return R.layout.activity_request_permission;
    }

    @Override
    public void initView() {

        finish();
    }

    @Override
    protected void initData() {

    }
}