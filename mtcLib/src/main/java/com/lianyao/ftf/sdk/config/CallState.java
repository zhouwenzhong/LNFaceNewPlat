package com.lianyao.ftf.sdk.config;

public enum CallState {
	CONNECTING(100), // 正在连接 //开始呼叫
	CONNECTED(180), // 已经建立连接 // 对方响应，说明对方在线
	ANSWER_CALL(203), // 开始接通 // 接电话
	ACCEPTED(204), // 接通成功 //
	NETWORK_UNSTABLE(0), // 网络不稳定 //
	NETWORK_NORMAL(0), // 网络正常 //
	VIDEO_PUSE(0), // 视频暂停 //
	VIDEO_RESTORE(0), // 视频恢复 //
	AUDIO_PUSE(0), // 音频暂停 //
	AUDIO_RESTORE(0), // 音频恢复 //
	DISCONNNECTED(200);// 连接断开 // 挂断
	public Integer value;

	CallState(Integer value) {
		this.value = value;
	}
}
