package com.iphoto.plus.components.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.iphoto.plus.R;
import com.iphoto.plus.components.fragment.LiveFragment;
import com.iphoto.plus.event.ChangeCardEvent;
import com.iphoto.plus.ptp.Camera;
import com.iphoto.plus.ptp.PtpService;
import com.iphoto.plus.ptp.model.LiveViewData;
import com.iphoto.plus.util.AppConfig;
import com.iphoto.plus.util.AppSettings;
import com.iphoto.plus.util.Contents;
import com.iphoto.plus.util.FilesUtil;
import com.iphoto.plus.util.MyToast;
import com.iphoto.plus.util.PermissionUtils;
import com.iphoto.plus.util.SPUtils;
import com.iphoto.plus.view.CommonDialog;
import com.iphoto.plus.view.SessionActivity;
import com.iphoto.plus.view.SessionView;
import com.gyf.immersionbar.ImmersionBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/***
 * 集成指南
 * 1. copy two *.jar to libs folder
 * 2.  虚拟硬件功能 <uses-feature android:name="android.hardware.usb.host"/>
 * 3.  WLAN 通信因此需要这个权限 <uses-permission android:name="android.permission.INTERNET"/>
 * 4.  xml/device_filter.xml
 * *****/
public class LiveActivity extends SessionActivity implements Camera.CameraListener
        , CommonDialog.OnConfirmListener, CommonDialog.OnCancelListener {

    //日志
    private final String TAG = LiveActivity.class.getSimpleName();

//    private static final int DIALOG_PROGRESS = 1;
//    private static final int DIALOG_NO_CAMERA = 2;

    private final Handler handler = new Handler();

    private PtpService ptp;
    private Camera camera;

    private boolean isInStart;
    private boolean isInResume = true;
    private SessionView sessionFrag;
    private AppSettings settings;
    private List<Fragment> fragments;
    private boolean mReceiverTag;
    private boolean isChange =true;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        if (AppConfig.LOG) {
            Log.i(TAG, "onCreate");
        }
        ImmersionBar.with(this)
                .statusBarDarkFont(true, 0.2f)
                .init();
        settings = new AppSettings(this);

        EventBus.getDefault().register(this);
        ptp = PtpService.Singleton.getInstance(this);
        requestExternalRW();
//        initData();
    }
    public void requestExternalRW() {
        if (PermissionUtils.isGrantExternalRW(this, 1)) {
//            presenter.getOriginalPic(pageIndex, albumId, photographerId);
            initData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //检验是否获取权限，如果获取权限，外部存储会处于开放状态，会弹出一个toast提示获得授权
                String sdCard = Environment.getExternalStorageState();
                if (sdCard.equals(Environment.MEDIA_MOUNTED)) {
                  //  presenter.getOriginalPic(pageIndex, albumId, photographerId);
                    initData();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    private void initData() {

        String albumId = getIntent().getStringExtra(Contents.AlbumId);
        SPUtils.save(Contents.AlbumId, albumId);

        createDir(albumId);

        fragments = new ArrayList<>();
        fragments.add(new LiveFragment());
        setFragmentPosition();

        registerReceiver();
        settings = new AppSettings(this);
        ptp = PtpService.Singleton.getInstance(this);
    }

    private int lastIndex = 0;

    private void setFragmentPosition() {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment currentFragment = fragments.get(0);
        Fragment lastFragment = fragments.get(lastIndex);
        lastIndex = 0;
        ft.hide(lastFragment);
        if (!currentFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
            ft.add(R.id.container, currentFragment);
        }
        ft.show(currentFragment);
        ft.commitAllowingStateLoss();


    }

    /**
     * 创建文件夹
     */
    private void createDir(String albumId) {
        String absolutePath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        File activityDir = new File(absolutePath + File.separator + "IPhoto");
        if (!activityDir.exists()) {
            boolean mkdir = activityDir.mkdir();
            // 检查mkdir()的结果
            if (!mkdir) {
                MyToast.show("无法创建目录");
            }
        }
        File appDir = new File(FilesUtil.getDirPath(albumId));
        if (!appDir.exists()) {
            boolean mkdir = appDir.mkdir();
            if (!mkdir) {
                MyToast.show("无法创建子目录");
            }
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (AppConfig.LOG) {
            Log.i(TAG, "onNewIntent " + intent.getAction());
        }
        this.setIntent(intent);
        if (isInStart) {
            ptp.initialize(this, intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (AppConfig.LOG) {
            Log.i(TAG, "onStart");
        }
        isInStart = true;
        ptp.setCameraListener(this);
       ptp.initialize(this, getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        isInResume = true;
        if (AppConfig.LOG) {
            Log.i(TAG, "onResume");
        }
        if (isChange) {
            ptp.setCameraListener(this);
            ptp.initialize(this, getIntent());
            isChange = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isInResume = false;
        if (AppConfig.LOG) {
            Log.i(TAG, "onPause");
        }
        if (isChange) {
            ptp.setCameraListener(null);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (AppConfig.LOG) {
            Log.i(TAG, "onStop");
        }
        isInStart = false;
//        ptp.setCameraListener(null);
//        if (isFinishing()) {
//            ptp.shutdown();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isInStart = false;
        ptp.setCameraListener(null);
        if (AppConfig.LOG) {
            Log.i(TAG, "onDestroy");
        }
        if (isFinishing()) {
            ptp.shutdown();
        }
        unregisterReceiver();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }


    private void sendDeviceInformation() {
        Thread th = new Thread(() -> {
            File dir = getExternalCacheDir();
            final File out = dir != null ? new File(dir, "deviceinfo.txt") : null;

            if (camera != null) {
                camera.writeDebugInfo(out);
            }

            final String shortDeviceInfo = out == null && camera != null ? camera.getDeviceInfo() : "unknown";

            handler.post(() -> {
              //  LiveActivity.this.dismissDialog(DIALOG_PROGRESS);
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "RYC USB Feedback");
                sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"PUT_EMAIL_HERE"});
                if (out != null && camera != null) {
                    sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + out));
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Any problems or feature whishes? Let us know: ");
                } else {
                    sendIntent.putExtra(Intent.EXTRA_TEXT,
                            "Any problems or feature whishes? Let us know: \n\n\n" + shortDeviceInfo);
                }
                startActivity(Intent.createChooser(sendIntent, "Email:"));
            });
        });
        th.start();
    }

    /****
     * 重写接口方法
     * **/
    @Override
    public void onCameraStarted(Camera camera) {
        this.camera = camera;
        if (AppConfig.LOG) {
            Log.i(TAG, "camera started");
        }
//        try {
//            dismissDialog(DIALOG_NO_CAMERA);
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        }

        camera.setCapturedPictureSampleSize(settings.getCapturedPictureSampleSize());
        if (sessionFrag != null) {
            if (vibrator != null) {
                vibrator.cancel();
            }
            sessionFrag.cameraStarted(camera);
        }
    }

    @Override
    public void onCameraStopped(Camera camera) {
        if (AppConfig.LOG) {
            Log.i(TAG, "camera stopped");
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.camera = null;
        sessionFrag.cameraStopped(camera);
    }

    @Override
    public void onNoCameraFound() {
        sessionFrag.cameraNoFound();
       // Toast.makeText(LiveActivity.this, "没检测到相机", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(String message) {
        sessionFrag.enableUi(false);
        sessionFrag.cameraStopped(null);
        startVibrator();
        showVibratorDialog();
    }

    @Override
    public void onPropertyChanged(int property, int value) {
        sessionFrag.propertyChanged(property, value);
    }

    @Override
    public void onPropertyStateChanged(int property, boolean enabled) {

    }

    @Override
    public void onPropertyDescChanged(int property, int[] values) {
        sessionFrag.propertyDescChanged(property, values);
    }

    @Override
    public void onLiveViewStarted() {
        sessionFrag.liveViewStarted();
    }

    @Override
    public void onLiveViewData(LiveViewData data) {
        if (!isInResume) {
            return;
        }
        sessionFrag.liveViewData(data);
    }

    @Override
    public void onLiveViewStopped() {
        sessionFrag.liveViewStopped();
    }

    @Override
    public void onCapturedPictureReceived(int objectHandle, String filename, Bitmap thumbnail, Bitmap bitmap) {
        if (thumbnail != null) {
            sessionFrag.capturedPictureReceived(objectHandle, filename, thumbnail, bitmap);
        } else {
            Toast.makeText(this, "No thumbnail available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBulbStarted() {
        sessionFrag.setCaptureBtnText("0");
    }

    @Override
    public void onBulbExposureTime(int seconds) {
        sessionFrag.setCaptureBtnText("" + seconds);
    }

    @Override
    public void onBulbStopped() {
        sessionFrag.setCaptureBtnText("Fire");
    }

    @Override
    public void onFocusStarted() {
        sessionFrag.focusStarted();
    }

    @Override
    public void onFocusEnded(boolean hasFocused) {
        sessionFrag.focusEnded(hasFocused);
    }

    @Override
    public void onFocusPointsChanged() {

    }

    @Override
    public void onObjectAdded(int handle, int format) {
        sessionFrag.objectAdded(handle, format);
    }

    @Override
    public void onRateAdded(int handle) {
        sessionFrag.rateAdded(handle);
    }

    @Override
    public Camera getCamera() {
        return camera;
    }

    @Override
    public void setSessionView(SessionView view) {
        sessionFrag = view;
    }

    @Override
    public AppSettings getSettings() {
        return settings;
    }

    /**
     * 代码中动态注册广播
     */

    private void registerReceiver() {
        if (!mReceiverTag) {     //避免重复多次注册广播
            IntentFilter filter = new IntentFilter();
            filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
            registerReceiver(mUsbReceiver, filter);
            mReceiverTag = true;
        }
    }

    /**
     * 注销广播
     */

    private void unregisterReceiver() {
        if (mReceiverTag) {
            mReceiverTag = false;
            unregisterReceiver(mUsbReceiver);
        }

    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)&&ptp!=null) {
//                ptp = PtpService.Singleton.getInstance(MainActivity.this);
                //   ptp.setCameraListener(MainActivity.this);
                ptp.initialize(LiveActivity.this, getIntent());
            }
        }
    };

    private void showVibratorDialog() {
        String title = getResources().getString(R.string.tips);
        String content = getResources().getString(R.string.tips_content);
        CommonDialog dialog = new CommonDialog(this, title, content, 0);
        dialog.setOnConfirmListener(this);
        dialog.setOnCancelListener(this);
        dialog.show();
    }

    /**
     * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
     */
    private void startVibrator() {
        boolean isVibrate = SPUtils.getBooleanDefTrue(Contents.VIBRATE);
        if (isVibrate) {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {100, 400, 100, 400, 100, 400}; // 停止 开启 停止 开启
            vibrator.vibrate(pattern, -1); //重复两次上面的pattern 如果只想震动一次，index设为-1
        }
    }

    @Override
    public void confirm(int type) {
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    @Override
    public void cancel(int type) {
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeCard(ChangeCardEvent event) {
        if (event.isChangeCard()) {
            isChange = true;
        }
    }
}
