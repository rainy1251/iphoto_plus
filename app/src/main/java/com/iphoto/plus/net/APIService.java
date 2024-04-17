package com.iphoto.plus.net;


import com.iphoto.plus.bean.AlbumListBean;
import com.iphoto.plus.bean.BannerBean;
import com.iphoto.plus.bean.BaseBean;
import com.iphoto.plus.bean.CategoryBean;
import com.iphoto.plus.bean.OriginalPicBean;
import com.iphoto.plus.bean.RegisterCodeBean;
import com.iphoto.plus.bean.SignInBean;
import com.iphoto.plus.bean.UploadFileBean;
import com.iphoto.plus.bean.UploadTokenBean;
import com.iphoto.plus.bean.UserBean;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface APIService {


    /**
     * 相册列表
     */

    @GET("/api/v1/album")
    Observable<AlbumListBean> getAlbumList(@Query("page") int page,@Query("pageSize") int pageSize , @Query("status") int status);

    /**
     * 获取用户信息
     */
    @GET("/api/v1/user/info")
    Observable<UserBean> getUserInfo();

    /**
     * 手机号密码登录
     */

    @POST("/api/v1/auth/login")
    Observable<BaseBean<SignInBean>> loginPwd(@Body RequestBody requestBody);

    /**
     * 发送验证码
     */
    @POST("/api/v1/aliyun/sendVerifyCode")
    Observable<BaseBean> sendRegisterCode(@Body RequestBody requestBody);

    /**
     * 注册
     */
    @POST("/api/v1/auth/register")
    Observable<BaseBean> register(@Body RequestBody requestBody);

    /**
     * 刷新令牌
     */
    @FormUrlEncoded
    @POST("/oauth/connect/token")
    Call<SignInBean> refreshToken(@FieldMap Map<String, Object> map);

    /**
     * 重置密码
     */
    @POST("/api/v1/user/update")
    Observable<BaseBean> resetPwd(@Body RequestBody requestBody);

    /**
     * 发送验证码
     */
    @FormUrlEncoded
    @POST("/oauth/smsconnect/code")
    Observable<BaseBean> sendCode(@FieldMap Map<String, Object> map);


    /**
     * 获取图片上传令牌
     */
    @POST("originalpicture/uploadToken")
    Observable<UploadTokenBean> getQiNiuToken(@Body RequestBody requestBody);

    /**
     * 上传对象(文件)
     */
    @Multipart
    @POST("/upload")
    Observable<UploadFileBean> getUpLoad(@Part("token") RequestBody token, @Part MultipartBody.Part file);

    /**
     * 查找原始照片列表
     */
    @GET("/originalpicture/list")
    Observable<OriginalPicBean> getOriginalPic(@Query("offset") int offset, @Query("limit") int limit
            , @Query("albumId") String albumId, @Query("createdUserId") String createdUserId);

    /**
     * 获取原图分类列表
     */
    @GET("/originalpicturecategory/list")
    Observable<CategoryBean> getCategoryList(@Query("albumId") String albumId);
}
