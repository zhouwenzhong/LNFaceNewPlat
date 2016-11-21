package com.lianyao.ftf.sdk.pjsua;

import org.pjsip.pjsua2.CallInfo;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.pjsip_status_code;

import com.lianyao.ftf.sdk.inter.SignalingCallBack;
import com.lianyao.ftf.sdk.uitl.MtcLog;

public class SipObserver implements MyAppObserver {
	String TAG = "SipObserver";
	SignalingCallBack signalingCallBack;

	public SipObserver(SignalingCallBack signalingCallBack) {
		this.signalingCallBack = signalingCallBack;
	}

	@Override
	public void notifyRegState(pjsip_status_code code, String reason,
			int expiration) {
		if (signalingCallBack == null) {
			return;
		}
		MtcLog.e(TAG, "notifyRegState>>" + code.swigValue() + ">>" + reason);
		signalingCallBack.onRegistrationResults(code.swigValue(), reason);
	}

	@Override
	public void notifyIncomingCall(MyCall call) {
		// TODO Auto-generated method stub

		// CallOpParam prm =new CallOpParam();
		//
		// prm.setStatusCode(pjsip_status_code.PJSIP_SC_OK);
		try {
			MtcLog.e(TAG, "notifyIncomingCall>>" + call.getInfo().getState()
					+ ">>" + call.getInfo().getStateText() + ">>"
					+ call.getInfo().getLastReason() + ">>"
					+ call.getInfo().getSetting().getFlag() + ">>"
					+ call.getInfo().getRemAudioCount() + ">>"
					+ call.getInfo().getRemVideoCount());

			CallOpParam prm = new CallOpParam();
			prm.setStatusCode(pjsip_status_code.PJSIP_SC_RINGING);
			call.answer(prm);
			// signalingCallBack.onIncomingCall(tid, isAudio, isVideo);
		} catch (Exception e) {
			call.delete();
			MtcLog.e(TAG, "notifyIncomingCall>>Exception" + e);
		}
	}

	@Override
	public void notifyCallState(MyCall call) {
		// TODO Auto-generated method stub
		// pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED
		try {
			CallInfo callInfo = call.getInfo();
			MtcLog.e(TAG, "notifyCallState>>" + callInfo.getState());
		} catch (Exception e) {
			MtcLog.e(TAG, "notifyCallState>>Exception");
		}

	}

	@Override
	public void notifyCallMediaState(MyCall call) {
		// TODO Auto-generated method stub
		MtcLog.e(TAG, "notifyCallMediaState");
	}

	@Override
	public void notifyBuddyState(MyBuddy buddy) {
		// TODO Auto-generated method stub
		MtcLog.e(TAG, "notifyBuddyState");
	}

}
