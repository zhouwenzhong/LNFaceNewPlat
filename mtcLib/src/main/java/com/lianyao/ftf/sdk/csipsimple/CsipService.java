package com.lianyao.ftf.sdk.csipsimple;

import android.content.Context;

import com.lianyao.ftf.sdk.config.CallError;
import com.lianyao.ftf.sdk.config.CallState;
import com.lianyao.ftf.sdk.config.Constants;
import com.lianyao.ftf.sdk.inter.SignalingCallBack;
import com.lianyao.ftf.sdk.inter.SignalingLayer;
import com.lianyao.ftf.sdk.uitl.FileUtils;
import com.lianyao.ftf.sdk.uitl.JsonUtil;
import com.lianyao.ftf.sdk.uitl.MtcLog;

import org.json.JSONObject;
import org.pjsip.api.SipProfile;
import org.pjsip.api.SipProfileState;
import org.pjsip.api.SipUri;
import org.pjsip.api.SipUri.ParsedSipContactInfos;
import org.pjsip.pjsua.csipsimple_config;
import org.pjsip.pjsua.pj_pool_t;
import org.pjsip.pjsua.pj_str_t;
import org.pjsip.pjsua.pjmedia_srtp_use;
import org.pjsip.pjsua.pjsip_status_code;
import org.pjsip.pjsua.pjsip_timer_setting;
import org.pjsip.pjsua.pjsip_transport_type_e;
import org.pjsip.pjsua.pjsua;
import org.pjsip.pjsua.pjsuaConstants;
import org.pjsip.pjsua.pjsua_acc_info;
import org.pjsip.pjsua.pjsua_call_setting;
import org.pjsip.pjsua.pjsua_config;
import org.pjsip.pjsua.pjsua_logging_config;
import org.pjsip.pjsua.pjsua_media_config;
import org.pjsip.pjsua.pjsua_msg_data;
import org.pjsip.pjsua.pjsua_transport_config;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import java.io.File;

public class CsipService implements SignalingLayer {

	private static final String STD_LIB_NAME = "stlport_shared";
	private static final String STACK_NAME = "pjsipjni";

	static {
		System.loadLibrary(STD_LIB_NAME);
		System.loadLibrary(STACK_NAME);
	}

	private Context context;

	public CsipService(Context context) {
		this.context = context;
	}

	/**
	 * 创建端口
	 *
	 * @param type
	 * @param port
	 * @return
	 */
	private Integer createLocalTransportAndAccount(pjsip_transport_type_e type,
												   int port) {
		pjsua_transport_config cfg = new pjsua_transport_config();
		int[] tId = new int[1];
		int status;
		pjsua.transport_config_default(cfg);
		cfg.setPort(port);
		status = pjsua.transport_create(type, cfg, tId);
		if (status != pjsuaConstants.PJ_SUCCESS) {
			return null;
		}
		Integer transportId = tId[0];
		if (transportId == null) {
			return null;
		}
		int[] p_acc_id = new int[1];
		pjsua.acc_add_local(transportId, pjsua.PJ_FALSE, p_acc_id);
		return p_acc_id[0];
	}

	/**
	 * 发消息
	 *
	 * @param tid
	 * @param message
	 * @return
	 */
	private boolean sendsipMessage(String tid, String message) {
		MtcLog.d("sendmessage", tid + ":" + message);
		SipProfileState profileState = CsipSession.getSipProfileState();
		if (profileState != null) {
			int pjsipAccountId = profileState.getPjsuaId();
			ParsedSipContactInfos finalCallee = SipUri.parseSipContact("<sip:"
					+ tid + "@" + CsipSession.getServerIP() + ">");
			if (pjsipAccountId == SipProfile.INVALID_ID) {
				pjsipAccountId = pjsua.acc_find_for_outgoing(pjsua
						.pj_str_copy(finalCallee.toString(false)));
			}
			pj_str_t uri = pjsua.pj_str_copy(finalCallee.toString(false));
			pj_str_t text = pjsua.pj_str_copy(message);
			byte[] userData = new byte[1];
			int status = pjsua.im_send(pjsipAccountId, uri, null, text, null,
					userData);
			return status == pjsuaConstants.PJ_SUCCESS;

		}
		return false;
	}

	public void cleanPjsua() {
		pjsua.call_hangup_all();
		if (CsipSession.getSipProfileState() != null) {
			pjsua.acc_set_online_status(CsipSession.getSipProfileState()
					.getPjsuaId(), 0);
		}
	}

