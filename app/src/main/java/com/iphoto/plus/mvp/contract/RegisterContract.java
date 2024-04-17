package com.iphoto.plus.mvp.contract;

import com.iphoto.plus.base.BaseView;
import com.iphoto.plus.bean.BaseBean;
import com.iphoto.plus.bean.SignInBean;

import io.reactivex.rxjava3.core.Observable;


public interface RegisterContract {

    interface Model {
        Observable<BaseBean> register(String phoneNo, String pwd,String code,String nickname);
        Observable<BaseBean> getCode(String phoneNo);
    }

    interface Presenter {

        void register(String phoneNo, String pwd,String code,String nickname);

        void getCode(String phoneNo);
    }

    interface View extends BaseView {

        void onRegisterSuccess(BaseBean baseBean);
        void onGetCodeSuccess(BaseBean baseBean);
    }
}
