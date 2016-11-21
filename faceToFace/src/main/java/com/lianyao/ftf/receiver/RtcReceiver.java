package com.lianyao.ftf.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lianyao.ftf.sdk.config.RtcBroadcast;
import com.lianyao.ftf.sdk.uitl.MtcLog;
import com.lianyao.ftf.view.PhoneAnswerVideoActivity;

public class RtcReceiver extends BroadcastReceiver {

	public RtcReceiver() {
		MtcLog.e("RtcReceiver is created!");
	}

	@Override
	public void onReceive(Context arg0, Intent intent) {
		if (intent.getAction().equals(RtcBroadcast.onIncomingCall)) {
			MtcLog.e("RtcReceiver", "onIncomingCall");
			String mobile = intent.getStringExtra("mobile");
			boolean isAudio = intent.getBooleanExtra("isAudio", false);
			boolean isVideo = intent.getBooleanExtra("isVideo", false);
			Intent intent2 = new Intent(arg0, PhoneAnswerVideoActivity.class);
			intent2.putExtra("nickname", mobile);
			intent2.putExtra("mobile", mobile);
			intent2.putExtra("isVideo", isVideo);
			intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent2.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			arg0.startActivity(intent2);
		}
	}
}
