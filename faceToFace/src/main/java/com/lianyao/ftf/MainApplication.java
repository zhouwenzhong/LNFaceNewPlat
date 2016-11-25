package com.lianyao.ftf;

import android.app.Application;
import android.content.IntentFilter;
import android.util.Log;

import com.lianyao.ftf.config.Constants;
import com.lianyao.ftf.receiver.RtcReceiver;
import com.lianyao.ftf.sdk.RtcClient;
import com.lianyao.ftf.sdk.config.RtcBroadcast;
import com.lianyao.ftf.sdk.inter.InitListener;
import com.lianyao.ftf.sdk.inter.RtcConnectionListener;
import com.lianyao.ftf.util.AppUtil;
import com.lianyao.ftf.util.ToastUtil;

public class MainApplication extends Application {
	RtcReceiver mReceiver = new RtcReceiver();

	@Override
	public void onCreate() {
		super.onCreate();
		//来电广播处理
		IntentFilter intentFilter = new IntentFilter();
		//addAction
		intentFilter.addAction(RtcBroadcast.onIncomingCall);
		registerReceiver(mReceiver, intentFilter);

		if(!AppUtil.isNetWorkAvailable(MainApplication.this.getApplicationContext())) {
			ToastUtil.showShort(this.getApplicationContext(), "请检查网络连接！");
			return;
		}

		initSdk();
	}

	@Override
	public void onTerminate(){
		super.onTerminate();
		unregisterReceiver(mReceiver);
	}

	public void initSdk() {

		final RtcClient client = RtcClient.getInstance();
		client.init(this, Constants.APPID, Constants.TICKET,
				new InitListener() {

					@Override
					public void onSuccess() {

					}

					@Override
					public void onError(String message) {

					}
				});
		client.rtcSetting.setRing(true, R.raw.call_bell);
		client.rtcSetting.setCallRing(true, R.raw.phone_ring);

		client.addConnectionListener(new RtcConnectionListener() {

			@Override
			public void onDisconnected(int error) {
				Log.e("MainApplication","网络断开。");
			}

			@Override
			public void onConnected() {
				Log.e("MainApplication","网络连接上。");
			}

		});
	}
}
