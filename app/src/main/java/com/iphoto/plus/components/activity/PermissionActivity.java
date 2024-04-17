package com.iphoto.plus.components.activity;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.iphoto.plus.R;
import com.iphoto.plus.base.BaseActivity;
import com.iphoto.plus.ptp.PtpConstants;

import java.util.Map;
public class PermissionActivity extends BaseActivity {

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private UsbManager mUsbManager;
    private TextView tvDevice;
    private String TAG = "rain";

    public BroadcastReceiver usbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("rain", action + "==action==");

            if (action.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                initPTP(context, intent);
            } else if (action.equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                tvDevice.setText("USB断开");
                Log.d("rain", "==============\nUSB断开\n=====================");
            }

        }
    };

    private void initPTP(Context context, Intent intent) {
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        // tvDevice.setText("USB连接");


        UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        if (device != null) {
//            registerPermissionReceiver(context);
//            PendingIntent mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(
//                    ACTION_USB_PERMISSION), 0);
//            mUsbManager.requestPermission(device, mPermissionIntent);
            //  connect(context, device);
            String deviceName = device.getDeviceName();
            int vendorId = device.getVendorId();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                String productName = device.getProductName();
                tvDevice.setText("设备名" + deviceName + "\n产品名字" + productName + "\n供应商Id" + vendorId);
            }
            Log.d(TAG, "initialize: got device through intent");
        } else {
            device = lookupCompatibleDevice(mUsbManager);
            if (device != null) {
                registerPermissionReceiver(context);
                PendingIntent mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(
                        ACTION_USB_PERMISSION), 0);
                mUsbManager.requestPermission(device, mPermissionIntent);
            }
        }
    }

    private boolean mReceiverTag;

    @Override
    protected void onStart() {
        super.onStart();

        initPTP(this,getIntent());

    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
        initPTP(this, intent);
    }

    private void init() {

        registerUsb();


    }

    private void registerUsb() {
        if (!mReceiverTag) {
            mReceiverTag = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
            filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
            registerReceiver(usbReceiver, filter);
        }
    }


    private void registerPermissionReceiver(Context context) {
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        context.registerReceiver(permissionReceiver, filter);
    }


    private UsbDevice lookupCompatibleDevice(UsbManager manager) {
        Map<String, UsbDevice> deviceList = manager.getDeviceList();
        for (Map.Entry<String, UsbDevice> e : deviceList.entrySet()) {
            UsbDevice d = e.getValue();
            if (d.getVendorId() == PtpConstants.CanonVendorId || d.getVendorId() == PtpConstants.NikonVendorId) {
                return d;
            }
        }
        return null;
    }

    private final BroadcastReceiver permissionReceiver = new BroadcastReceiver() {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                unregisterPermissionReceiver(context);

                synchronized (this) {

                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        connect(context, device);
                        String deviceName = device.getDeviceName();
                        int vendorId = device.getVendorId();
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            String productName = device.getProductName();
                            tvDevice.setText("设备名" + deviceName + "\n产品名字" + productName + "\n供应商Id" + vendorId);
                        }
                    } else {
                        //TODO report
                        Log.d("rain", "==没权限");
                    }
                }
            }
        }
    };
    private byte[] bytes;
    private static int TIMEOUT = 0;
    private boolean forceClaim = true;

    private void connect(Context context, UsbDevice device) {
        UsbInterface intf = device.getInterface(0);
        //  UsbEndpoint endpoint = intf.getEndpoint(0);

        UsbDeviceConnection connection = mUsbManager.openDevice(device);
        connection.claimInterface(intf, forceClaim);
        UsbEndpoint in = null;
        UsbEndpoint out = null;
        for (int e = 0, en = intf.getEndpointCount(); e < en; ++e) {
            UsbEndpoint endpoint = intf.getEndpoint(e);
            if (endpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                if (endpoint.getDirection() == UsbConstants.USB_DIR_IN) {
                    in = endpoint;
                } else if (endpoint.getDirection() == UsbConstants.USB_DIR_OUT) {
                    out = endpoint;
                }
            }
            Log.d("rain", endpoint.getType() + "==endpoint=="+ endpoint.getDirection() );
        }
        UsbEndpoint finalIn = in;
        UsbEndpoint finalOut = out;

        //  Log.d("rain", in + "==endpoint=="+ out);
        new Thread(new Runnable() {
            @Override
            public void run() {
                // int i = connection.bulkTransfer(endpoint, bytes, bytes.length, TIMEOUT);
                Log.d("rain", finalIn + "==endpoint=="+ finalOut);
            }
        });
    }

    /**
     * 注销广播
     */

    private void unregisterReceiver() {
        if (usbReceiver != null) {
            mReceiverTag = false;
            unregisterReceiver(usbReceiver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver();

    }

    private void unregisterPermissionReceiver(Context context) {

        if (permissionReceiver != null) {
            context.unregisterReceiver(permissionReceiver);
        }
    }


    @Override
    public int getLayoutId() {
        return R.layout.permission_activity;
    }

    @Override
    public void initView() {


    }

    @Override
    protected void initData() {

        tvDevice = findViewById(R.id.tv_device);

        init();
        tvDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


}