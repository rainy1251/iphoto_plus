package com.iphoto.plus.components.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;

import com.iphoto.plus.R;
import com.iphoto.plus.base.BaseMvpActivity;
import com.iphoto.plus.bean.BaseBean;
import com.iphoto.plus.databinding.ActivitySearchPwdBinding;
import com.iphoto.plus.mvp.contract.SearchPwdContract;
import com.iphoto.plus.mvp.presenter.SearchPwdPresenter;
import com.iphoto.plus.util.MyToast;
import com.iphoto.plus.view.TimerCount;

public class ResetPwdActivity extends BaseMvpActivity<SearchPwdPresenter, ActivitySearchPwdBinding>
        implements SearchPwdContract.View, View.OnClickListener {

    private SearchPwdPresenter presenter;
    private CountDownTimer countDownTimer;
    private TimerCount timer;

    @Override
    public int getLayoutId() {
        return R.layout.activity_search_pwd;
    }

    @Override
    public void initView() {
        viewBinding.includeLogin.tvToolbarTitle.setVisibility(View.GONE);
    }

    @Override
    protected void initData() {
        presenter = new SearchPwdPresenter(this);
        initListener();
    }

    private void initListener() {
        viewBinding.tvSendCode.setOnClickListener(this);
        viewBinding.tvReset.setOnClickListener(this);
    }

    public static void start(Activity context) {
        Intent intent = new Intent(context, ResetPwdActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onSuccess(BaseBean bean) {
        if (bean.getCode()==0) {
            MyToast.show("修改成功");
            finish();
        }

    }

    @Override
    public void onGetCodeSuccess(BaseBean bean) {

        if (bean.getCode() == 0) {
            MyToast.show("验证码发送成功");
            timer = new TimerCount(60000, 1000, viewBinding.tvSendCode);
            timer.start();

        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        String phoneNo;
        switch (v.getId()) {
            case R.id.tv_sendCode:
                phoneNo = viewBinding.etPhone.getText().toString();
                if (TextUtils.isEmpty(phoneNo)) {
                    MyToast.show("请输入手机号");
                    return;
                }
                presenter.sendCode(phoneNo);
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_reset:
                String code = viewBinding.etCode.getText().toString();
                String pwd = viewBinding.etPwd.getText().toString();
                phoneNo = viewBinding.etPhone.getText().toString();
                if (TextUtils.isEmpty(phoneNo)) {
                    MyToast.show("请输入手机号");
                    return;
                }
                if (TextUtils.isEmpty(code)) {
                    MyToast.show("请输入验证码");
                    return;
                }
                if (TextUtils.isEmpty(pwd)) {
                    MyToast.show("请输入新密码");
                    return;
                }
                if (pwd.length()<6) {
                    MyToast.show("请输入6-12位字母或数字");
                    return;
                }
                presenter.resetPwd(phoneNo, code, pwd);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }

    }
}
