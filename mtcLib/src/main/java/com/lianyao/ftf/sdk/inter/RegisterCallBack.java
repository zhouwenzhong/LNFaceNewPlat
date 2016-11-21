package com.lianyao.ftf.sdk.inter;

import org.json.JSONObject;

public interface RegisterCallBack {
	/**
	 * 注册成功
	 */
	public void onSuccess(JSONObject jsonObject);

	/**
	 * 注册失败
	 * 
	 * @param message
	 */
	public void onError(final String message);
}
