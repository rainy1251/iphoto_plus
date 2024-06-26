package com.iphoto.plus.net.qiniu;


import com.iphoto.plus.bean.PicBean;
import com.iphoto.plus.util.FilesUtil;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;


/**
 * ClassName: RetrieveImageSubscribe
 * Description:
 * Author: levi
 * CreateDate: 2020/8/13 14:48
 */
public class QiNiuImageSubscribe implements ObservableOnSubscribe<PicBean> {

    //    private PicBean mPicBean;
    private String mUpLoadToken;
    private String mPicPath;
    private PicBean mPicBean;

    public QiNiuImageSubscribe(PicBean picBean, String token) {
        this.mPicPath = picBean.getPicPath();
        this.mPicBean = picBean;
        this.mUpLoadToken = token;
    }

    @Override
    public void subscribe(ObservableEmitter<PicBean> emitter) throws Exception {
        File file = new File(mPicPath);
        long length = file.length();

//        if (length > 4 * 1024 * 1024) {
//            Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
//            options.quality = 90;
//            Tiny.getInstance().source(mPicPath).asFile().withOptions(options).compress(new FileCallback() {
//                @Override
//                public void callback(boolean isSuccess, String outfile, Throwable t) {
//                    if (isSuccess) {
//                        upLoadFile(emitter, outfile);
//                    }
//                }
//            });
//        } else {
        upLoadFile(emitter, mPicPath);
//        }


    }

    private void upLoadFile(ObservableEmitter<PicBean> emitter, String path) {
        Map<String, String> params = new HashMap<>();
        params.put("x:LocalKey", mPicBean.getPicId() + ":" + mPicBean.getPicName());
        QiNiuCloud.configQiNiu().put(path, "1", mUpLoadToken, (key12, info, res) -> {
            if (info.isOK() || info.statusCode == 579 || info.statusCode == 614) {
                mPicBean.setProgress(0);
                mPicBean.setStatus(1);
                FilesUtil.deleteFile(path);
                try {
                    mPicBean.setPicPath(res.getString("url"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                emitter.onNext(mPicBean);
                emitter.onComplete();
            } else {
                mPicBean.setProgress(0);
                mPicBean.setStatus(-1);
                emitter.onNext(mPicBean);
                emitter.onComplete();
            }
        }, new UploadOptions(params, null, true, (key1, percent) -> {
            int progress = (int) (percent * 100);
            mPicBean.setProgress(progress);
            mPicBean.setStatus(0);
            emitter.onNext(mPicBean);
        }, null));
    }

}