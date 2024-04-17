package com.iphoto.plus.components.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;

import com.iphoto.plus.R;
import com.iphoto.plus.base.BaseMvpActivity;
import com.iphoto.plus.bean.BaseBean;
import com.iphoto.plus.databinding.ActivityRegisterBinding;
import com.iphoto.plus.mvp.contract.RegisterContract;
import com.iphoto.plus.mvp.presenter.RegisterPresenter;
import com.iphoto.plus.util.MyToast;

public class RegisterActivity extends BaseMvpActivity<RegisterPresenter, ActivityRegisterBinding> implements
        View.OnClickListener, RegisterContract.View {

    private RegisterPresenter presenter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    public void initView() {
        viewBinding.includeLogin.ivBack.setVisibility(View.GONE);
        viewBinding.includeLogin.tvToolbarTitle.setVisibility(View.GONE);
    }

    @Override
    protected void initData() {
        presenter = new RegisterPresenter(this);
        initListener();
    }

    private void initListener() {
        viewBinding.tvSendCode.setOnClickListener(this);
        viewBinding.tvRegister.setOnClickListener(this);
        viewBinding.tvGotoLogin.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_register:
                String phoneNo = viewBinding.etPhone.getText().toString();
                String pwd = viewBinding.etPwd.getText().toString();
                String code = viewBinding.etCode.getText().toString();
                String nickname = viewBinding.etNickname.getText().toString();
                if (TextUtils.isEmpty(phoneNo)) {
                    MyToast.show("请输入手机号");
                    return;
                }
                if (TextUtils.isEmpty(pwd)) {
                    MyToast.show("请输入6-12位字母或数字");
                    return;
                }
                if (pwd.length()<6) {
                    MyToast.show("请输入6-12位字母或数字");
                    return;
                }

                if (TextUtils.isEmpty(code)&&code.length()==6) {
                    MyToast.show("请输入验证码");
                    return;
                }
                if (TextUtils.isEmpty(nickname)) {
                    MyToast.show("请输入昵称");
                    return;
                }
                presenter.register(phoneNo, pwd, code,nickname);
                break;
            case R.id.tv_sendCode:
                String phone = viewBinding.etPhone.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    MyToast.show("请输入手机号");
                    return;
                }
                presenter.getCode(phone);

                break;
            case R.id.tv_goto_login:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onRegisterSuccess(BaseBean baseBean) {
        if (baseBean.getCode() == 0) {
            MyToast.show("注册成功,去登录");
            finish();
        }else{
            MyToast.show(baseBean.getMessage());
        }

    }
    private CountDownTimer countDownTimer;
    @Override
    public void onGetCodeSuccess(BaseBean baseBean) {
        if (baseBean.getCode() == 0) {
            MyToast.show("验证码发送成功");
            countDownTimer = new CountDownTimer(30 * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    String str = millisUntilFinished / 1000 + "s";
                    viewBinding.tvSendCode.setText(str);
                }

                @Override
                public void onFinish() {
                    viewBinding.tvSendCode.setText("发送验证码");
                    viewBinding.tvSendCode.setEnabled(true);
                }
            };
            countDownTimer.start();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}