package com.iphoto.plus.mvp.presenter;

import com.iphoto.plus.base.BasePresenter;
import com.iphoto.plus.base.CustomObserver;
import com.iphoto.plus.bean.UserBean;
import com.iphoto.plus.mvp.contract.MineContract;
import com.iphoto.plus.mvp.model.MineModel;
import com.iphoto.plus.net.RxScheduler;

import org.jetbrains.annotations.NotNull;

public class MinePresenter extends BasePresenter<MineContract.View> implements MineContract.Presenter {


    private final MineContract.Model model;

    public MinePresenter(MineContract.View view) {
        super(view);
        model = new MineModel();
    }

    @Override
    public void getUserInfo() {
   
        model.getInfo()
                .compose(RxScheduler.Obs_io_main())
                .to(getView().bindAutoDispose())//解决内存泄漏
                .subscribe(new CustomObserver<UserBean>(getView()) {
                    @Override
                    public void onNext(@NotNull UserBean bean) {
                        getView().onSuccess(bean);
                    }
                });
    }

    @Override
    public void signOut() {
        model.signOut();
        getView().onSignOutSuccess();
    }

    @Override
    public void cleanCache() {
        model.cleanCache().compose(RxScheduler.Obs_io_main())
                .to(getView().bindAutoDispose())//解决内存泄漏
                .subscribe(t -> {
                    if (t) {
                        getView().onClean();
                    }
                });
    }


}
