package com.iphoto.plus.mvp.presenter;

import com.iphoto.plus.base.BasePresenter;
import com.iphoto.plus.base.CustomObserver;
import com.iphoto.plus.bean.BaseBean;
import com.iphoto.plus.bean.SignInBean;
import com.iphoto.plus.mvp.contract.LoginContract;
import com.iphoto.plus.mvp.contract.RegisterContract;
import com.iphoto.plus.mvp.model.LoginModel;
import com.iphoto.plus.mvp.model.RegisterModel;
import com.iphoto.plus.net.RxScheduler;

import org.jetbrains.annotations.NotNull;

public class RegisterPresenter extends BasePresenter<RegisterContract.View> implements RegisterContract.Presenter {

    private final RegisterContract.Model model;

    public RegisterPresenter(RegisterContract.View view) {
        super(view);
        model = new RegisterModel();
    }

    @Override
    public void register(String phoneNo, String pwd,String code,String nickname) {

        model.register(phoneNo, pwd,code,nickname).compose(RxScheduler.Obs_io_main())
                .to(getView().bindAutoDispose())
                .subscribe(new CustomObserver<BaseBean>(getView()) {
                    @Override
                    public void onNext(@NotNull BaseBean bean) {
                        getView().onRegisterSuccess(bean);
                    }
                });

    }

    @Override
    public void getCode(String phoneNo) {
        model.getCode(phoneNo).compose(RxScheduler.Obs_io_main())
                .to(getView().bindAutoDispose())
                .subscribe(new CustomObserver<BaseBean>(getView()) {
                    @Override
                    public void onNext(@NotNull BaseBean bean) {
                        getView().onGetCodeSuccess(bean);
                    }
                });
    }

}
