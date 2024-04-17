package com.iphoto.plus.components.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.iphoto.plus.R;
import com.iphoto.plus.base.BaseMvpActivity;
import com.iphoto.plus.bean.UserBean;
import com.iphoto.plus.components.fragment.AlbumFragment;
import com.iphoto.plus.components.fragment.MineFragment;
import com.iphoto.plus.databinding.ActivityMainBinding;
import com.iphoto.plus.event.LoginAgainEvent;
import com.iphoto.plus.event.LoginExitEvent;
import com.iphoto.plus.event.LoginSuccessEvent;
import com.iphoto.plus.mvp.contract.MineContract;
import com.iphoto.plus.mvp.presenter.MinePresenter;
import com.iphoto.plus.util.Contents;
import com.iphoto.plus.util.MyToast;
import com.iphoto.plus.util.SPUtils;
import com.iphoto.plus.view.AppDB;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseMvpActivity<MinePresenter,ActivityMainBinding> implements
        BottomNavigationView.OnNavigationItemSelectedListener, MineContract.View {

    private List<Fragment> fragments;
    private boolean isExit;
    private MinePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        presenter = new MinePresenter(this);
        presenter.getUserInfo();
        EventBus.getDefault().register(this);
        viewBinding.bottomNavBar.setItemIconTintList(null);
        viewBinding.bottomNavBar.setOnNavigationItemSelectedListener(this);

    }

    @Override
    protected void initData() {
        fragments = new ArrayList<>();
        // fragments.add(new HomeFragment());
        fragments.add(new AlbumFragment());
        fragments.add(new MineFragment());
        setFragmentPosition(0);
        AppDB.getInstance().picDao().delete();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
       // super.onSaveInstanceState(outState);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.home:
//                setFragmentPosition(0);
//                return true;
            case R.id.live:
                setFragmentPosition(0);
                return true;
            case R.id.mine:
                String access_token = SPUtils.getString(Contents.access_token);
                if (TextUtils.isEmpty(access_token)) {
                    LoginActivity.start(this);
                } else {
                    setFragmentPosition(1);
                }
                return true;
        }
        return true;
    }

    private int lastIndex = 0;

    private void setFragmentPosition(int position) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment currentFragment = fragments.get(position);
        Fragment lastFragment = fragments.get(lastIndex);
        lastIndex = position;
        ft.hide(lastFragment);
        if (!currentFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
            ft.add(R.id.container, currentFragment);
        }
        ft.show(currentFragment);
        ft.commitAllowingStateLoss();
    }




    @Override
    public void onBackPressed() {
        exitBy2Click();
    }

    private void exitBy2Click() {
        Timer tExit;
        if (!isExit) {
            isExit = true;
            MyToast.show("再按一次退出应用");
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);

        } else {
            finish();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginEvent(LoginAgainEvent event) {
        LoginActivity.start(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginSuccessEvent(LoginSuccessEvent event) {
        if (event.isLogin()) {
            viewBinding.bottomNavBar.setSelectedItemId(R.id.live);
            setFragmentPosition(0);
            presenter.getUserInfo();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginExitEvent(LoginExitEvent event) {
        viewBinding.bottomNavBar.setSelectedItemId(R.id.live);
        setFragmentPosition(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(true)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onSuccess(UserBean bean) {
//        SPUtils.save(Contents.Id,bean.getId());
    }

    @Override
    public void onSignOutSuccess() {

    }

    @Override
    public void onClean() {

    }
}