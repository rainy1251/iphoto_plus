package com.iphoto.plus.components.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;

import com.iphoto.plus.R;
import com.iphoto.plus.base.BaseMvpActivity;
import com.iphoto.plus.bean.BaseBean;
import com.iphoto.plus.bean.SignInBean;
import com.iphoto.plus.databinding.ActivityLoginBinding;
import com.iphoto.plus.event.LoginExitEvent;
import com.iphoto.plus.event.LoginSuccessEvent;
import com.iphoto.plus.mvp.contract.LoginContract;
import com.iphoto.plus.mvp.presenter.LoginPresenter;
import com.iphoto.plus.util.Contents;
import com.iphoto.plus.util.MyToast;
import com.iphoto.plus.util.SPUtils;
import com.iphoto.plus.view.TimerCount;
import com.sahooz.library.Country;
import com.sahooz.library.PickActivity;

import org.greenrobot.eventbus.EventBus;

public class LoginActivity extends BaseMvpActivity<LoginPresenter, ActivityLoginBinding>
        implements LoginContract.View, View.OnClickListener {

    private LoginPresenter presenter;
    private String phonePrefix = "86";

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initView() {
        viewBinding.includeLogin.ivBack.setVisibility(View.GONE);
        viewBinding.includeLogin.tvToolbarTitle.setVisibility(View.GONE);

        initListener();
    }

    private void initListener() {
        viewBinding.tvSendCode.setOnClickListener(this);
        viewBinding.tvCountryCode.setOnClickListener(this);

        viewBinding.tvLogin.setOnClickListener(this);
        viewBinding.tvRegister.setOnClickListener(this);
        viewBinding.tvForget.setOnClickListener(this);


    }

    @Override
    protected void initData() {
        presenter = new LoginPresenter(this);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_login:
                String phoneNo = viewBinding.etPhone.getText().toString();
                String pwd = viewBinding.etPwd.getText().toString();
                if (TextUtils.isEmpty(phoneNo)) {
                    MyToast.show("请输入手机号");
                    return;
                }
                if (TextUtils.isEmpty(pwd)) {
                    MyToast.show("请输入密码");
                    return;
                }
                presenter.loginPwd(phoneNo, pwd);
                break;
            case R.id.tv_register:
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);

                break;
            case R.id.tv_forget:
                Intent reset = new Intent(this, ResetPwdActivity.class);
                startActivity(reset);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        String access_token = SPUtils.getString(Contents.access_token);
        if (TextUtils.isEmpty(access_token)) {
            EventBus.getDefault().post(new LoginExitEvent());
        }
    }

    @Override
    public void onLoginPwdSuccess(BaseBean<SignInBean> baseBean) {
        if (baseBean.getCode() == 0) {
            SPUtils.save(Contents.access_token, baseBean.getData().getToken());
            SPUtils.save(Contents.refresh_token, baseBean.getData().getRefreshToken());
            MyToast.show("登录成功");
            EventBus.getDefault().post(new LoginSuccessEvent(true));
            finish();
        }
    }
}