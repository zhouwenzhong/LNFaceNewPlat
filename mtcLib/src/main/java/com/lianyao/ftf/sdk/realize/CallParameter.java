package com.lianyao.ftf.sdk.realize;

import org.webrtc.EglBase;
import org.webrtc.RendererCommon.ScalingType;
import org.webrtc.SurfaceViewRenderer;

import android.app.Activity;

/**
 * 呼叫/被叫参数
 * 
 * @author guochao
 * 
 */
public final class CallParameter {

	private Activity activity;
	private EglBase.Context renderEGLContext;
	private SurfaceViewRenderer localRender;
	private SurfaceViewRenderer remoteRender;
	private boolean isVideo = false;
	private boolean isAudio = true;
	private String userName = null;
	public boolean isCallInitiator = false;


	/**
	 * 无视频通讯 Context和SurfaceViewRenderer传null
	 * 
	 * @param activity
	 * @param renderEGLContext
	 * @param localRender
	 */
	public CallParameter(Activity activity, EglBase.Context renderEGLContext,
			SurfaceViewRenderer localRender) {
		super();
		if (localRender != null) {
			localRender.setScalingType(ScalingType.SCALE_ASPECT_FILL);
			localRender.init(renderEGLContext, null);
		}
		this.activity = activity;
		this.renderEGLContext = renderEGLContext;
		this.localRender = localRender;
	}

	public boolean isVideo() {
		return isVideo;
	}

	public void setVideo(boolean isVideo) {
		this.isVideo = isVideo;
	}

	public boolean isAudio() {
		return isAudio;
	}

	public void setAudio(boolean isAudio) {
		this.isAudio = isAudio;
	}

	public CallParameter() {
		super();
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public EglBase.Context getRenderEGLContext() {
		return renderEGLContext;
	}

	public void setRenderEGLContext(EglBase.Context renderEGLContext) {
		this.renderEGLContext = renderEGLContext;
	}

	public SurfaceViewRenderer getLocalRender() {
		return localRender;
	}

	public void setLocalRender(SurfaceViewRenderer localRender) {
		if (localRender != null) {
			localRender.setScalingType(ScalingType.SCALE_ASPECT_FILL);
			localRender.init(renderEGLContext, null);
			this.localRender = localRender;
		}
	}

	public void setItemVideoParameter(boolean isAudio,
			SurfaceViewRenderer remoteRender) {
			if (remoteRender != null) {
				remoteRender.setScalingType(ScalingType.SCALE_ASPECT_FILL);
				remoteRender.init(renderEGLContext, null);
			}
			
			this.remoteRender = remoteRender;
			this.isVideo = true;
			this.isAudio = isAudio;
	}

	public void setItemAudioParameter() {

		this.remoteRender = null;
		this.isVideo = false;
		this.isAudio = true;
	}

	public void setItemParameter(boolean isAudio, boolean isVideo) {
		this.isAudio = isAudio;
		this.isVideo = isVideo;
	}

	public void updateItemParameter(SurfaceViewRenderer remoteRender) {
			remoteRender.setScalingType(ScalingType.SCALE_ASPECT_FILL);
			remoteRender.init(renderEGLContext, null);
			
			this.remoteRender = remoteRender;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public SurfaceViewRenderer getRemoteRender() {
		return remoteRender;
	}

	public void setRemoteRender(SurfaceViewRenderer remoteRender) {
		this.remoteRender = remoteRender;
	}

	public boolean isCallInitiator() {
		return isCallInitiator;
	}

	public void setCallInitiator(boolean isCallInitiator) {
		this.isCallInitiator = isCallInitiator;
	}
}
