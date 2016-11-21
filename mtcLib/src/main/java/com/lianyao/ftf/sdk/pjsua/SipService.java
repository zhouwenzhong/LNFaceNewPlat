package com.lianyao.ftf.sdk.pjsua;

import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.AuthCredInfo;
import org.pjsip.pjsua2.AuthCredInfoVector;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.StringVector;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import android.content.Context;

import com.lianyao.ftf.sdk.config.Constants;
import com.lianyao.ftf.sdk.inter.SignalingCallBack;
import com.lianyao.ftf.sdk.inter.SignalingLayer;
import com.lianyao.ftf.sdk.uitl.MtcLog;

public class SipService implements SignalingLayer {
	// public static Endpoint ep = new Endpoint();
	// private TransportConfig sipTpConfig = new TransportConfig();
	static {
		System.loadLibrary("pjsua2");
	}
	public static MyApp app = null;
	public static MyCall currentCall = null;
	public static MyAccount account = null;
	public static AccountConfig accCfg = null;
	public SipLooper looper;
	public Context context = null;

	public SipService(Context context) {
		this.context = context;
		looper = new SipLooper();
		looper.requestStart();
	}

	@Override
	public boolean initialize(final SignalingCallBack signalingCallBack) {
		looper.execute(new Runnable() {

			@Override
			public void run() {
				if (app == null) {
					app = new MyApp();
					app.init(new SipObserver(signalingCallBack), context
							.getFilesDir().getAbsolutePath());
				}
				if (app.accList.size() == 0) {
					accCfg = new AccountConfig();
					accCfg.setIdUri("sip:localhost");
					accCfg.getNatConfig().setIceEnabled(false);
					accCfg.getVideoConfig().setAutoTransmitOutgoing(true);
					accCfg.getVideoConfig().setAutoShowIncoming(true);
					account = app.addAcc(accCfg);
				} else {
					account = app.accList.get(0);
					accCfg = account.cfg;
				}
			}
		});

		return true;
	}

	@Override
	public boolean registration(final String tid, final String password) {
		looper.execute(new Runnable() {

			@Override
			public void run() {
				String acc_id = "<sip:" + tid + "@" + Constants.SipServer
						+ ">";
				String registrar = "sip:" + Constants.SipServer;
				String proxy = "sip:" + Constants.SipServer;
				accCfg.setIdUri(acc_id);
				accCfg.getRegConfig().setRegistrarUri(registrar);
				AuthCredInfoVector creds = accCfg.getSipConfig().getAuthCreds();
				creds.clear();
				creds.add(new AuthCredInfo("Digest", "*", tid, 0, password));
				StringVector proxies = accCfg.getSipConfig().getProxies();
				proxies.clear();
				proxies.add(proxy);
				try {
					account.modify(accCfg);
				} catch (Exception e) {

				}

			}
		});
		return true;

	}
	
	@Override
	public boolean unRegistration(String tid, String password) {
		return true;
	}
	
	@Override
	public boolean makeCall(final String tid, boolean isVideo, boolean Audio) {
		looper.execute(new Runnable() {

			@Override
			public void run() {
				String buddy_uri = "<sip:" + tid + "@" + Constants.SipServer
						+ ">";
				MyCall call = new MyCall(account, -1);
				CallOpParam prm = new CallOpParam(true);
				prm.setOptions(1);
				// prm.setReason("1");
				// CallSetting callSetting = new CallSetting();
				// callSetting.setAudioCount(2);
				// callSetting.setFlag(2);
				// prm.setOpt(callSetting);
				try {
					call.makeCall(buddy_uri, prm);
				} catch (Exception e) {
					call.delete();
					MtcLog.e("registration", "makeCall" + e);
				}
			}
		});
		return true;
	}

	@Override
	public void sendIceCandidate(String tid, IceCandidate description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendDescription(String tid, SessionDescription description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCallReply(String tid) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onHangup(String tid) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConflict(String tid) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnswer(String tid, boolean isVideo, boolean Audio) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * 是否通话中
	 * @param tid
	 * @return
	 */
	@Override
	public boolean isCalling() {
		return false;
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getRegisterStatus() {
		return  true;
	}
}
