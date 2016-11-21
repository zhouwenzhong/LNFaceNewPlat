package com.lianyao.ftf.sdk.inter;

public interface LoginCallback {

	/**
	 * 登录成功
	 */
	public void onSussess();

	/**
	 * 登录失败
	 */
	public void onFailure(int state, String message);

	/**
	 * token初次获取或者token过期
	 */
	public void onUpdateToken(String tInfo);

}