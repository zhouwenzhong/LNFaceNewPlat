package com.lianyao.ftf.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.lianyao.ftf.receiver.RtcReceiver;
import com.lianyao.ftf.sdk.config.RtcBroadcast;
import com.lianyao.ftf.sdk.fork.NativeRuntime;
import com.lianyao.ftf.sdk.uitl.FileUtils;

/**
 * Created by zhouwz on 16/7/22.
 */
public class AutoStartService extends Service {

    public AutoStartService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.e("AutoStartService", "service start!");
        (new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    NativeRuntime.startService(getPackageName() + "/"
                                    + "com.lianyao.ftf.sdk.realize.FtfService",
                            FileUtils.createRootPath(AutoStartService.this));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        })).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
