package com.lianyao.ftf.sdk;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.lianyao.ftf.sdk.config.CallState;
import com.lianyao.ftf.sdk.config.RtcBroadcast;
import com.lianyao.ftf.sdk.inter.CalledObserver;
import com.lianyao.ftf.sdk.inter.FTFCallback;
import com.lianyao.ftf.sdk.inter.InitListener;
import com.lianyao.ftf.sdk.inter.LoginCallback;
import com.lianyao.ftf.sdk.inter.RegisterCallBack;
import com.lianyao.ftf.sdk.inter.RtcConnectionListener;
import com.lianyao.ftf.sdk.layered.RtcCallManager;
import com.lianyao.ftf.sdk.layered.RtcSetting;
import com.lianyao.ftf.sdk.layered.RtcUser;
import com.lianyao.ftf.sdk.realize.FtfService;
import com.lianyao.ftf.sdk.realize.MainManager;
import com.lianyao.ftf.sdk.uitl.FileUtils;
import com.lianyao.ftf.sdk.uitl.MtcLog;
import com.lianyao.ftf.sdk.uitl.RtcSPUtil;
import com.lianyao.ftf.sdk.uitl.StringUtil;

/**
 * SDK实例
 * 
 * @author guochao
 * 
 */
public class RtcClient {
	public RtcSetting rtcSetting;
	private RtcUser rtcUser;
	private Context context;
	private MainManager mainMannger;
	private static RtcClient rtcClient = new RtcClient();
	private List<RtcConnectionListener> conListeners = new ArrayList<RtcConnectionListener>();
	private List<RtcCallManager> callManagers = new ArrayList<RtcCallManager>();
	private NetworkChangeReceive networkChangeReceive;
	private static boolean initialized = false;

	public String appId = "";
	public String appKey = "";// ticket
	private String mobile;
	private String token;

	private RtcClient() {
		rtcSetting = new RtcSetting();
		rtcUser = new RtcUser();
		mainMannger = MainManager.getInstance();
	}

	public static RtcClient getInstance() {
		return rtcClient;
	}

	/** 初始化SDK */
	public void init(Context context, String appId, String appKey,
			InitListener initCallBack) {
		this.appId = appId;
		this.appKey = appKey;
		this.context = context;
		// TODO 验证 appid appKey 失败调用initCallBack.onerror
		if (true) {
			mainMannger.initialize(context, initCallBack, calledObserver,
					FileUtils.createRootPath(context));
		}
		IntentFilter intentFilter = new IntentFilter();
        //addAction
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		networkChangeReceive = new NetworkChangeReceive();
        this.context.registerReceiver(networkChangeReceive, intentFilter);
		initialized = true;
	}

	/** 注册 */
	public void register(String username,
			final RegisterCallBack callback) {
		if(!initialized) {
			MtcLog.i("sdk未初始化");
			return;
		}
		FTFCallback callback2 = new FTFCallback() {
			@Override
			public void onResponse(String str, int code, String msg) {
				System.err.println(str);
				try {
					JSONObject json = new JSONObject(str);
					json = json.getJSONObject("rsc");
					if (code == 200) {
						String uid = json.getJSONObject("user").getString("uid");
						String tid = json.getJSONObject("user").getJSONObject("tm").getString("tid");
//						tid = getTidUid(tid);
						uid = json.getJSONObject("user").getString("uid");
						RtcSPUtil.put(context, "uid", uid);
						RtcSPUtil.put(context, "tid", tid);
						callback.onSuccess(json.getJSONObject("user"));
					} else {
						callback.onError(msg);
					}
				} catch (JSONException e) {
					callback.onError(e.toString());
				}
			}

			@Override
			public void onFailure(String err) {
				callback.onError(err);
			}
		};
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = tm.getDeviceId();
		rtcUser.FTF_Register(appId, username, appKey, imei, callback2);
	}

	/** 登录 */
	public void login(final LoginCallback loginCallback) {
		String mobile = (String)RtcSPUtil.get(context, "mobile", "");
		String token = (String)RtcSPUtil.get(context, "mobile", "");

		if(!StringUtil.isNullOrEmpety(mobile) && !StringUtil.isNullOrEmpety(token)) {
			login(mobile, token, loginCallback);
		}
	}

