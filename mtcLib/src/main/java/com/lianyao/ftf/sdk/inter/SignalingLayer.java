package com.lianyao.ftf.sdk.inter;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

/**
 * 信令层接口
 * 
 * @author guochao
 * 
 */
public interface SignalingLayer {

	/**
	 * 初始化
	 * 
	 * @param 信令层回调
	 * @return 本地初始化结果
	 */
	boolean initialize(SignalingCallBack signalingCallBack);

	/**
	 * 
	 * 注册
	 * 
	 * @param 手机号
	 * @param 密码
	 * @return 本地注册结果,服务器返回结果在回调中
	 */
	boolean registration(String tid, String password);

	/**
	 *
	 * 注销
	 *
	 * @param 手机号
	 * @param 密码
	 * @return 本地注销
	 */
	boolean unRegistration(String tid, String password);

	/**
	 * 呼叫
	 * 
	 * @param 手机号
	 * @param 视频
	 * @param 音频
	 * @return 本地拨打结果,是否拨打成功在回调中
	 */
	boolean makeCall(String tid, boolean isVideo, boolean Audio);

	/**
	 * 
	 * @param 手机号
	 * @param webrtcICE信息
	 */
	void sendIceCandidate(String tid, IceCandidate description);

	/**
	 * 
	 * @param 手机号
	 * @param webrtcSDP信息
	 */
	void sendDescription(String tid, SessionDescription description);

	/**
	 * 响应打电话
	 * 
	 * @param 手机号
	 */
	void onCallReply(String tid);

	/**
	 * 接电话
	 * 
	 * @param 手机号
	 */
	void onAnswer(String tid, boolean isVideo, boolean Audio);

	/**
	 * 挂电话
	 * 
	 * @param 手机号
	 */
	void onHangup(String tid);

	
	public void release();
	
	/**
	 * 正在通话中
	 * 
	 * @param 手机号
	 */
	void onConflict(String tid);

	/**
	 * 是否通话中
	 * @param tid
	 * @return
	 */
	boolean isCalling();

	/**
	 * 获取当前用户注册状态
	 * @return
	 */
	boolean getRegisterStatus();
}
