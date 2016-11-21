package com.lianyao.ftf.sdk.layered;

import android.net.Uri;

import com.lianyao.ftf.sdk.realize.CallHelper;

/**
 * 设置
 * 
 * @author guochao
 * 
 */
public final class RtcSetting {

	/**
	 * 设置震动
	 */
	public RtcSetting setShock(boolean isShock) {
		CallHelper.isShock = isShock;
		return this;
	}

	/**
	 * 
	 * 设置来电响铃 声音文件
	 * 
	 * @param isRing
	 * @param ringfileRawId
	 */
	public RtcSetting setCallRing(boolean isRing, int ringfileRawId) {
		CallHelper.isRing = isRing;
		CallHelper.ringfileRawId = ringfileRawId;
		CallHelper.ringfileUri = null;
		return this;
	}

	/**
	 * 
	 * 设置来电响铃 声音文件
	 * 
	 * @param isRing
	 * @param ringfileRawId
	 */
	public RtcSetting setCallRing(boolean isRing, Uri uri) {
		CallHelper.isRing = isRing;
		CallHelper.ringfileUri = uri;
		CallHelper.ringfileRawId = 0;
		return this;
	}

	/**
	 * 
	 * 设置呼叫是的响铃 声音文件
	 * 
	 * @param isRing
	 * @param ringfileRawId
	 */
	public RtcSetting setRing(boolean isCallRing, int callRingfileRawId) {
		CallHelper.isCallRing = isCallRing;
		CallHelper.callRingfileRawId = callRingfileRawId;
		CallHelper.callRingfileUri = null;
		return this;
	}
	

	/**
	 * 
	 * 设置呼叫是的响铃 声音文件
	 * 
	 * @param isRing
	 * @param ringfileRawId
	 */
	public RtcSetting setRing(boolean isCallRing, Uri uri) {
		CallHelper.isCallRing = isCallRing;
		CallHelper.callRingfileUri = uri;
		CallHelper.callRingfileRawId = 0;
		return this;
	}

	/**
	 * 设置视频编解码
	 * 
	 * @param MtcCodec
	 *            .xxx
	 */
	public RtcSetting setVideoCodec(String videoCodec) {
		CallHelper.videoCodec = videoCodec;
		return this;
	}

	/**
	 * 设置音频编解码
	 * 
	 * @param MtcCodec
	 *            .xxx
	 */
	public RtcSetting setAudioCodec(String audioCodec) {
		CallHelper.audioCodec = audioCodec;
		return this;
	}
}
