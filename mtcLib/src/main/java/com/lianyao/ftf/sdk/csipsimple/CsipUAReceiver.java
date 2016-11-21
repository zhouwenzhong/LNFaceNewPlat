package com.lianyao.ftf.sdk.csipsimple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.pjsip.api.SipCallSession.InvState;
import org.pjsip.pjsua.Callback;
import org.pjsip.pjsua.SWIGTYPE_p_int;
import org.pjsip.pjsua.SWIGTYPE_p_p_pjmedia_port;
import org.pjsip.pjsua.SWIGTYPE_p_pjmedia_sdp_session;
import org.pjsip.pjsua.SWIGTYPE_p_pjmedia_stream;
import org.pjsip.pjsua.SWIGTYPE_p_pjsip_rx_data;
import org.pjsip.pjsua.SWIGTYPE_p_pjsip_status_code;
import org.pjsip.pjsua.SWIGTYPE_p_pjsip_tx_data;
import org.pjsip.pjsua.pj_pool_t;
import org.pjsip.pjsua.pj_str_t;
import org.pjsip.pjsua.pj_stun_nat_detect_result;
import org.pjsip.pjsua.pjsip_event;
import org.pjsip.pjsua.pjsip_redirect_op;
import org.pjsip.pjsua.pjsip_status_code;
import org.pjsip.pjsua.pjsua;
import org.pjsip.pjsua.pjsuaConstants;
import org.pjsip.pjsua.pjsua_acc_info;
import org.pjsip.pjsua.pjsua_call_info;
import org.pjsip.pjsua.pjsua_med_tp_state_info;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import android.content.Context;
import android.os.PowerManager.WakeLock;

import com.lianyao.ftf.sdk.config.CallError;
import com.lianyao.ftf.sdk.config.CallState;
import com.lianyao.ftf.sdk.inter.SignalingCallBack;
import com.lianyao.ftf.sdk.uitl.MtcLog;
import com.lianyao.ftf.sdk.uitl.StringUtil;

public class CsipUAReceiver extends Callback {

	private WakeLock eventLock;
	private SignalingCallBack callBack;

	private List<Description> list = new ArrayList<Description>();
	private int count = 0;

	public CsipUAReceiver(Context context, SignalingCallBack callBack) {
		this.callBack = callBack;
	}

	@Override
	public void on_pager(int callId, pj_str_t from, pj_str_t to,
			pj_str_t contact, pj_str_t mime_type, pj_str_t body) {
		lockCpu();
		String bodyStr = CsipUtil.pjStrToString(body);
		MtcLog.i("getmessage", bodyStr);
		if (bodyStr == null || bodyStr.equals("")) {
			return;
		}
		String tid = CsipUtil.pjStrToString(from);
		tid = CsipUtil.uriToCallee(tid);
		tid = StringUtil.getUidFromTid(tid);
		try {
			JSONObject jsonObject = new JSONObject(bodyStr);
			String type = jsonObject.getString("type");
			if (type.equals(CsipSendType.ICE_CANDIDATE)) {
				IceCandidate candidate = new IceCandidate(
						jsonObject.getString("id"), jsonObject.getInt("label"),
						jsonObject.getString("candidate"));
				callBack.onCandidate(tid, candidate);
			} else if (type.equals(CsipSendType.WECRTC_ANSWER)
					|| type.equals(CsipSendType.WECRTC_OFFER)) {
				Description description = new Description();
				description.index = jsonObject.getInt("index");
				description.isEnd = jsonObject.getBoolean("isEnd");
				description.type = jsonObject.getString("type");
				description.sdp = jsonObject.getString("sdp");
				list.add(description);
				if (description.isEnd) {
					count = description.index;
				}
				if ((description.isEnd && list.size() == 1)
						|| (list.size() == count + 1 && list.size() > 1)) {
					Comparator<Description> comp = new SortComparator();
					Collections.sort(list, comp);
					String sdpStr = "";
					for (int i = 0; i < list.size(); i++) {
						sdpStr += list.get(i).sdp;
					}
					count = 0;
					list.clear();
					SessionDescription sdp = new SessionDescription(
							SessionDescription.Type.fromCanonicalForm(type),
							sdpStr);
					callBack.onSessionDescription(tid, sdp);
				}
			} else if (type.equals("onHangup")) {
				callBack.onHangUp(tid, CallState.DISCONNNECTED.value);
			}
		} catch (JSONException e) {

		}
		unlockCpu();
	}

