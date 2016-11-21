package com.lianyao.ftf.sdk.inter;

/**
 * 被叫回调
 * 
 * @author guochao
 * 
 */
public interface CalledObserver {
	/**
	 * 来电话
	 * 
	 * @param callId
	 * @param name
	 * @param isVideo
	 * @param isAudio
	 */
	public void onIncomingCall(String tid, boolean isAudio, boolean isVideo);

	/**
	 * 未接通情况下挂断
	 * 
	 * @param callId
	 * @param name
	 */
	public void onHangUp(String tid);
}
