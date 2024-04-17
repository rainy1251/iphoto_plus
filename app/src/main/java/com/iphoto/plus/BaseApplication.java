/**
 * Copyright 2013 Nils Assbeck, Guersel Ayaz and Michael Zoech
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.iphoto.plus;


import android.content.Intent;

import androidx.multidex.MultiDexApplication;

import com.iphoto.plus.components.activity.MainActivity;
import com.iphoto.plus.util.SPUtils;
import com.tencent.bugly.crashreport.CrashReport;

public class BaseApplication extends MultiDexApplication {


    private static BaseApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        SPUtils.instance(getApplicationContext());
        CrashReport.initCrashReport(getApplicationContext(), "48f37500eb", false);
      //  Thread.setDefaultUncaughtExceptionHandler(restartHandler);
    }

    public static BaseApplication getInstance() {
        return instance;
    }

    // 创建服务用于捕获崩溃异常
    private Thread.UncaughtExceptionHandler restartHandler = (thread, ex) -> {
        restartApp();//发生崩溃异常时,重启应用
    };
    //重启App
    public void restartApp(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());  //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
    }
}
