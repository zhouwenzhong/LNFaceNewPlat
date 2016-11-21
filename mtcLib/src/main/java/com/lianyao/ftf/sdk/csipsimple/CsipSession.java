package com.lianyao.ftf.sdk.csipsimple;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.pjsip.api.SipProfile;
import org.pjsip.api.SipProfileState;
import org.pjsip.api.SipUri;

public class CsipSession {

	private static String serverIP;
	private static SipProfile sipProfile;
	private static SipProfileState sipProfileState;
	private static Map<String, Information> map = new HashMap<String, Information>();

	public enum State {
		incoming, callout, connect
	}

	static class Information {
		public Information(Integer callId, State state) {
			super();
			this.callId = callId;
			this.state = state;
		}

		public Integer callId;
		public State state;
	}

	public static SipProfile getSipProfile() {
		return sipProfile;
	}

	public static void setSipProfile(SipProfile sipProfile) {
		CsipSession.sipProfile = sipProfile;
	}

	public static SipProfileState getSipProfileState() {
		return sipProfileState;
	}

	public static void setSipProfileState(SipProfileState sipProfileState) {
		CsipSession.sipProfileState = sipProfileState;
	}

	public static String getServerIP() {
		return serverIP;
	}

	public static void setServerIP(String serverIP) {
		CsipSession.serverIP = serverIP;
	}

	public static void setOtherParty(String tid, Integer callId, State state) {
		Information information = new Information(callId, state);
		map.put(tid, information);
	}

	public static void updateOtherParty(String tid, State state) {
		Information information = map.get(tid);
		if(information != null)
			information.state = state;
	}

	public static Integer getCallId(String tid) {
		Information information = map.get(tid);
		if (information != null) {
			return information.callId;
		} else {
			return null;
		}
	}
	public static State getCallSate(String tid) {
		Information information = map.get(tid);
		if (information != null) {
			return information.state;
		} else {
			return null;
		}
	}
	
	public static boolean isCalling() {
		Set<String> tids = map.keySet();
		for (String tid:tids
			 ) {
			Information info = map.get(tid);
			if(info.state == State.connect) {
				return true;
			}
		}

		return false;
	}
	public static void clearOtherParty(String tid) {
		if (tid == null) {
			map.clear();
		} else if (map.containsKey(tid)) {
			map.remove(tid);
		}
	}

	/**
	 * 判断通话是否还存在
	 * @param callId
	 * @return
	 */
	public static boolean isCallExist(int callId) {
		Set<String> tids = map.keySet();
		for (String tid:tids
			 ) {
			Information info = map.get(tid);
			if(info.callId == callId) {
				return true;
			}
		}

		return false;
	}
	public static SipProfile buildAccount(String displayName, String username,
			String accountServer, String password) {
		String[] serverParts = accountServer.split(":");
		SipProfile account = new SipProfile();
		account.wizard = "EXPERT";
		account.id = 2l;
		account.display_name = displayName;
		account.transport = SipProfile.TRANSPORT_TCP;
		account.default_uri_scheme = "sip";
		account.acc_id = "<sip:" + SipUri.encodeUser(username.trim()) + "@"
				+ serverParts[0].trim() + ">";
		account.reg_uri = "sip:" + accountServer;
		account.use_srtp = -1;
		account.use_zrtp = -1;
		account.realm = "*";
		account.username = username.trim();
		account.data = password;
		account.scheme = SipProfile.CRED_SCHEME_DIGEST;
		account.datatype = SipProfile.CRED_DATA_PLAIN_PASSWD;
		account.initial_auth = false;
		account.auth_algo = "";
		account.priority = 100;
		account.publish_enabled = 0;
		account.reg_timeout = 900;
		account.reg_delay_before_refresh = 100;
		account.contact_rewrite_method = 2;
		account.allow_contact_rewrite = true;
		account.allow_via_rewrite = false;
		account.allow_sdp_nat_rewrite = false;
		account.force_contact = "";
		account.proxies = new String[] { "sip:" + accountServer };
		account.vm_nbr = "";
		account.mwi_enabled = false;
		account.try_clean_registers = 1;
		account.use_rfc5626 = false;
		account.rfc5626_instance_id = "";
		account.rfc5626_reg_id = "";
		account.vid_in_auto_show = -1;
		account.vid_out_auto_transmit = -1;
		account.rtp_port = -1;
		account.rtp_bound_addr = "";
		account.rtp_public_addr = "";
		account.rtp_enable_qos = -1;
		account.rtp_qos_dscp = -1;
		account.sip_stun_use = -1;
		account.media_stun_use = -1;
		account.ice_cfg_use = -1;
		account.ice_cfg_enable = 0;
		account.turn_cfg_use = -1;
		account.turn_cfg_enable = 0;
		account.turn_cfg_server = "";
		account.turn_cfg_user = "";
		account.turn_cfg_password = "";
		account.ipv6_media_use = 0;
		return account;
	}

}
