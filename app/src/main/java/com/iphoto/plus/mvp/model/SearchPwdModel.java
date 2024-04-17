package com.iphoto.plus.mvp.model;

import com.iphoto.plus.bean.BaseBean;
import com.iphoto.plus.mvp.contract.SearchPwdContract;
import com.iphoto.plus.net.RetrofitClient;
import com.iphoto.plus.util.Contents;
import com.iphoto.plus.util.RequestBodyUtils;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.RequestBody;

public class SearchPwdModel implements SearchPwdContract.Model {

    @Override
    public Observable<BaseBean> sendCode(String phoneNo) {
        Map<String, Object> map = new HashMap<>();
        map.put(Contents.PhoneNo, phoneNo);
        RequestBody requestBody = RequestBodyUtils.getRequestBody(map);
        return RetrofitClient.getServer().sendRegisterCode(requestBody);
    }

    @Override
    public Observable<BaseBean> resetPwd(String phoneNo, String code, String pwd) {
        Map<String, Object> map = new HashMap<>();
        map.put("verifyCode", code);
        map.put("password", pwd);
        RequestBody requestBody = RequestBodyUtils.getRequestBody(map);
        return RetrofitClient.getServer().resetPwd(requestBody);
    }
}
