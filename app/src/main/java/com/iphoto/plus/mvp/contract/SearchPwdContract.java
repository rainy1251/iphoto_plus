package com.iphoto.plus.mvp.contract;

import com.iphoto.plus.base.BaseView;
import com.iphoto.plus.bean.BaseBean;

import io.reactivex.rxjava3.core.Observable;

public interface SearchPwdContract {
    interface Model {
        Observable<BaseBean> resetPwd(String phoneNo, String code, String pwd);
        Observable<BaseBean> sendCode(String phoneNo);
    }

    interface View extends BaseView {
        void onSuccess(BaseBean bean);
        void onGetCodeSuccess(BaseBean bean);
    }

    interface Presenter {
        void resetPwd(String phoneNo , String code, String pwd);
        void sendCode(String phoneNo );
    }

}
