package com.iphoto.plus.mvp.model;

import com.iphoto.plus.bean.OriginalPicBean;
import com.iphoto.plus.mvp.contract.NoticeContract;
import com.iphoto.plus.net.RetrofitClient;

import io.reactivex.rxjava3.core.Observable;

public class NoticeModel implements NoticeContract.Model {

    @Override
    public Observable<OriginalPicBean> getOriginalPic(int offset, String albumId, String photographerId) {
        return RetrofitClient.getServer().getOriginalPic(offset,50,albumId,photographerId);
    }

}