	@Override
	public boolean initialize(SignalingCallBack signalingCallBack) {
		CsipSession.setServerIP(Constants.SipServer);
		// 创建pjsip
		int status = pjsua.create();
		{
			// 设置监听
			pjsua.setCallbackObject(new CsipUAReceiver(context,
					signalingCallBack));
			pjsua.setZrtpCallbackObject(new CsipZrtpReceiver());

			// new csipsimple 配置
			csipsimple_config cssCfg = new csipsimple_config();
			// 设置csipsimple配置
			pjsua.csipsimple_config_default(cssCfg);
			// pjsua.PJ_TRUE/pjsua.PJ_FALSE
			// sip 包大小 一般服务器不管理这个选项
			cssCfg.setUse_compact_form_headers(pjsua.PJ_FALSE);
			cssCfg.setUse_compact_form_sdp(pjsua.PJ_FALSE);
			// 不使用更新
			cssCfg.setUse_no_update(pjsua.PJ_TRUE);
			// 噪声没用到sip的音视频通讯 此选项无用
			cssCfg.setUse_noise_suppressor(pjsua.PJ_FALSE);

			// tcp 心跳包间隔 /秒
			cssCfg.setTcp_keep_alive_interval(30);
			// Tls 心跳包间隔 /秒
			cssCfg.setTls_keep_alive_interval(30);

			// Should the stack never switch to TCP when packets are too big?
			cssCfg.setDisable_tcp_switch(pjsuaConstants.PJ_FALSE);
			// 禁用 rport?
			cssCfg.setDisable_rport(pjsuaConstants.PJ_FALSE);
			// sip sdp处理 没用到
			cssCfg.setAdd_bandwidth_tias_in_sdp(pjsuaConstants.PJ_FALSE);
			// zrtp 本地储存 关闭
			cssCfg.setUse_zrtp(pjsua.PJ_FALSE);
			cssCfg.setStorage_folder(pjsua.pj_str_copy(""));

			// new pjsip配置
			pjsua_config cfg = new pjsua_config();
			pjsua.config_default(cfg);
			// ????
			cfg.setCb(pjsuaConstants.WRAPPER_CALLBACK_STRUCT);
			// 设备认证
			cfg.setUser_agent(pjsua.pj_str_copy(CsipUtil.getUserAgent(context)));
			// 线程 至少一个
			cfg.setThread_cnt(1);
			// 安全实时传输控制协议
			cfg.setUse_srtp(pjmedia_srtp_use.PJMEDIA_SRTP_DISABLED);
			cfg.setSrtp_secure_signaling(0);
			cfg.setNat_type_in_sdp(0);

			//stun
			cfg.setStun_host(pjsua.pj_str_copy(Constants.IceServer));

			// sip 的一个定时器 不知道干嘛用的 ????
			pjsip_timer_setting timerSetting = cfg.getTimer_setting();
			timerSetting.setMin_se(90);
			timerSetting.setSess_expires(1800);
			cfg.setTimer_setting(timerSetting);

			// new log 配置
			pjsua_logging_config logCfg = new pjsua_logging_config();
			pjsua.logging_config_default(logCfg);
			logCfg.setConsole_level(4);
			logCfg.setLevel(5);
			logCfg.setMsg_logging(pjsuaConstants.PJ_TRUE);

			File outFile = FileUtils.getLogsFile(context, true);
			if (outFile != null) {
				logCfg.setLog_filename(pjsua.pj_str_copy(outFile.getAbsolutePath()));
				logCfg.setLog_file_flags(0x1108 /* PJ_O_APPEND */);
			}

			// 媒体配置->不需要
			pjsua_media_config mediaCfg = new pjsua_media_config();
			pjsua.media_config_default(mediaCfg);

			// 初始化
			status = pjsua.csipsimple_init(cfg, logCfg, mediaCfg, cssCfg,
					context);
			if (status != pjsuaConstants.PJ_SUCCESS) {
				cleanPjsua();
				return false;
			}
		}
		{
			Integer localTcpAccPjId = createLocalTransportAndAccount(
					pjsip_transport_type_e.PJSIP_TRANSPORT_TCP, 0);
			if (localTcpAccPjId == null) {
				cleanPjsua();
				return false;
			}

		}

		// Add pjsip modules
		pjsua.mod_earlylock_init();

		// Initialization is done, now start pjsua
		status = pjsua.start();

		if (status != pjsua.PJ_SUCCESS) {
			cleanPjsua();
			return false;
		} else {
			// 不传输音视频
			pjsua.set_no_snd_dev();
			return true;
		}
	}

