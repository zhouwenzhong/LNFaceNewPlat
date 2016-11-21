package com.lianyao.ftf.sdk.inter;

public interface InitListener {

	/**
	 * 初始化成功
	 */
	public void onSuccess();

	/**
	 * 初始化失败
	 * 
	 * @param message
	 */
	public void onError(final String message);

}
