package com.lianyao.ftf.util;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;

/**
 * 跟App相关的辅助类
 */
public class AppUtil {

	/**
	 * 获取应用程序名称
	 */
	public static String getAppName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			int labelRes = packageInfo.applicationInfo.labelRes;
			return context.getResources().getString(labelRes);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * [获取应用程序版本名称信息]
	 * 
	 * @param context
	 * @return 当前应用的版本名称
	 */
	public static String getVersionName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			return packageInfo.versionName;

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getDeviceIMEI(Context context) {
	    String deviceId;
	    if (isPhone(context)) {
	        TelephonyManager telephony = (TelephonyManager) context
	                .getSystemService(Context.TELEPHONY_SERVICE);
	        deviceId = telephony.getDeviceId();
	    } else {
	        deviceId = Settings.Secure.getString(context.getContentResolver(),
	                Settings.Secure.ANDROID_ID);
	  
	    }
	    return deviceId;
	}
	
	public static boolean isPhone(Context context) {
	    TelephonyManager telephony = (TelephonyManager) context
	            .getSystemService(Context.TELEPHONY_SERVICE);
	    if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
	        return false;
	    } else {
	        return true;
	    }
	}

	@SuppressWarnings("deprecation")
	public static void wakeUpAndUnlock(Context context){  
        //获取电源管理器对象
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是调试用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
        //点亮屏幕
        wl.acquire();
        //释放  
//        wl.release(); 
        //得到键盘锁管理器对象
        KeyguardManager km= (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //解锁
        kl.disableKeyguard();
    }

	/**
	 * 判断网络是否可用
	 * @param context
	 * @return
	 */
	public static boolean isNetWorkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager)
				context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		if (networkInfo == null || !networkInfo.isAvailable()) {
			return false;
		}

		return true;
	}
}