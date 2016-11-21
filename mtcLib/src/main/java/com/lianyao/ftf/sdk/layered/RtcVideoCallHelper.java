package com.lianyao.ftf.sdk.layered;

import com.lianyao.ftf.sdk.realize.MainManager;

public class RtcVideoCallHelper {

	public void speaker(boolean isSpeaker) {
		MainManager.getInstance().setSpeaker(isSpeaker);
	}

	public void switchCamera() {
		MainManager.getInstance().switchCamera();
	}

	public void stopVideo() {
		MainManager.getInstance().setLocalVideoEnabled(false);
	}

	public void restoreVideo() {
		MainManager.getInstance().setLocalVideoEnabled(true);
	}

	public void stopAudio() {
		MainManager.getInstance().setLocalAudioEnabled(false);
	}

	public void restoreAudio() {
		MainManager.getInstance().setLocalAudioEnabled(true);
	}
}
