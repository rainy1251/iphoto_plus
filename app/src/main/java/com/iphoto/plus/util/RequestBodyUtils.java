package com.iphoto.plus.util;
import com.google.gson.Gson;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import java.util.Map;

public class RequestBodyUtils {

    public static RequestBody getRequestBody(Map<String, Object> map) {
        Gson gson = new Gson();
        String strEntity = gson.toJson(map);
        return RequestBody.create(strEntity, MediaType.parse("application/json"));
    }
}