package com.iphoto.plus.mvp.model;

import static com.iphoto.plus.util.RequestBodyUtils.getRequestBody;

import com.iphoto.plus.bean.BaseBean;
import com.iphoto.plus.bean.SignInBean;
import com.iphoto.plus.mvp.contract.LoginContract;
import com.iphoto.plus.net.RetrofitClient;
import com.iphoto.plus.util.Contents;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.RequestBody;

public class LoginModel implements LoginContract.Model {

    @Override
    public Observable<BaseBean<SignInBean>> loginPwd(String phoneNo,String password) {
        Map<String, Object> map = new HashMap<>();
        map.put(Contents.PhoneNo, phoneNo);
        map.put("password", password);
        RequestBody body = getRequestBody(map);
        return RetrofitClient.getServer().loginPwd(body);
    }

}
