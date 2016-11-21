package com.lianyao.ftf.sdk.config;

public enum CallError {

	ERROR_NO_DATA(0), // 没有通话数据
	REJECTED(487), // 远端拒接
	ERROR_TRANSPORT(0), // 连接建立失败
	ERROR_INAVAILABLE(480), // 远端不在线
	ERROR_BUSY(406), // 远端正在通话
	ERROR_NONENTITY(404), // 远端不存在
	ERROR_NORESPONSE(408), // 远端没有响应
	ERROR_LOCAL_VERSION_SMALLER(0), //
	ERROR_PEER_VERSION_SMALLER(0);
	public Integer value;

	CallError(Integer value) {
		this.value = value;
	}

	public static CallError valueToCode(Integer value) {
		for (CallError error : CallError.values()) {
			if (error.value == value || error.value.equals(value)) {
				return error;
			}
		}
		return null;
	}
}
