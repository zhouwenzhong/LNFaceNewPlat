package com.lianyao.ftf.sdk.inter;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

/**
 * 信令回调
 * 
 * @author guochao
 * 
 */
public interface SignalingCallBack {

	/**
	 * 来电话
	 * 
	 * @param tid
	 * @param isVideo
	 */
	public void onIncomingCall(final String tid, boolean isAudio,
			boolean isVideo);

	/**
	 * 呼叫
	 * 
	 * @param tid
	 */
	public void onCalling(final String tid);

	/**
	 * 对方接到 呼叫
	 * 
	 * @param tid
	 */
	public void onCallReply(final String tid);

	/**
	 * 占线/用户未登录/不存在
	 * 
	 * @param tid
	 */
	public void onCallFailure(final String tid, final int state);

	/**
	 * 接电话
	 * 
	 * @param tid
	 */
	public void onAnswerPhone(final String tid, boolean isVideo, boolean Audio);

	/**
	 * 挂电话
	 * 
	 * @param tid
	 */
	public void onHangUp(final String tid, final int state);

	/**
	 * Ice Candidate
	 * 
	 * @param tid
	 */
	public void onCandidate(final String tid, IceCandidate iceCandidate);

	/**
	 * SessionDescription
	 * 
	 * @param tid
	 */
	public void onSessionDescription(final String tid,
			SessionDescription sessionDescription);

	/**
	 * SessionDescription
	 * 
	 * @param callId
	 * @param name
	 */
	public void onRegistrationResults(final int state, final String msg);

}
