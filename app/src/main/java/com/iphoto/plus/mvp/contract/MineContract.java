package com.iphoto.plus.mvp.contract;

import com.iphoto.plus.base.BaseView;
import com.iphoto.plus.bean.UserBean;

import io.reactivex.rxjava3.core.Observable;

public interface MineContract {

    interface Model {
        Observable<UserBean> getInfo();

        void signOut();

        Observable<Boolean>  cleanCache();
    }

    interface Presenter {
        void getUserInfo();
        void signOut();
        void cleanCache();
    }

    interface View extends BaseView {
        void onSuccess(UserBean bean);

        void onSignOutSuccess();

        void onClean();

    }

}
