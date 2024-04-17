package com.iphoto.plus.view;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;


import com.iphoto.plus.util.MyToast;
import com.iphoto.plus.util.UiUtils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONObject;

import java.io.File;
import java.util.List;

public class MyService extends Service {

    private List<String> imagePathFromSD;


    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        //返回MyBind对象
        return new MyBinder();
    }

    private void methodInMyService(String token,String title) {

        imagePathFromSD = UiUtils.getImagePathFromSD(title);

        if (imagePathFromSD.size() < 1) {
            MyToast.show("无可上传图片");
            return;
        }


        uploadManager = new UploadManager();


        MyToast.show("开始上传图片共"+imagePathFromSD.size()+"张");
        for (int i = 0; i < imagePathFromSD.size(); i++) {

            getUpimg(imagePathFromSD.get(i), token);

        }

    }

    /**
     * 该类用于在onBind方法执行后返回的对象，
     * 该对象对外提供了该服务里的方法
     */
    public class MyBinder extends Binder {


        public void invokeMethodInMyService(String token,String title) {
            methodInMyService(token,title);
        }

        public MyService getMyService() {
            return MyService.this;
        }
    }


    private UploadManager uploadManager;
    private int pro = 0;

    /**
     * 上传
     *
     * @param imagePath
     * @param token
     */
    public void getUpimg(final String imagePath, final String token) {


        uploadManager.put(imagePath, null, token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info,
                                         JSONObject res) {
                        if (info.isOK()||info.statusCode==579) {
                            deleteAllFiles(imagePath);
                            if (onOkRefresh != null) {
                                onOkRefresh.OnOkRefresh(true);

                            }
                           // mHandler.sendEmptyMessage(0);
                        }else  {
//                            if (onErrorRefresh != null) {
//                                onErrorRefresh.OnErrorRefresh(true);
//                            }

                        }

                    }
                }, null);

    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    pro += 1;
//                    if (pro == imagePathFromSD.size()) {
//                        pro = 0;
//                        MyToast.show("上传成功");
//                        if (onSuccessRefresh != null) {
//                          //  onSuccessRefresh.OnSuccessRefresh(true);
//                        }
//
//                    }

                    break;
            }
        }
    };

    private void deleteAllFiles(String pathIndex) {
        File f = new File(pathIndex);
        if (f.exists()) {

            f.delete(); //删除该文件
        } else {
            Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private OnOkRefresh onOkRefresh;

    public interface OnOkRefresh {

        void OnOkRefresh(boolean refresh);

    }

    public void setOnOkRefresh(OnOkRefresh onOkRefresh) {
        this.onOkRefresh = onOkRefresh;
    }

    private OnErrorRefresh onErrorRefresh;

    public interface OnErrorRefresh {

        void OnErrorRefresh(boolean refresh);

    }

    public void setOnErrorRefresh(OnErrorRefresh onErrorRefresh) {
        this.onErrorRefresh = onErrorRefresh;
    }



}