	@Override
	public void on_incoming_call(int acc_id, int call_id,
			SWIGTYPE_p_pjsip_rx_data rdata) {
		lockCpu();
		pjsua_call_info call_info = new pjsua_call_info();
		pjsua.call_get_info(call_id, call_info);
		String tid = CsipUtil.pjStrToString(call_info.getRemote_info());
		tid = CsipUtil.uriToCallee(tid);
		tid = StringUtil.getUidFromTid(tid);
		CsipSession.setOtherParty(tid, call_id, CsipSession.State.incoming);
		MtcLog.i("callinfo", call_info.getRem_offerer() + "");
		if (call_info.getRem_offerer() == 1) {
			callBack.onIncomingCall(tid, true, true);
		} else {
			callBack.onIncomingCall(tid, true, false);
		}
		unlockCpu();
	}

	@Override
	public void on_reg_state(final int accountId) {
		lockCpu();
		int success = pjsuaConstants.PJ_FALSE;
		pjsua_acc_info pjAccountInfo;
		pjAccountInfo = new pjsua_acc_info();
		success = pjsua.acc_get_info(accountId, pjAccountInfo);

		if (success == pjsuaConstants.PJ_SUCCESS && pjAccountInfo != null) {
			int expire = pjAccountInfo.getExpires();
			int state = pjAccountInfo.getStatus().swigValue();
			String msg = CsipUtil.pjStrToString(pjAccountInfo.getStatus_text());
			if( msg.equals("OK") && expire < 0 ) {
				//注销
			}else {
				callBack.onRegistrationResults(state, msg);
			}
		} else {
			callBack.onRegistrationResults(404, "fail");
		}
		unlockCpu();
	}

	@Override
	public void on_call_state(int call_id, pjsip_event e) {
		lockCpu();
		boolean exists = CsipSession.isCallExist(call_id);
		pjsua_call_info call_info = new pjsua_call_info();
		pjsua.call_get_info(call_id, call_info);
		int callState = call_info.getState().swigValue();

		String tid = CsipUtil.pjStrToString(call_info.getRemote_info());
		tid = CsipUtil.uriToCallee(tid);
		tid = StringUtil.getUidFromTid(tid);

		if (callState == InvState.CALLING) {
			callBack.onCalling(tid);
		} else if (callState == InvState.DISCONNECTED && exists) {
			int status = call_info.getLast_status().swigValue();
			MtcLog.i("on_call_state", "Last_status>>" + status);
			if (status == CallState.DISCONNNECTED.value
					|| status == CallError.REJECTED.value) {
				callBack.onHangUp(tid, status);
			} else if (status == CallError.ERROR_INAVAILABLE.value
					|| status == CallError.ERROR_NONENTITY.value
					|| status == CallError.ERROR_NORESPONSE.value) {
				callBack.onCallFailure(tid, status);
			} else if (status == CallError.ERROR_BUSY.value) {
				callBack.onCallFailure(tid, status);
			}
			CsipSession.clearOtherParty(tid);
		} else if (callState == InvState.EARLY) {
			callBack.onCallReply(tid);
		} else if (callState == InvState.CONNECTING) {
			CsipSession.updateOtherParty(tid, CsipSession.State.connect);
			MtcLog.i("callinfo", call_info.getRem_offerer() + "");
			if (call_info.getRem_offerer() == 1) {
				callBack.onAnswerPhone(tid, true, true);
			} else {
				callBack.onAnswerPhone(tid, true, false);
			}
		}
		unlockCpu();
	}

