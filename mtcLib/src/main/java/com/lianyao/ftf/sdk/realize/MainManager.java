package com.lianyao.ftf.sdk.realize;

/**
 * 
 * 通讯流程
 * 
 * 1,A创建一个 RTCPeerConnection对象.
 *
 * 2,A创建一个offer(即SDP会话描述)通过RTCPeerConnection createOffer()方法.
 *
 * 3,A调用setLocalDescription()方法用他的offer.
 *
 * 4,A通过信令机制将他的offer发给Eve.
 *
 * 5,B调用setRemoteDescription()方式设置A的offer,因此他的RTCPeerConnection知道了A的设置.
 *
 * 6,B调用方法createAnswer(),然后会触发一个callback,这个callback里面可以去到自己的answer.
 *
 * 7,B设置他自己的anser通过调用方法setLocalDescription().
 *
 * 8,B通过信令机制将他的anser发给A.
 *
 * 9,A设置B的anser通过方法setRemoteDescription().
 * 
 */
import android.content.Context;

import com.lianyao.ftf.sdk.config.CallError;
import com.lianyao.ftf.sdk.config.CallState;
import com.lianyao.ftf.sdk.config.CallStats;
import com.lianyao.ftf.sdk.csipsimple.CsipService;
import com.lianyao.ftf.sdk.csipsimple.CsipSession;
import com.lianyao.ftf.sdk.fork.NativeRuntime;
import com.lianyao.ftf.sdk.inter.CallStateListener;
import com.lianyao.ftf.sdk.inter.CallStatsObserver;
import com.lianyao.ftf.sdk.inter.CalledObserver;
import com.lianyao.ftf.sdk.inter.InitListener;
import com.lianyao.ftf.sdk.inter.LoginCallback;
import com.lianyao.ftf.sdk.inter.SignalingCallBack;
import com.lianyao.ftf.sdk.inter.SignalingLayer;
import com.lianyao.ftf.sdk.inter.StatusObserver;
import com.lianyao.ftf.sdk.layered.RtcDevCheckManager;
import com.lianyao.ftf.sdk.uitl.MtcLog;
import com.lianyao.ftf.sdk.webrtc.StaticParameter;
import com.lianyao.ftf.sdk.webrtc.WebrtcClient;
import com.lianyao.ftf.sdk.webrtc.WebrtcEvents;
import com.lianyao.ftf.sdk.webrtc.WebrtcParameters;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;

import java.util.HashMap;
import java.util.Map;

/**
 * 通讯相关
 * 
 * @author guochao
 * 
 */
public final class MainManager implements SignalingCallBack, WebrtcEvents {

	/**
	 * 两个log标识
	 */
	private final String TAG = "MtcServer";
	private final String WEBRTCSTEP = "WEBRTCSTEP";

	public static MainManager getInstance() {
		if (mainService == null) {
			mainService = new MainManager();
		}
		return mainService;
	}

	/**
	 * 初始化
	 * 
	 * @param callBack
	 * @param context
	 */
	public void initialize(Context context, InitListener callBack,
			CalledObserver calledObserver, String cache) {
		NativeRuntime.startService(context.getPackageName()
				+ "/com.lianyao.ftf.sdk.realize.FtfService", cache);
		this.context = context;
		this.calledObserver = calledObserver;
//		if (signalingLayer == null) {
			signalingLayer = new CsipService(context);
			signalingLayer.initialize(this);
//		}
		callBack.onSuccess();
	}

