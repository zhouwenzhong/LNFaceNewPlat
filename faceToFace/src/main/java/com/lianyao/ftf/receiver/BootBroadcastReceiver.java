package com.lianyao.ftf.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lianyao.ftf.service.AutoStartService;
import com.lianyao.ftf.util.Logger;

/**
 * Created by zhouwz on 16/6/8.
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
            Intent intent1 = new Intent(context, AutoStartService.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(intent1);
            Logger.i("Service start");
        }
    }
}
