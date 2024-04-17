package com.iphoto.plus.mvp.contract;

import com.iphoto.plus.base.BaseView;
import com.iphoto.plus.bean.AlbumListBean;

import io.reactivex.rxjava3.core.Observable;


public interface AlbumTabContract {
    interface Model {
        Observable<AlbumListBean> getList(int page,int state);
    }

    interface View extends BaseView {
        void onSuccess(AlbumListBean bean);
    }

    interface Presenter {
        void getList(int page,int state);
    }

}
