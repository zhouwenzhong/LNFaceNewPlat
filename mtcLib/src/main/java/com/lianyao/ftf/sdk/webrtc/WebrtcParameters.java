package com.lianyao.ftf.sdk.webrtc;

import android.content.Context;

public class WebrtcParameters {

	public final Context context;
	public final String videoCodec;
	public final String audioCodec;

	public WebrtcParameters(Context context, String videoCodec,
			String audioCodec) {
		super();
		this.context = context;
		this.videoCodec = videoCodec;
		this.audioCodec = audioCodec;
	}

}