	/** 登录 */
	public void login(final String mobile, final String token,
			final LoginCallback loginCallback) {
		if(!initialized) {
			MtcLog.i("sdk未初始化");
			return;
		}
		this.mobile = mobile;
		this.token = token;
		MtcLog.d("Login, mobile:" + mobile + ",token:" + token);
		if("".equals(RtcSPUtil.get(context, "uid", ""))
				|| "".equals(RtcSPUtil.get(context, "tid", ""))
				|| !(appId+"_"+mobile).equals(RtcSPUtil.get(context, "uid", "").toString()) ) {
			register(mobile, new RegisterCallBack() {
				@Override
				public void onSuccess(JSONObject jsonObject) {
					String tid = RtcSPUtil.get(context, "tid", "").toString();
					tid = getTidUid(tid);
					String uid = RtcSPUtil.get(context, "uid", "").toString();
					uid = getTidUid(uid);
					try {
						RtcClient.this.token = jsonObject.getJSONObject("tm").getString("token");
					}catch (JSONException ex) {
						MtcLog.e(ex.toString());
					}
					login(uid, tid, RtcClient.this.token, loginCallback, 0);
				}

				@Override
				public void onError(String message) {
					Log.e("registe", "注册失败：" + message);
				}
			});
		} else {
			String tid = RtcSPUtil.get(context, "tid", "").toString();
			tid = getTidUid(tid);
			String uid = RtcSPUtil.get(context, "uid", "").toString();
			uid = getTidUid(uid);
			login(uid, tid, token, loginCallback, 0);
		}
		
		
	}
	
	/** 程序退出 */
	public void logOut() {
		String tid = RtcSPUtil.get(context, "tid", "").toString();
		tid = getTidUid(tid);
		FtfService.mobile = null;
		FtfService.token = null;
		mainMannger.logout(tid, token);
	}

	/** 程序退出后，再进行SDK释放操作 */
	public void release() {
//		initialized = false;
		// TODO 释放sip 和 webrtc
//		this.context.unregisterReceiver(networkChangeReceive);

		for (RtcCallManager callManager: callManagers) {
			callManager.release();
		}
		callManagers.clear();
		conListeners.clear();
	}

	/** 创建RtcCallManager实例 */
	public RtcCallManager getCallManager() {
		if(!initialized) {
			MtcLog.i("sdk未初始化");
			return null;
		}
		RtcCallManager callManager = new RtcCallManager();
		callManagers.add(callManager);
		return callManager;
	}

	CalledObserver calledObserver = new CalledObserver() {
		@Override
		public void onIncomingCall(String tid, boolean isAudio, boolean isVideo) {
			if(FtfService.mobile == null || FtfService.mobile.length() == 0)
			{
				return;
			}
			Intent intent = new Intent();
			intent.setAction(RtcBroadcast.onIncomingCall);
			// 来电接收到的还是tid，得改成mobile发送给app
			tid = StringUtil.getMobileFromUid(tid);
			intent.putExtra("mobile", tid);
			intent.putExtra("isAudio", isAudio);
			intent.putExtra("isVideo", isVideo);
			context.sendBroadcast(intent);
		}

		@Override
		public void onHangUp(String tid) {
			Intent intent = new Intent();
			intent.putExtra("tid", tid);
			intent.setAction(RtcBroadcast.onHangUp);
			context.sendBroadcast(intent);
		}
	};

