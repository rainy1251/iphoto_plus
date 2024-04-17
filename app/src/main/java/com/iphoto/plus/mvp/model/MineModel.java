package com.iphoto.plus.mvp.model;

import com.iphoto.plus.bean.UserBean;
import com.iphoto.plus.mvp.contract.MineContract;
import com.iphoto.plus.net.RetrofitClient;
import com.iphoto.plus.util.Contents;
import com.iphoto.plus.util.FilesUtil;
import com.iphoto.plus.util.SPUtils;
import com.iphoto.plus.util.UiUtils;
import com.iphoto.plus.view.CacheDataManager;

import java.io.File;

import io.reactivex.rxjava3.core.Observable;

public class MineModel implements MineContract.Model {
    @Override
    public Observable<UserBean> getInfo() {
        return RetrofitClient.getServer().getUserInfo();
    }

    @Override
    public void signOut() {
        SPUtils.save(Contents.access_token,"");
    }

    @Override
    public Observable<Boolean> cleanCache() {

        return Observable.create(emitter -> {
            CacheDataManager.clearAllCache(UiUtils.getContext());
            FilesUtil.deleteDirectory(new File(FilesUtil.getBasePath()));
            emitter.onNext(true);
            emitter.onComplete();
        });
    }


}
