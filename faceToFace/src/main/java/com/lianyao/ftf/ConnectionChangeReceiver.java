package com.lianyao.ftf;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ConnectionChangeReceiver extends BroadcastReceiver {

	Context context;

	@Override
	public void onReceive(Context context, Intent intent) {
		// this.context = context;
		// if (!NetUtil.isConnected(context)) {
		// ToastUtil.showShort(context, "网络不可以用");
		// } else {
		// if (!SPUtil.get(context, "uid", "").equals("")) {
		// new MtcClient().FTF_Refresh(SPUtil.get(context, "uid", "")
		// .toString(), Constants.APPID,
		// SPUtil.get(context, "tid", "").toString(),
		// Constants.TICKET, new RefreshClass());
		// }
		// }
	}

	// class RefreshClass implements FTFCallback {
	// @Override
	// public void onFailure(String err) {
	//
	// }
	//
	// @Override
	// public void onResponse(String str) {
	// JSONObject json = JSONObject.parseObject(str);
	// JSONObject jsonUser = json.getJSONObject("rsc").getJSONObject(
	// "user");
	// JSONObject jsonTm = jsonUser.getJSONObject("tm");
	// String tid = jsonTm.getString("tid");
	// boolean flag = new MtcClient().FTF_Login(tid,
	// jsonTm.getString("token"),
	// new LoginClass(tid, jsonTm.getString("token")));
	// Logger.e("刷新后登录：" + flag);
	// }
	// }

	// class LoginClass implements RegistrationCallBack {
	//
	// String tid, token;
	//
	// public LoginClass(String tid, String token) {
	// this.tid = tid;
	// this.token = token;
	// }
	//
	// @Override
	// public void onSussess() {
	// SPUtil.put(context, "token", token);
	// JSONObject param = new JSONObject();
	// param.put("id", SPUtil.get(context, "id", "-1"));
	// param.put("token", token);
	// RestRequest.getInstance(context).setRestPost(
	// new UpdateTokenClass(), "updateToken.json", param);
	// }
	//
	// @Override
	// public void onFailure(int state, String message) {
	// Logger.e("登录状态码：" + state + ",状态：" + message);
	// }
	//
	// }
	//
	// class UpdateTokenClass implements RestInterface {
	// @Override
	// public void onLoading(long total, long current, boolean isUploading) {
	//
	// }
	//
	// @Override
	// public void onSuccess(JSONObject json) {
	// }
	//
	// @Override
	// public void onFailure(HttpException error, String msg) {
	//
	// }
	// }

}
