package com.iphoto.plus.mvp.model;

import static com.iphoto.plus.util.RequestBodyUtils.getRequestBody;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.iphoto.plus.bean.BaseBean;
import com.iphoto.plus.bean.RegisterCodeBean;
import com.iphoto.plus.bean.SignInBean;
import com.iphoto.plus.mvp.contract.LoginContract;
import com.iphoto.plus.mvp.contract.RegisterContract;
import com.iphoto.plus.net.RetrofitClient;
import com.iphoto.plus.util.Contents;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class RegisterModel implements RegisterContract.Model {

    @Override
    public Observable<BaseBean> register(String phoneNo, String pwd, String code, String nickname) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("username", nickname);
        map.put("password", pwd);
        map.put("phone",phoneNo);
        map.put(Contents.verifyCode, code);
        RequestBody body = getRequestBody(map);
        return RetrofitClient.getServer().register(body);
    }

    @Override
    public Observable<BaseBean> getCode(String phoneNo) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("phone",phoneNo);
        RequestBody body = getRequestBody(map);
        return RetrofitClient.getServer().sendRegisterCode(body);
    }

}
