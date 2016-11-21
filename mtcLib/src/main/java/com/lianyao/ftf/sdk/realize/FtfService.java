package com.lianyao.ftf.sdk.realize;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.lianyao.ftf.sdk.RtcClient;
import com.lianyao.ftf.sdk.inter.CalledObserver;
import com.lianyao.ftf.sdk.inter.InitListener;
import com.lianyao.ftf.sdk.inter.LoginCallback;
import com.lianyao.ftf.sdk.uitl.MtcLog;

import java.util.Timer;
import java.util.TimerTask;

public class FtfService extends Service {
	public static boolean isStart = false;
	public static boolean isLogin = false;
	public static String mobile = null;
	public static String token = null;
	private Timer mTimer;
	private static long time = 0;

	/**
	 * 初始化
	 * 
	 * 应用第一次启动(在LoginActivity line60 启动)
	 * 
	 * 应用第一次被杀死后调用(自动启动)
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		// ftfStart();
		MtcLog.e("FtfService", "FtfService>>" + this.toString());
		// login();
		if (mTimer == null) {
			mTimer = new Timer();
		}
		setTimerTask();
		Log.i("FtfService", "onCreate!");
	}

	// private void ftfStart() {
	// RtcCallMannger.getInstance().initialize(initCallBack,
	// FtfService.this,FileUtils.createRootPath());
	// }
	private void setTimerTask() {
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				Message message = new Message();
				message.what = 1;
				doActionHandler.sendMessage(message);
			}
		}, 200000, 200000);
	}

	@SuppressLint("HandlerLeak")
	private Handler doActionHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int msgId = msg.what;
			switch (msgId) {
				case 1:
					time ++;
					MtcLog.d("FtfService register : " + time);
					RtcClient.getInstance().login(new LoginCallback() {
						@Override
						public void onSussess() {

						}

						@Override
						public void onFailure(int state, String message) {

						}

						@Override
						public void onUpdateToken(String tInfo) {
						}
					});
//					if (FtfService.token != null && FtfService.mobile != null) {
//						time ++;
//						MtcLog.d("FtfService register : " + time);
//						RtcClient.getInstance().login(mobile, token, new LoginCallback() {
//							@Override
//							public void onSussess() {
//
//							}
//
//							@Override
//							public void onFailure(int state, String message) {
//
//							}
//
//							@Override
//							public void onUpdateToken(String tInfo) {
//							}
//						});
//					}
					break;
				default:
					break;
			}
		}
	};
	void login() {
		Log.d("FtfService", "isStart>>" + isStart);
		if (!isStart) {
			isStart = true;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		login();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return new LocalBinder();
	}

	public class LocalBinder extends Binder {
		public FtfService getService() {
			return FtfService.this;
		}
	}

	/**
	 * 初始化回调
	 */
	InitListener initCallBack = new InitListener() {

		@Override
		public void onSuccess() {
			Log.i("FtfService", "InitCallBack->onSuccess");
		}

		@Override
		public void onError(String message) {
			Log.i("FtfService", "InitCallBack->onFailure:" + message);
		}
	};
	CalledObserver calledObserver = new CalledObserver() {

		@Override
		public void onIncomingCall(String tid, boolean isAudio,
				boolean isVideo) {
			Log.i("FtfService", "InitCallBack->onIncomingCall:" + tid);
		}

		@Override
		public void onHangUp(String tid) {
			Log.i("FtfService", "InitCallBack->onHangUp:" + tid);
			Intent intent = new Intent();
			intent.setAction("onHangUp");
			sendBroadcast(intent);
		}
	};

}
