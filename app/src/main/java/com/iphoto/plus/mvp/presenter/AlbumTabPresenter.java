package com.iphoto.plus.mvp.presenter;

import com.iphoto.plus.base.BasePresenter;
import com.iphoto.plus.base.CustomObserver;
import com.iphoto.plus.bean.AlbumListBean;
import com.iphoto.plus.mvp.contract.AlbumTabContract;
import com.iphoto.plus.mvp.model.AlbumTabModel;
import com.iphoto.plus.net.RxScheduler;
import com.iphoto.plus.util.Contents;

import org.jetbrains.annotations.NotNull;

public class AlbumTabPresenter extends BasePresenter<AlbumTabContract.View> implements AlbumTabContract.Presenter{

    private final AlbumTabContract.Model model;

    public AlbumTabPresenter(AlbumTabContract.View view) {
        super(view);
        model = new AlbumTabModel();
    }

    @Override
    public void getList(int page,int state) {
        model.getList(page,state)
                .compose(RxScheduler.Obs_io_main())
                .to(getView().bindAutoDispose())//解决内存泄漏
                .subscribe(new CustomObserver<AlbumListBean>(getView()) {
                    @Override
                    public void onNext(@NotNull AlbumListBean bean) {
                        getView().onSuccess(bean);
                    }
                });
    }
}