	/**
	 * 登录
	 * 
	 * @param 手机号
	 * @param token
	 * @param token
	 * @param 登录回调
	 * @param 重复登录次数
	 */
	private void login(final String uid, final String tid, final String token,
			final LoginCallback loginCallback, final Integer times) {
		if(!initialized) {
			MtcLog.i("sdk未初始化");
			return;
		}
		if (times > 0) {
			loginCallback.onUpdateToken(token);
		}
		LoginCallback loginCallback2 = new LoginCallback() {

			@Override
			public void onUpdateToken(String tInfo) {

			}

			@Override
			public void onSussess() {
				MtcLog.i("test", "onSussess");
				FtfService.mobile = mobile;
				FtfService.token = token;
				RtcClient.this.token = token;
				RtcSPUtil.put(context, "mobile", mobile);
				RtcSPUtil.put(context, "token", token);
				loginCallback.onSussess();
			}

			@Override
			public void onFailure(int state, String message) {
				// TODO 判断错误类型
				MtcLog.i("test", "onFailure");
				// state == 445 &&
				if (times < 5) {
					refreshToken(uid, tid, token, times, loginCallback);
				} else {
					loginCallback.onFailure(state, message);
				}
			}
		};
		mainMannger.login(tid, token, loginCallback2);
	}

	private void refreshToken(String uid, String tid, String token,
			Integer times, final LoginCallback loginCallback) {
		MtcLog.i("test", "refreshToken");
		if(!initialized) {
			MtcLog.i("sdk未初始化");
			return;
		}
		final Integer nowTimes = times + 1;
		rtcUser.FTF_Refresh(uid, appId, tid, appKey, new FTFCallback() {
			@Override
			public void onResponse(String str, int code, String msg) {
				try {
					JSONObject object = new JSONObject(str)
							.getJSONObject("rsc");
					if (code == 200) {
						JSONObject user = object.getJSONObject("user");
						String uid = user.getString("uid");
						String tid = user.getJSONObject("tm").getString("tid");
						String token = user.getJSONObject("tm").getString(
								"token");
						login(uid, tid, token, loginCallback, nowTimes);
					} else {
						loginCallback.onFailure(code,
								msg);
					}
				} catch (JSONException e) {
					// TODO 错误状态码
					loginCallback.onFailure(405, e.toString());
				}
			}

			@Override
			public void onFailure(String err) {
				// TODO 错误状态码
				loginCallback.onFailure(405, err);
			}
		});
	}
	
	public void addConnectionListener(RtcConnectionListener listener) {
		conListeners.add(listener);
	}

	private static NetworkInfo activeNetwork = null;

	class NetworkChangeReceive extends BroadcastReceiver {
		public NetworkChangeReceive() {
			super();
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager connectivityManager = (ConnectivityManager)
					context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			if(activeNetwork == null) {
				activeNetwork = networkInfo;
			}

			if (networkInfo != null && networkInfo.isAvailable() && (activeNetwork.getType() != networkInfo.getType())) {
				activeNetwork = networkInfo;

				Log.i("network", "网络连接上。。。");
				if(!initialized) {
					MtcLog.i("sdk未初始化");
					return;
				}

				String tid = RtcSPUtil.get(context, "tid", "").toString();
				tid = getTidUid(tid);
				String uid = RtcSPUtil.get(context, "uid", "").toString();
				uid = getTidUid(uid);
				if(tid != null && tid.length() > 0) {
					login(uid, tid, RtcClient.this.token, new LoginCallback() {
						@Override
						public void onUpdateToken(String tInfo) {
						}
						@Override
						public void onSussess() {
						}
						@Override
						public void onFailure(int state, String message) {
						}
					}, 0);
				}

				// 网络可用
				for (RtcConnectionListener listener : conListeners) {
					MtcLog.e("网络连接上了");
					listener.onConnected();
				}
			} else if(networkInfo == null || !networkInfo.isAvailable()){
				// 网络不可用
				for (RtcConnectionListener listener : conListeners) {
					MtcLog.e("网络已断开");
					listener.onDisconnected(CallState.NETWORK_UNSTABLE.value);
				}
			}
		}
	}


	// type 0标识uid， 1标识tid。 正式时候直接return id就行了
	private String getTidUid(String id) {
//		String mobile = id.split("_")[0];
//		id = id.replaceAll(appId + "_" + mobile, mobile + "_" + appId);
		return id;
	}

	/**
	 * 判断sdk是否初始化
	 * @return
	 */
	public static boolean isInited() {
		return initialized;
	}
}
