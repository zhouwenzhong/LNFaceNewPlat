package com.lianyao.ftf.sdk.inter;

public interface FTFCallback {

	public void onFailure(String err);

	public void onResponse(String str, int code, String msg);
}