	@Override
	public void on_pager_status(int call_id, pj_str_t to, pj_str_t body,
			pjsip_status_code status, pj_str_t reason) {
		lockCpu();
		// TODO 发送失败处理
		// if (status.swigValue() != 0) {
		// String bodyStr = SipUtil.pjStrToString(body);
		//
		// try {
		// JSONObject jsonObject = new JSONObject(bodyStr);
		// String type = jsonObject.getString("type");
		// if (type.equals(SipSendType.WECRTC_ANSWER)
		// || type.equals(SipSendType.WECRTC_OFFER)) {
		// callBack.onSessionDescriptionError(call_id, bodyStr);
		// }
		// } catch (JSONException e) {
		//
		// }
		//
		// }
		unlockCpu();
	}

	class Description {
		public boolean isEnd;
		public int index;
		public String sdp;
		public String type;
	}

	class SortComparator implements Comparator<Description> {
		@Override
		public int compare(Description lhs, Description rhs) {
			return (lhs.index - rhs.index);
		}
	}

	private void lockCpu() {
		if (eventLock != null) {
			eventLock.acquire();
		}
	}

	private void unlockCpu() {
		if (eventLock != null && eventLock.isHeld()) {
			eventLock.release();

		}
	}

	public void stopService() {
		if (eventLock != null) {
			while (eventLock.isHeld()) {
				eventLock.release();
			}
		}
	}

	@Override
	public void on_call_tsx_state(int call_id,
			org.pjsip.pjsua.SWIGTYPE_p_pjsip_transaction tsx, pjsip_event e) {

	}

	@Override
	public void on_buddy_state(int buddy_id) {
	}

	@Override
	public void on_call_media_state(final int callId) {
	}

	@Override
	public void on_mwi_info(int acc_id, pj_str_t mime_type, pj_str_t body) {

	}

	public int on_validate_audio_clock_rate(int clockRate) {
		return -1;
	}

	@Override
	public void on_setup_audio(int beforeInit) {

	}

	@Override
	public void on_teardown_audio() {

	}

	@Override
	public pjsip_redirect_op on_call_redirected(int call_id, pj_str_t target) {

		return pjsip_redirect_op.PJSIP_REDIRECT_ACCEPT;
	}

	@Override
	public void on_nat_detect(pj_stun_nat_detect_result res) {

	}

	@Override
	public void on_call_media_transport_state(int call_id,
			pjsua_med_tp_state_info info) {
	}

	@Override
	public void on_call_replace_request(int call_id,
			SWIGTYPE_p_pjsip_rx_data rdata, SWIGTYPE_p_int st_code,
			pj_str_t st_text) {
	}

	@Override
	public void on_call_replaced(int old_call_id, int new_call_id) {
	}

	@Override
	public void on_call_sdp_created(int call_id,
			SWIGTYPE_p_pjmedia_sdp_session sdp, pj_pool_t pool,
			SWIGTYPE_p_pjmedia_sdp_session rem_sdp) {
	}

	@Override
	public void on_call_transfer_request(int call_id, pj_str_t dst,
			SWIGTYPE_p_pjsip_status_code code) {
	}

	@Override
	public void on_dtmf_digit(int call_id, int digit) {
	}

	@Override
	public void on_pager2(int call_id, pj_str_t from, pj_str_t to,
			pj_str_t contact, pj_str_t mime_type, pj_str_t body,
			SWIGTYPE_p_pjsip_rx_data rdata) {
	}

	@Override
	public void on_pager_status2(int call_id, pj_str_t to, pj_str_t body,
			pjsip_status_code status, pj_str_t reason,
			SWIGTYPE_p_pjsip_tx_data tdata, SWIGTYPE_p_pjsip_rx_data rdata) {
	}

	@Override
	public int on_set_micro_source() {
		return 0;
	}

	@Override
	public void on_stream_created(int call_id, SWIGTYPE_p_pjmedia_stream strm,
			long stream_idx, SWIGTYPE_p_p_pjmedia_port p_port) {
	}

	@Override
	public void on_stream_destroyed(int call_id,
			SWIGTYPE_p_pjmedia_stream strm, long stream_idx) {
	}

	@Override
	public void on_call_transfer_status(int callId, int st_code,
			pj_str_t st_text, int final_, SWIGTYPE_p_int p_cont) {
	}
}
