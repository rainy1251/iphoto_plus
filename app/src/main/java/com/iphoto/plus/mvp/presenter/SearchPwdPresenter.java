package com.iphoto.plus.mvp.presenter;

import com.iphoto.plus.base.BasePresenter;
import com.iphoto.plus.base.CustomObserver;
import com.iphoto.plus.bean.BaseBean;
import com.iphoto.plus.mvp.contract.SearchPwdContract;
import com.iphoto.plus.mvp.model.SearchPwdModel;
import com.iphoto.plus.net.RxScheduler;

import org.jetbrains.annotations.NotNull;

public class SearchPwdPresenter extends BasePresenter<SearchPwdContract.View> implements SearchPwdContract.Presenter {

    private  SearchPwdContract.Model model;

    public SearchPwdPresenter(SearchPwdContract.View view) {
        super(view);
        model = new SearchPwdModel();
    }

    @Override
    public void resetPwd(String phoneNo, String code, String pwd ){

        model.resetPwd(phoneNo,code,pwd)
                .compose(RxScheduler.Obs_io_main())
                .to(getView().bindAutoDispose())//解决内存泄漏
                .subscribe(new CustomObserver<BaseBean>(getView()) {
                    @Override
                    public void onNext(@NotNull BaseBean bean) {
                        getView().onSuccess(bean);
                    }
                });
    }

    @Override
    public void sendCode(String phoneNo ){

        model.sendCode(phoneNo)
                .compose(RxScheduler.Obs_io_main())
                .to(getView().bindAutoDispose())//解决内存泄漏
                .subscribe(new CustomObserver<BaseBean>(getView()) {
                    @Override
                    public void onNext(@NotNull BaseBean bean) {
                        getView().onGetCodeSuccess(bean);
                    }
                });
    }
}