	@Override
	public boolean registration(String tid, String password) {
		if(tid == null || tid.equals("")) {
			return false;
		}
		int status = pjsuaConstants.PJ_FALSE;
		SipProfile profile = CsipSession.buildAccount(tid, tid,
				Constants.SipServer, password);
		CsipSession.setSipProfile(profile);
		CsipAccount account = new CsipAccount(profile);
		account.applyExtraParams(context);
		SipProfileState accountInfo = new SipProfileState(profile);
		CsipSession.setSipProfileState(accountInfo);
		account.cfg.setRegister_on_acc_add(pjsuaConstants.PJ_FALSE);
		account.cfg.setReg_timeout(300);
		int[] accId = new int[1];
		MtcLog.d("pjsip uri :" + CsipUtil.pjStrToString(account.cfg.getReg_uri()));
		status = pjsua.acc_add(account.cfg, pjsuaConstants.PJ_FALSE, accId);
		MtcLog.d("register acc_add :" + (status == pjsuaConstants.PJ_SUCCESS));
		status = pjsua.csipsimple_set_acc_user_data(accId[0], account.css_cfg);
		MtcLog.d("register csipsimple_set_acc_user_data :" + (status == pjsuaConstants.PJ_SUCCESS));
		status = pjsua.acc_set_registration(accId[0], 1);
		MtcLog.i("register result :" + (status == pjsuaConstants.PJ_SUCCESS));
		if (status == pjsuaConstants.PJ_SUCCESS) {
			SipProfileState ps = new SipProfileState(profile);
			ps.setAddedStatus(status);
			ps.setPjsuaId(accId[0]);
			CsipSession.setSipProfileState(ps);
			CsipSession.getSipProfile().active = true;
			pjsua.acc_set_online_status(accId[0], 1);
		}
		return status == pjsuaConstants.PJ_SUCCESS;

	}

	@Override
	public boolean unRegistration(String tid, String password) {
		int status = pjsuaConstants.PJ_FALSE;
		if (CsipSession.getSipProfileState() != null) {
			pjsua.acc_set_online_status(CsipSession.getSipProfileState()
					.getPjsuaId(), 0);
		}
		if(CsipSession.getSipProfileState() != null) {
			status = pjsua.acc_set_registration(CsipSession.getSipProfileState()
					.getPjsuaId(), 0);
		}
		cleanPjsua();
		MtcLog.i("unRegistration:" + status);
		return status == pjsuaConstants.PJ_SUCCESS;
	}

	@Override
	public boolean makeCall(String tid, boolean Audio, boolean isVideo) {
		boolean isSuccess;
		if (CsipSession.getSipProfileState() == null) {
			return false;
		}
		int pjsuaAccId = CsipSession.getSipProfileState().getPjsuaId();
		String url = "<sip:" + tid + "@" + Constants.SipServerIp + ">";
		pj_str_t uri = pjsua.pj_str_copy(url);
		byte[] userData = new byte[1];
		int[] callId = new int[1];
		pjsua_msg_data msgData = new pjsua_msg_data();
		pjsua_call_setting cs = new pjsua_call_setting();
		cs.setAud_cnt(1);
		cs.setVid_cnt(0);
		if (isVideo) {
			cs.setFlag(4);
		} else {
			cs.setFlag(8);
		}
		pjsua.msg_data_init(msgData);
		pj_pool_t pj_pool_t = pjsua.pool_create("call_tmp", 512, 512);
		pjsua.csipsimple_init_acc_msg_data(pj_pool_t, pjsuaAccId, msgData);
		int status = pjsua.call_make_call(pjsuaAccId, uri, cs, userData,
				msgData, callId);
		isSuccess = (status == pjsuaConstants.PJ_SUCCESS);
		if (isSuccess) {
			CsipSession
					.setOtherParty(tid, callId[0], CsipSession.State.callout);
			MtcLog.i("make call ", "make call success");
		} else {
			MtcLog.w("make call ", "make call failed:" + status);
//			CsipSession.clearOtherParty(tid);
//			cleanPjsua();
			pjsua.call_hangup_all();
			pjsua.pj_pool_release(pj_pool_t);
			return  false;
		}
		pjsua.pj_pool_release(pj_pool_t);

		return true;
	}

