package com.iphoto.plus.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.viewbinding.ViewBinding;

import com.iphoto.plus.R;
import com.iphoto.plus.bean.SignInBean;
import com.iphoto.plus.components.activity.LoginActivity;
import com.iphoto.plus.util.BindingUtil;
import com.iphoto.plus.util.Contents;
import com.iphoto.plus.util.MyToast;
import com.iphoto.plus.util.SPUtils;
import com.iphoto.plus.view.MyProgressLoading;
import com.alibaba.fastjson.JSONObject;
import com.gyf.immersionbar.ImmersionBar;
import com.qiniu.android.utils.AsyncRun;

import autodispose2.AutoDispose;
import autodispose2.AutoDisposeConverter;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

public abstract class BaseMvpActivity<P extends BasePresenter, T extends ViewBinding> extends AppCompatActivity implements BaseView {

    protected P mPresenter;
    private MyProgressLoading mLoading;
    private Context mContext;
    protected T viewBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        viewBinding = BindingUtil.createBinding(this, 1);
        if (viewBinding == null) {
            throw new NullPointerException("binding is null");
        }
        mLoading = new MyProgressLoading(this, R.style.DialogStyle);
        setContentView(((ViewBinding) viewBinding).getRoot());
        initView();
        initData();
        ImmersionBar.with(this)
                .statusBarDarkFont(true, 0.2f)
                .init();
    }


    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.detachView();
            viewBinding = null;
        }
        super.onDestroy();
    }

    /**
     * 设置布局
     */
    public abstract int getLayoutId();

    /**
     * 初始化视图
     */
    public abstract void initView();

    protected abstract void initData();

    /**
     * 绑定生命周期 防止MVP内存泄漏
     *
     * @param <P>
     * @return
     */
    @Override
    public <P> AutoDisposeConverter<P> bindAutoDispose() {
        return AutoDispose.autoDisposable(AndroidLifecycleScopeProvider
                .from(this, Lifecycle.Event.ON_DESTROY));
    }

    @Override
    public void showLoading() {
        mLoading.show();
    }

    @Override
    public void hideLoading() {
        mLoading.dismiss();
    }

    @Override
    public void onError(String msg) {
        AsyncRun.runInMain(new Runnable() {
            @Override
            public void run() {

                if (!TextUtils.isEmpty(msg)) {
                    MyToast.show(msg);
                } else {
                    MyToast.show(msg+".");
                }
            }
        });

    }

    @Override
    public void onAgainLogin() {
        mHandler.sendEmptyMessage(1);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                if (!TextUtils.isEmpty((String) msg.obj)) {
                    MyToast.show((String) msg.obj);
                }
            } else {
                SPUtils.remove(Contents.access_token);
                LoginActivity.start(mContext);
            }
        }
    };

}