	/**
	 * 登录
	 * 
	 * @param user
	 * @param pwd
	 * @return
	 */
	public boolean login(String user, String pwd, LoginCallback loginCallback) {
		this.loginCallback = loginCallback;
		if (signalingLayer.registration(user, pwd)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 注销
	 * @return
     */
	public boolean logout(String user, String pwd) {
		if (signalingLayer.unRegistration(user, pwd)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 呼叫
	 * 
	 */
	public void makeCall(CallParameter callParameter) {
		if(!RtcDevCheckManager.checkCameraHardware(context)) {
			callParameter.setVideo(false);
		}
		this.callParameter = callParameter;
		// STEP 主叫方 步骤1 创建RTCPeerConnection
		if (signalingLayer.makeCall(callParameter.getUserName(),
				callParameter.isAudio(), callParameter.isVideo())) {
//			webrtcClient = new WebrtcClient(new WebrtcParameters(context,
//					CallHelper.videoCodec, CallHelper.audioCodec));
//			webrtcClient.addLocal(callParameter.getLocalRender(),
//					callParameter.isAudio(), callParameter.isVideo());
//			webrtcClient.addEvents(this);
//			this.callParameter.isCallInitiator = true;
//			webrtcClient.addRemote(callParameter.getUserName(),
//					callParameter.getRemoteRender(), callParameter.isAudio(),
//					callParameter.isVideo());
		}else {
			callStateListener.onCallStateChanged(CallState.DISCONNNECTED, CallError.ERROR_BUSY);
			hangup(callParameter.getUserName());
		}
	}

	/**
	 * 挂断
	 * 
	 * @return
	 */
	public void hangup(String tid) {
		helper.endComeCall();
		callParameter = null;
		signalingLayer.onHangup(tid);
		rtcDispose();
	}

	public void release() {
		helper.endComeCall();
		callParameter = null;
		signalingLayer.release();
		rtcDispose();
	}

	/**
	 * 接电话
	 */
	public void answer(CallParameter callParameter, String tid) {
		helper.endComeCall();
		// STEP 被叫方 步骤1 创建RTCPeerConnection
		callParameter.isCallInitiator = false;
		this.callParameter = callParameter;
		webrtcClient = new WebrtcClient(new WebrtcParameters(context,
				CallHelper.videoCodec, CallHelper.audioCodec));
		if(!RtcDevCheckManager.checkCameraHardware(context)) {
			callParameter.setVideo(false);
		}
		webrtcClient.addLocal(callParameter.getLocalRender(),
				callParameter.isAudio(), callParameter.isVideo());
		webrtcClient.addEvents(this);
		webrtcClient.addRemote(callParameter.getUserName(),
				callParameter.getRemoteRender(), callParameter.isAudio(),
				callParameter.isVideo());
		signalingLayer.onAnswer(tid, callParameter.isAudio(),
				callParameter.isVideo());
		if (callStateListener != null) {
			callStateListener.onCallStateChanged(CallState.ANSWER_CALL, null);
		}			
		if (webrtcClient != null && callStatsObserver != null) {
			webrtcClient.enableStatsEvents(true, STATS_PERIOD, tid);
		}
	}

	/**
	 * 释放资源
	 */
	public void dispose() {
		sipDispose();
		rtcDispose();
	}

	public void sipDispose() {
		// TODO 清除sip
	}

	public void rtcDispose() {
		helper.endComeCall();
		callStateListener = null;
		statsObserver = null;
		callStatsObserver = null;
		if (webrtcClient != null) {
			webrtcClient.dispose();
			webrtcClient = null;
		}
		// if (peerConnectionClient != null)
		// peerConnectionClient.close();

	}

	@Override
	public void onIncomingCall(String tid, boolean isAudio, boolean isVideo) {
		if (callParameter == null) {
			helper.beganComeCall(context);
			callParameter = new CallParameter();
			callParameter.setAudio(isAudio);
			callParameter.setVideo(isVideo);
			if(!RtcDevCheckManager.checkCameraHardware(context)) {
				callParameter.setVideo(false);
			}
			callParameter.setUserName(tid);
			calledObserver.onIncomingCall(tid, isAudio, isVideo);
			signalingLayer.onCallReply(tid);
		} else {
			signalingLayer.onConflict(tid);
		}

	}

	@Override
	public void onHangUp(String tid, final int state) {
		// TODO 这有问题--正在通话中 有人打进来时
		callParameter = null;
		if (callStateListener != null) {
			callStateListener.onCallStateChanged(CallState.DISCONNNECTED, null);
			callStateListener = null;
		} else {
			calledObserver.onHangUp(tid);
		}
		hangup(tid);
	}

	@Override
	public void onAnswerPhone(String tid, boolean isVideo, boolean isAudio) {
		helper.endComeCall();
		if (callParameter != null && callParameter.isCallInitiator) {
			callParameter.setAudio(isAudio);
			callParameter.setVideo(isVideo);
			if(!RtcDevCheckManager.checkCameraHardware(context)) {
				callParameter.setVideo(false);
			}
			callParameter.setUserName(tid);
			// STEP 主叫方 步骤2 创建一个offer
			MtcLog.i(WEBRTCSTEP, "主叫方 步骤2 创建一个offer");
			webrtcClient.createOffer();
			if (callStateListener != null) {
				callStateListener.onCallStateChanged(CallState.ANSWER_CALL,
						null);
			}
		}

	}

	@Override
	public void onCandidate(final String tid, final IceCandidate candidate) {
		if(callParameter != null && callParameter.getActivity() != null) {
			callParameter.getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (webrtcClient == null) {
						MtcLog.i(WEBRTCSTEP,
								"peerConnectionClient是null值，addRemoteIceCandidate中断");
					} else {
						webrtcClient.addRemoteIceCandidate(candidate, tid);
					}
				}
			});
		}
	}

	@Override
	public void onSessionDescription(final String tid,
			final SessionDescription sdp) {
		if (callParameter == null) {
			// STEP 主叫方 步骤5 setRemoteDescription 设置被叫方发过来的session
			MtcLog.i(WEBRTCSTEP, "主叫方 步骤5 setRemoteDescription callParameter为空");
			return;
		}
		if (callParameter != null && callParameter.isCallInitiator) {
			// STEP 主叫方 步骤5 setRemoteDescription 设置被叫方发过来的session
			MtcLog.i(WEBRTCSTEP,
					"主叫方 步骤5 setRemoteDescription 设置被叫方发过来的session");
		} else {
			// STEP 被叫方 步骤2 setRemoteDescription 设置主叫方发过来的session
			MtcLog.i(WEBRTCSTEP,
					"被叫方 步骤2 setRemoteDescription 设置主叫方发过来的session");
		}
		callParameter.getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				webrtcClient.setRemoteDescription(sdp, tid);
				if (!callParameter.isCallInitiator) {
					// STEP 被叫方 步骤3 createAnswer()
					MtcLog.i(WEBRTCSTEP, "被叫方 步骤3 createAnswer()");
					webrtcClient.createAnswer(tid);
				}
			}
		});

	}

	@Override
	public void onCallFailure(String tid, int state) {
		if (callStateListener != null) {
			if (CallError.ERROR_BUSY.value.equals(state)
					&& CsipSession.getCallSate(tid) != null) {
				if(CsipSession.getCallSate(tid).name()
						.equals(CsipSession.State.incoming.name())) {

				}else {
					callStateListener.onCallStateChanged(null,
							CallError.valueToCode(state));
					helper.endComeCall();
					callParameter = null;
					signalingLayer.onConflict(tid);
					rtcDispose();
				}
			}else{
				callStateListener.onCallStateChanged(null,
						CallError.valueToCode(state));
				hangup(tid);
			}
		}
	}

	@Override
	public void onCallReply(String tid) {
		if (callStateListener != null) {
			webrtcClient = new WebrtcClient(new WebrtcParameters(context,
					CallHelper.videoCodec, CallHelper.audioCodec));
			if(!RtcDevCheckManager.checkCameraHardware(context)) {
				callParameter.setVideo(false);
			}
			webrtcClient.addLocal(callParameter.getLocalRender(),
					callParameter.isAudio(), callParameter.isVideo());
			webrtcClient.addEvents(this);
			this.callParameter.isCallInitiator = true;
			webrtcClient.addRemote(callParameter.getUserName(),
					callParameter.getRemoteRender(), callParameter.isAudio(),
					callParameter.isVideo());
			callStateListener.onCallStateChanged(CallState.CONNECTED, null);

			if (webrtcClient != null && callStatsObserver != null) {
				webrtcClient.enableStatsEvents(true, STATS_PERIOD, tid);
			}
		}
		if (helper != null) {
			helper.beganCall(context);
		}
	}

	@Override
	public void onCalling(String tid) {
		if (callStateListener != null) {
			callStateListener.onCallStateChanged(CallState.CONNECTING, null);
		}
	}

	@Override
	public void onLocalDescription(SessionDescription sdp, String tid) {
		if (callParameter != null && callParameter.isCallInitiator) {
			// STEP 主叫方 步骤4 信令机制将他的offer发给被叫方.
			MtcLog.i(WEBRTCSTEP, "主叫方 步骤4 信令机制将他的offer发给被叫方");
		} else {
			// STEP 被叫方 步骤5 信令机制将他的anser发给被叫方.
			MtcLog.i(WEBRTCSTEP, "被叫方 步骤5 信令机制将他的anser发给被叫方");
		}
		signalingLayer.sendDescription(tid, sdp);
	}

	@Override
	public void onIceCandidate(IceCandidate candidate, String tid) {
		MtcLog.d(TAG, "onIceCandidate" + candidate);
		signalingLayer.sendIceCandidate(tid, candidate);
	}

	@Override
	public void onIceConnected(String tid) {
		MtcLog.d(TAG, "onIceConnected");
		if (callStateListener != null) {
			callStateListener.onCallStateChanged(CallState.ACCEPTED, null);
		}
	}

	@Override
	public void onIceDisconnected(String tid) {
		MtcLog.d(TAG, "onIceDisconnected");
		if (callStateListener != null) {
			callStateListener.onCallStateChanged(CallState.DISCONNNECTED, null);
			hangup(tid);
			callStateListener = null;
		}
	}

	@Override
	public void onPeerConnectionClosed(String tid) {
		MtcLog.d(TAG, "onPeerConnectionClosed");

	}

	@Override
	public void onPeerConnectionStatsReady(StatsReport[] reports, String tid) {
		if (callStatsObserver == null) {
			return;
		}
		
		//发送和接收的帧率
		String fpsSent = null;
		String fpsRecv = null;
		//发送和接收的带宽
		String bandwidthSent = null;
		String bandwidthRecv = null;
		//发送和接收的宽高
		String sendFrameWidth = null;
		String sendFrameHeight = null;
		String recvFrameWidth = null;
		String recvFrameHeight = null;
		//发送和接收所丢的包的数量
		String packetsLostSent = null;
		String packetsLostRecv = null;
		//发送和接收的总包数量
		String packetsSent = null;
		String packetsRecv = null;
		CallStats callStats = new CallStats();


		for (StatsReport report : reports) {
			if (report.type.equals("ssrc") && report.id.contains("ssrc")
					&& report.id.contains("send")) {
				Map<String, String> reportMap = getReportMap(report);
				String trackId = reportMap.get("googTrackId");
				if (trackId != null
						&& trackId.contains(StaticParameter.VIDEO_TRACK_ID)) {
					fpsSent = reportMap.get("googFrameRateSent");
					packetsLostSent = reportMap.get("packetsLost");
					packetsSent = reportMap.get("packetsSent");
					sendFrameWidth = reportMap.get("googFrameWidthSent");
					sendFrameHeight = reportMap.get("googFrameHeightSent");
				}
			} else if (report.type.equals("ssrc") && report.id.contains("ssrc")
					&& report.id.contains("recv")) {
				Map<String, String> reportMap = getReportMap(report);
				String frameWidth = reportMap.get("googFrameWidthReceived");
				if (frameWidth != null) {
					fpsRecv = reportMap.get("googFrameRateReceived");
					packetsLostRecv = reportMap.get("packetsLost");
					packetsRecv = reportMap.get("packetsReceived");
					recvFrameWidth = reportMap.get("googFrameWidthReceived");
					recvFrameHeight = reportMap.get("googFrameHeightReceived");
				}
			} else if (report.id.equals("bweforvideo")) {
				Map<String, String> reportMap = getReportMap(report);
				bandwidthSent = reportMap.get("googAvailableSendBandwidth");
				bandwidthRecv = reportMap.get("googAvailableReceiveBandwidth");
			} else if (report.type.equals("googCandidatePair")) {

			}

		}
		
		callStats.setFpsRecv(fpsRecv);
		callStats.setFpsSent(fpsSent);
		callStats.setBandwidthRecv(bandwidthRecv);
		callStats.setBandwidthSent(bandwidthSent);
		callStats.setRecvFrameHeight(recvFrameHeight);
		callStats.setRecvFrameWidth(recvFrameWidth);
		callStats.setSendFrameHeight(sendFrameHeight);
		callStats.setSendFrameWidth(sendFrameWidth);
		callStats.setPacketsRecv(packetsRecv);
		callStats.setPacketsSent(packetsSent);
		callStats.setPacketsLostRecv(packetsLostRecv);
		callStats.setPacketsLostSent(packetsLostSent);
//		MtcLog.v("Call stats fpsRecv :" + fpsRecv);
//		MtcLog.v("Call stats fpsSent :" + fpsSent);
//		MtcLog.v("Call stats bandwidthRecv :" + bandwidthRecv);
//		MtcLog.v("Call stats bandwidthSent :" + bandwidthSent);
//		MtcLog.v("Call stats sendFrameHeight :" + sendFrameHeight);
//		MtcLog.v("Call stats sendFrameWidth :" + sendFrameWidth);
//		MtcLog.v("Call stats packetsRecv :" + packetsRecv);
//		MtcLog.v("Call stats packetsSent :" + packetsSent);
//		MtcLog.v("Call stats packetsLostRecv :" + packetsLostRecv);
//		MtcLog.v("Call stats packetsLostSent :" + packetsLostSent);
		
		callStatsObserver.onCallStats(callStats);
	}

	private Map<String, String> getReportMap(StatsReport report) {
		Map<String, String> reportMap = new HashMap<String, String>();
		for (StatsReport.Value value : report.values) {
			reportMap.put(value.name, value.value);
		}
		return reportMap;
	}

	@Override
	public void onPeerConnectionError(String description, String tid) {
		if (callStateListener != null) {
			callStateListener.onCallStateChanged(null,
					CallError.ERROR_TRANSPORT);
			hangup(tid);
		}
		MtcLog.e(TAG, "onPeerConnectionError:" + description);
	}

	public void onEmptyDisconnected() {
		MtcLog.d(TAG, "onEmptyDisconnected");
		if (callStateListener != null) {
			callStateListener.onCallStateChanged(CallState.DISCONNNECTED, null);
			callStateListener = null;
		}
	}

	/**
	 * 设置扬声器
	 * 
	 * @param flag
	 */
	public void setSpeaker(boolean flag) {
		helper.setSpeaker(flag, context);
	}

	/**
	 * 切换摄像头
	 */
	public void switchCamera() {
		if(signalingLayer.isCalling()) {
			webrtcClient.switchCamera();
		}
	}

	public void setLocalVideoEnabled(boolean enable) {
		if(signalingLayer.isCalling()) {
			webrtcClient.setLocalVideoEnabled(enable);
		}
	}

	public void setLocalAudioEnabled(boolean enable) {
		if(signalingLayer.isCalling()) {
			webrtcClient.setLocalAudioEnabled(enable);
		}
	}

	@Override
	public void onRegistrationResults(int state, String msg) {
		if (msg.equals("OK")) {
			loginCallback.onSussess();
		} else {
			loginCallback.onFailure(state, msg);
		}
	}

	public void setStatsObserver(int periodMs, StatusObserver observer) {
		if (observer != null && periodMs > 0) {
			this.statsObserver = observer;
			// peerConnectionClient.enableStatsEvents(true, periodMs);
		}
	}

	public void setCallStatsObserver(CallStatsObserver observer, String tid) {
		this.callStatsObserver = observer;
	}
	
	public void addCallStateListener(CallStateListener callStateListener) {
		this.callStateListener = callStateListener;
	}

	public boolean getRegisterStatus() {
		return signalingLayer.getRegisterStatus();
	}

	public CallParameter getCallParameter() {
		return callParameter;
	}

	private static MainManager mainService;
	private CallHelper helper = new CallHelper();
	private LoginCallback loginCallback;
	private CallStateListener callStateListener;
	private CallStatsObserver callStatsObserver;
	private final static int STATS_PERIOD = 1000;
	private StatusObserver statsObserver;
	private Context context;
	private SignalingLayer signalingLayer;

	private WebrtcClient webrtcClient;
	private CallParameter callParameter;
	private CalledObserver calledObserver;

	private MainManager() {
	}
}
