package com.lianyao.ftf.sdk.inter;

/**
 * fps 丢包 等监听
 * 
 * @author guochao
 * 
 */
public interface StatusObserver {

	/**
	 * 发送方
	 * 
	 */
	public void onSent(String fps, String bandwidth, String packetsLost);

	/**
	 * 接收方
	 */
	public void onRecv(String fps, String bandwidth, String packetsLost);

}
