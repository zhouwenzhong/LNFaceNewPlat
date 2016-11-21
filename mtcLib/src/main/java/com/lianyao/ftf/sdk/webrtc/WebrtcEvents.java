package com.lianyao.ftf.sdk.webrtc;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;

/**
 * webrtc 状态回调
 */
public interface WebrtcEvents {
	/**
	 * Callback fired once local SDP is created and set.
	 */
	public void onLocalDescription(final SessionDescription sdp, String tid);

	/**
	 * Callback fired once local Ice candidate is generated.
	 */
	public void onIceCandidate(final IceCandidate candidate, String tid);

	/**
	 * Callback fired once connection is established (IceConnectionState is
	 * CONNECTED).
	 */
	public void onIceConnected(String tid);

	/**
	 * Callback fired once connection is closed (IceConnectionState is
	 * DISCONNECTED).
	 */
	public void onIceDisconnected(String tid);

	/**
	 * Callback fired once peer connection is closed.
	 */
	public void onPeerConnectionClosed(String tid);

	/**
	 * Callback fired once peer connection statistics is ready.
	 */
	public void onPeerConnectionStatsReady(final StatsReport[] reports,
			String tid);

	/**
	 * Callback fired once peer connection error happened.
	 */
	public void onPeerConnectionError(final String description, String tid);
}
