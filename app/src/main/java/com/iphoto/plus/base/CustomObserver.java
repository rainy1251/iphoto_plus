package com.iphoto.plus.base;

import com.iphoto.plus.net.OnErrorCalBackListener;
import com.iphoto.plus.net.RetrofitClient;
import com.iphoto.plus.util.Contents;
import com.iphoto.plus.util.MyLog;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public abstract class CustomObserver<T> implements Observer<T>, OnErrorCalBackListener {
    @Override
    public void onSubscribe(@NonNull Disposable d) {
        mView.showLoading();
    }

    @Override
    abstract public void onNext(T t);

    @Override
    public void onError(@NonNull Throwable e) {
        mView.hideLoading();
    }

    @Override
    public void onComplete() {
        mView.hideLoading();
    }

    private BaseView mView;

    public CustomObserver(BaseView view) {
        super();
        mView = view;
        RetrofitClient.setOnErrorCalBackListener(this);
    }

    @Override
    public void onError(int code, String msg) {
        MyLog.show(code+"==="+msg);

        if (code == Contents.Code_200 || code == Contents.Code_201) {

            return;
        }

        if (mView != null) {
            mView.hideLoading();
        }
        try {
            switch (code) {
                case Contents.Code_401:
                   // mView.onError("请登录");
                    break;
                case Contents.Code_400:

                case Contents.Code_403:
                    mView.onAgainLogin();
                    break;
                case Contents.Code_404:


                case Contents.Code_405:

                case Contents.Code_415:

                case Contents.Code_500:


                default:
                    mView.onError(msg);

                    break;
            }
        } catch (Exception e) {

        }
    }
}