	@Override
	public void sendIceCandidate(String tid, IceCandidate iceCandidate) {
		JSONObject json = new JSONObject();
		JsonUtil.jsonPut(json, "type", CsipSendType.ICE_CANDIDATE);
		JsonUtil.jsonPut(json, "label", iceCandidate.sdpMLineIndex);
		JsonUtil.jsonPut(json, "id", iceCandidate.sdpMid);
		JsonUtil.jsonPut(json, "candidate", iceCandidate.sdp);
		sendsipMessage(tid, json.toString());
	}

	@Override
	public void sendDescription(String tid, SessionDescription description) {
		int index = 500;
		for (int i = 0; i < description.description.length() / index + 1; i++) {
			String sdpString;
			if (i * index + index >= description.description.length()) {
				sdpString = description.description.substring(i * index,
						description.description.length());
			} else {
				sdpString = description.description.substring(i * index, i
						* index + index);
			}
			JSONObject json = new JSONObject();
			JsonUtil.jsonPut(json, "type", description.type.canonicalForm());
			JsonUtil.jsonPut(json, "sdp", sdpString);
			JsonUtil.jsonPut(json, "index", i);
			if (i == description.description.length() / index) {
				JsonUtil.jsonPut(json, "isEnd", true);
			} else {
				JsonUtil.jsonPut(json, "isEnd", false);
			}
			sendsipMessage(tid, json.toString());
		}

	}

	@Override
	public void onCallReply(String tid) {
		Integer call_id = CsipSession.getCallId(tid);
		if (call_id != null) {
			pjsua.call_answer2(call_id, null, CallState.CONNECTED.value, null,
					null);
		}
	}

	@Override
	public void onAnswer(String tid, boolean isAudio, boolean isVideo) {
		Integer call_id = CsipSession.getCallId(tid);
		if (call_id != null) {
			CsipSession.updateOtherParty(tid, CsipSession.State.connect);
			pjsua_call_setting cs = new pjsua_call_setting();
			cs.setAud_cnt(1);
			cs.setVid_cnt(0);
			if (isVideo) {
				cs.setFlag(4);
			} else {
				cs.setFlag(8);
			}
			pjsua.call_answer2(call_id, cs, CallState.DISCONNNECTED.value,
					null, null);
		}
	}

	@Override
	public void onHangup(String tid) {
		Integer call_id = CsipSession.getCallId(tid);
		if (call_id != null) {
			pjsua.call_hangup(call_id, CallError.REJECTED.value, null, null);
			JSONObject message = new JSONObject();
			JsonUtil.jsonPut(message, "type", "onHangup");
			sendsipMessage(tid, message.toString());
			CsipSession.clearOtherParty(tid);
		}
	}

	@Override
	public void onConflict(String tid) {
		Integer call_id = CsipSession.getCallId(tid);
		if (call_id != null) {
			pjsua.call_hangup(call_id, CallError.ERROR_BUSY.value, null, null);
			CsipSession.clearOtherParty(tid);
		}
	}

	/**
	 * 是否通话中
	 * @param tid
	 * @return
	 */
	@Override
	public boolean isCalling() {
		return CsipSession.isCalling();
	}


	/**
	 * 资源释放
	 */
	@Override
	public void release(){
		MtcLog.d("CSIP release!");
		pjsua.csipsimple_destroy(0);
	}

	/**
	 * 获取注册状态
	 * @return
	 */
	@Override
	public boolean getRegisterStatus() {
		int success = pjsuaConstants.PJ_FALSE;
		pjsua_acc_info pjAccountInfo;
		pjAccountInfo = new pjsua_acc_info();
		SipProfileState sipProfileState = CsipSession.getSipProfileState();
		if(sipProfileState == null) {
			return false;
		}
		int pjsuaAccId = sipProfileState.getPjsuaId();

		success = pjsua.acc_get_info(pjsuaAccId, pjAccountInfo);

		if (success == pjsuaConstants.PJ_SUCCESS && pjAccountInfo != null) {
			int state = pjAccountInfo.getExpires();
			pjsip_status_code status = pjAccountInfo.getStatus();
			String msg = CsipUtil.pjStrToString(pjAccountInfo.getStatus_text());
			if( status.swigValue() == 0 ) {
				return false;
			}else {
				return true;
			}
		} else {
			return false;
		}
	}
}
