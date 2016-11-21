package com.lianyao.ftf.sdk.inter;

import com.lianyao.ftf.sdk.config.CallError;
import com.lianyao.ftf.sdk.config.CallState;

public interface CallStateListener {

	/**
	 * 连接状态变化
	 * 
	 * @param state
	 *            状态
	 */
	public void onCallStateChanged(final CallState callState,
			final CallError error);
}
