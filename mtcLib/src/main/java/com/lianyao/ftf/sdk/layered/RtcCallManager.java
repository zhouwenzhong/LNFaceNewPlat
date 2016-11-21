package com.lianyao.ftf.sdk.layered;

import android.app.Activity;

import com.lianyao.ftf.sdk.RtcClient;
import com.lianyao.ftf.sdk.inter.CallStateListener;
import com.lianyao.ftf.sdk.inter.CallStatsObserver;
import com.lianyao.ftf.sdk.realize.CallParameter;
import com.lianyao.ftf.sdk.realize.MainManager;
import com.lianyao.ftf.sdk.uitl.StringUtil;

import org.webrtc.EglBase;
import org.webrtc.SurfaceViewRenderer;

import java.util.UUID;

public class RtcCallManager {
	private RtcVideoCallHelper callHelper;
	private MainManager mainMannger;
	private CallParameter callParameter;
	private String callId = "";
	private String userName;

	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCallId() {
		return callId;
	}

	public void setCallId(String callId) {
		this.callId = callId;
	}

	public RtcCallManager() {
		callHelper = new RtcVideoCallHelper();
		mainMannger = MainManager.getInstance();
		callId = UUID.randomUUID().toString();
	}

	public void setLocalSurfaceView(SurfaceViewRenderer localRender) {
		EglBase base = EglBase.create();
		callParameter = mainMannger.getCallParameter();
		if (callParameter == null) {
			callParameter = new CallParameter();
		}
		callParameter.setRenderEGLContext(base.getEglBaseContext());
		callParameter.setLocalRender(localRender);
	}

	public void setRemoteSurfaceView(SurfaceViewRenderer remoteRender) {
		callParameter.setItemVideoParameter(true, remoteRender);
	}

	public void addCallStateListener(CallStateListener callStateListener) {
		mainMannger.addCallStateListener(callStateListener);
	}

	public void makeCall(String username, Activity activity) {
		if(StringUtil.isNullOrEmpety(username)) {
			mainMannger.onEmptyDisconnected();
			return;
		}
		// 接收的是手机号，手动给加上appid，变成uid
		this.userName = username;
		callParameter.setActivity(activity);
		callParameter.setAudio(true);
		callParameter.setVideo(true);
		callParameter.setUserName(getWrapUserName(username));
		mainMannger.makeCall(callParameter);
	}

	public void makeAudioCall(String username, Activity activity) {
		if(StringUtil.isNullOrEmpety(username)) {
			mainMannger.onEmptyDisconnected();
			return;
		}
		// 接收的是手机号，手动给加上appid，变成uid
		this.userName = username;
		callParameter.setAudio(true);
		callParameter.setVideo(false);
		callParameter.setActivity(activity);
		callParameter.setUserName(getWrapUserName(username));
		mainMannger.makeCall(callParameter);
	}

	public void makeVideoCall(String username, Activity activity) {
		if(StringUtil.isNullOrEmpety(username)) {
			mainMannger.onEmptyDisconnected();
			return;
		}
		// 接收的是手机号，手动给加上appid，变成uid
		this.userName = username;
		callParameter.setAudio(false);
		callParameter.setVideo(true);
		callParameter.setActivity(activity);
		callParameter.setUserName(getWrapUserName(username));
		
		mainMannger.makeCall(callParameter);
	}

	public void answerCall(Activity activity) {
		callParameter.setAudio(true);
		callParameter.setVideo(true);
		callParameter.setActivity(activity);

		callParameter.setUserName(getWrapUserName(userName));
		
		mainMannger.answer(callParameter, getWrapUserName(userName));
	}

	public void answerAudioCall(Activity activity) {
		callParameter.setAudio(true);
		callParameter.setVideo(false);
		callParameter = mainMannger.getCallParameter();
		callParameter.setActivity(activity);
		callParameter.setUserName(getWrapUserName(userName));
		
		mainMannger.answer(callParameter, getWrapUserName(userName));
	}

	public void answerVideoCall(Activity activity) {
		callParameter.setAudio(false);
		callParameter.setVideo(true);
		callParameter.setActivity(activity);
		callParameter.setUserName(getWrapUserName(userName));
		
		mainMannger.answer(callParameter, getWrapUserName(userName));
	}

	public void mute() {

	}

	public void refuse() {
		stopAllCall();
	}

	public void release() {
		mainMannger.release();
	}
	
	public void stopCall(String username) {
		mainMannger.hangup(getWrapUserName(username));
	}

	public void stopAllCall() {
		if (callParameter != null) {
			mainMannger.hangup(callParameter.getUserName());
		}
	}

	public RtcVideoCallHelper getVideoCallHelper() {
		return callHelper;
	}

	public String getWrapUserName(String userName) {
		return StringUtil.getUidFromMobile(userName, RtcClient.getInstance().appId);
	}
	
	public void addCallStatsObserver(CallStatsObserver callStatsObserver){
		mainMannger.setCallStatsObserver(callStatsObserver, getWrapUserName(this.userName));
	}

	public boolean getRegisterStatus() {
		return mainMannger.getRegisterStatus();
	}
}
