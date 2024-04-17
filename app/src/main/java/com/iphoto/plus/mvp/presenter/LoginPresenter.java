package com.iphoto.plus.mvp.presenter;

import com.iphoto.plus.base.BasePresenter;
import com.iphoto.plus.base.CustomObserver;
import com.iphoto.plus.bean.BaseBean;
import com.iphoto.plus.bean.SignInBean;
import com.iphoto.plus.mvp.contract.LoginContract;
import com.iphoto.plus.mvp.model.LoginModel;
import com.iphoto.plus.net.RxScheduler;

import org.jetbrains.annotations.NotNull;

public class LoginPresenter extends BasePresenter<LoginContract.View> implements LoginContract.Presenter {

    private final LoginContract.Model model;

    public LoginPresenter(LoginContract.View view) {
        super(view);
        model = new LoginModel();
    }

    @Override
    public void loginPwd(String phoneNo,String password) {
        model.loginPwd(phoneNo,password).compose(RxScheduler.Obs_io_main())
                .to(getView().bindAutoDispose())
                .subscribe(new CustomObserver<BaseBean>(getView()) {
                    @Override
                    public void onNext(@NotNull BaseBean bean) {
                        getView().onLoginPwdSuccess(bean);
                    }
                });
    }
}
