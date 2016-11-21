package com.lianyao.ftf.sdk.webrtc;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.webrtc.AudioTrack;
import org.webrtc.IceCandidate;
import org.webrtc.PeerConnection;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRenderer.Callbacks;
import org.webrtc.VideoTrack;

import com.lianyao.ftf.sdk.webrtc.WebrtcClient.PCObserver;

public class WebrtcRemote {

	public WebrtcRemote(boolean videoCallEnabled, boolean audioCallEnabled,
			PeerConnection peerConnection, PCObserver observer,
			Callbacks remoteRender) {
		super();
		this.videoCallEnabled = videoCallEnabled;
		this.audioCallEnabled = audioCallEnabled;
		this.peerConnection = peerConnection;
		this.observer = observer;
		this.remoteRender = remoteRender;
	}

	VideoTrack remoteVideoTrack;
	boolean isInitiator;
	List<IceCandidate> queuedRemoteCandidates = Collections
			.synchronizedList(new LinkedList<IceCandidate>());
	AudioTrack remoteAudioTrack;

	final boolean videoCallEnabled;
	final boolean audioCallEnabled;
	final PeerConnection peerConnection;
	final PCObserver observer;
	final VideoRenderer.Callbacks remoteRender;

}