package com.iphoto.plus.mvp.model;

import com.iphoto.plus.bean.AlbumListBean;
import com.iphoto.plus.mvp.contract.AlbumTabContract;
import com.iphoto.plus.net.RetrofitClient;

import io.reactivex.rxjava3.core.Observable;

public class AlbumTabModel implements AlbumTabContract.Model {
    @Override
    public Observable<AlbumListBean> getList(int page,int state) {
        return RetrofitClient.getServer().getAlbumList(page,10,state);
    }
}
