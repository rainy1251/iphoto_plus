package com.iphoto.plus.mvp.contract;

import com.iphoto.plus.base.BaseView;
import com.iphoto.plus.bean.BaseBean;
import com.iphoto.plus.bean.SignInBean;

import io.reactivex.rxjava3.core.Observable;


public interface LoginContract {

    interface Model {
        Observable<BaseBean<SignInBean>> loginPwd(String phoneNo,String pwd);
    }

    interface Presenter {
        void loginPwd(String phoneNo,String pwd);
    }

    interface View extends BaseView {
        void onLoginPwdSuccess(BaseBean<SignInBean> baseBean);
    }
}
