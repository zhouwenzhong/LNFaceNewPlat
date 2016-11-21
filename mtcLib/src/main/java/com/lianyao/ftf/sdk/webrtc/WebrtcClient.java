package com.lianyao.ftf.sdk.webrtc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.CameraEnumerationAndroid;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnection.IceConnectionState;
import org.webrtc.PeerConnection.IceServer;
import org.webrtc.PeerConnection.RTCConfiguration;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.StatsObserver;
import org.webrtc.StatsReport;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import com.lianyao.ftf.sdk.config.Constants;
import com.lianyao.ftf.sdk.uitl.MtcLog;

public class WebrtcClient {

	/******* 日志标记 **********/
	private final String WEBRTCSTEP = "WEBRTCSTEP";

	/******* PeerConnection工厂 **********/
	private PeerConnectionFactory peerConnectionFactory;

	/******* 视频管理 **********/
	private VideoTrack localVideoTrack;

	/******* 音频管理 **********/
	private AudioTrack localAudioTrack;

	/******* 远程通话参数 **********/
	private Map<String, WebrtcRemote> webrtcRemoteMap = new HashMap<String, WebrtcRemote>();

	/******* 媒体管理 **********/
	private MediaStream mediaStream;

	/******* webrtc参数 **********/
	private WebrtcParameters parameters;

	/******* 本地SessionDescription 多方通话时只获取一次 **********/
	private SessionDescription localSdp;

	/******* webrtc状态监听 **********/
	private WebrtcEvents events;

	/******* 摄像头视频管理 **********/
	private VideoCapturerAndroid videoCapturer;

	/******* 视频源管理 **********/
	private VideoSource localVideoSource;

	/******* webrtc线程 **********/
	private WebrtcLooper webrtcLooper;

	/******* 统计信息监听用定时器 **********/
	private Timer statsTimer;
	
	private static final String MAX_VIDEO_WIDTH_CONSTRAINT = "maxWidth";
	private static final String MIN_VIDEO_WIDTH_CONSTRAINT = "minWidth";
	private static final String MAX_VIDEO_HEIGHT_CONSTRAINT = "maxHeight";
	private static final String MIN_VIDEO_HEIGHT_CONSTRAINT = "minHeight";

	/**
	 * 构造函数
	 * 
	 * @param parameters
	 */
	public WebrtcClient(final WebrtcParameters parameters) {
		this.parameters = parameters;
		statsTimer = new Timer();

		webrtcLooper = new WebrtcLooper();
		webrtcLooper.requestStart();
		webrtcLooper.execute(new Runnable() {

			@Override
			public void run() {
				PeerConnectionFactory.initializeInternalTracer();
				/**
				 * 1.初始化 四个参数
				 * 
				 * a)上下文、
				 * 
				 * b)是否初始化音频、
				 * 
				 * c)是否初始化视频、
				 * 
				 * d)是否允许硬件加速。
				 */
				PeerConnectionFactory.initializeAndroidGlobals(
						parameters.context, true, true, true);
			}
		});

	}

	/**
	 * 回调监听
	 * 
	 * @param events
	 */
	public void addEvents(WebrtcEvents events) {
		this.events = events;

	}

	/**
	 * 本地参数
	 * 
	 * @param localRender
	 * @param audioEnabled
	 * @param videoEnabled
	 */
	public void addLocal(final VideoRenderer.Callbacks localRender,
			final boolean audioEnabled, final boolean videoEnabled) {
		webrtcLooper.execute(new Runnable() {

			@Override
			public void run() {
				/**
				 * 2.PeerConnectionFactory构造函数创建
				 */
				peerConnectionFactory = new PeerConnectionFactory(null);
				/**
				 * 3.创建并设置MediaStream
				 */
				mediaStream = peerConnectionFactory
						.createLocalMediaStream(StaticParameter.LOCAL_MEDIA_STREAM_ID);
				if (localRender != null) {
					/**
					 * 4.获取前置摄像头驱动名称
					 * 
					 * 获取后置摄像头驱动名称
					 * 
					 * String frontCameraDeviceName = CameraEnumerationAndroid
					 * .getNameOfFrontFacingDevice();
					 * 
					 */
					if(CameraEnumerationAndroid.getDeviceCount() < 1) {
						if(!videoEnabled) {

						}else {
							reportError("Failed to open camera", null);
							return;
						}
					}else {
						String cameraDeviceName = CameraEnumerationAndroid
								.getDeviceName(0);
						/**
						 * 5. 获取摄像头实例
						 */
						videoCapturer = VideoCapturerAndroid.create(
								cameraDeviceName, null, null);
						/**
						 * 6.VideoSource/VideoTrack 视频管理
						 * 
						 * MediaConstraints媒体约束 ，键值对形式,视频宽、高、fps等
						 * 
						 */
						if (videoCapturer == null) {
							reportError("Failed to open camera", null);
							return;
						}
						// 获取视频源 允许方法开启、停止设备捕获视频
						MediaConstraints videoConstraints = new MediaConstraints();
						
						// 设置分辨率
//						videoConstraints.mandatory.add(new KeyValuePair(
//								MIN_VIDEO_WIDTH_CONSTRAINT, Integer
//										.toString(1920)));
//						videoConstraints.mandatory.add(new KeyValuePair(
//								MAX_VIDEO_WIDTH_CONSTRAINT, Integer
//										.toString(1920)));
//						videoConstraints.mandatory.add(new KeyValuePair(
//								MIN_VIDEO_HEIGHT_CONSTRAINT, Integer
//										.toString(1080)));
//						videoConstraints.mandatory.add(new KeyValuePair(
//								MAX_VIDEO_HEIGHT_CONSTRAINT, Integer
//										.toString(1920)));
						
						localVideoSource = peerConnectionFactory.createVideoSource(
								videoCapturer, videoConstraints);
						// 添加VideoSource到MediaStream
						localVideoTrack = peerConnectionFactory.createVideoTrack(
								StaticParameter.VIDEO_TRACK_ID, localVideoSource);
						localVideoTrack.addRenderer(new VideoRenderer(localRender));
						/** =================== 以上部分可以显示本地摄像头 ===================== */
						mediaStream.addTrack(localVideoTrack);
					}
				}
				setLocalVideoEnabled(videoEnabled);

				/**
				 * 6.AudioSource/AudioTrack 音频管理
				 * 
				 * MediaConstraints媒体约束 ，键值对形式,音频具体有什么还不知道
				 */
				MediaConstraints audioConstraints = new MediaConstraints();
				AudioSource localAudioSource = peerConnectionFactory
						.createAudioSource(audioConstraints);
				localAudioTrack = peerConnectionFactory.createAudioTrack(
						StaticParameter.AUDIO_TRACK_ID, localAudioSource);
				mediaStream.addTrack(localAudioTrack);
				setLocalAudioEnabled(audioEnabled);

			}
		});

	}

	/**
	 * 远程参数
	 * 
	 * @param tid
	 * @param remoteRender
	 * @param audioCallEnabled
	 * @param videoCallEnabled
	 */
	public void addRemote(final String tid,
			final VideoRenderer.Callbacks remoteRender,
			final boolean audioCallEnabled, final boolean videoCallEnabled) {
		webrtcLooper.execute(new Runnable() {

			@Override
			public void run() {
				/**
				 * 8.创建PeerConnection 三个参数
				 * 
				 * a)RTCConfig 包括ice、tcp、等
				 * 
				 * b)MediaConstraints 媒体约束 ，键值对形式,SRTP协议约束等
				 * 
				 * c)PeerConnection.Observer 状态监听
				 * 
				 */
				MediaConstraints pcMediaConstraints = new MediaConstraints();
				pcMediaConstraints.optional
						.add(new MediaConstraints.KeyValuePair(
								StaticParameter.DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT,
								"true"));
				PCObserver observer = new PCObserver(tid);
				PeerConnection peerConnection = peerConnectionFactory
						.createPeerConnection(bulidRTCConfiguration(),
								pcMediaConstraints, observer);
				peerConnection.addStream(mediaStream);
				WebrtcRemote webrtcRemote = new WebrtcRemote(videoCallEnabled,
						audioCallEnabled, peerConnection, observer,
						remoteRender);
				webrtcRemoteMap.put(tid, webrtcRemote);
			}
		});
	}

	/**
	 * RTCConfiguration配置
	 * 
	 * @return
	 */
	private RTCConfiguration bulidRTCConfiguration() {
		List<IceServer> iceServers = new ArrayList<PeerConnection.IceServer>();
		iceServers.add(new IceServer("stun:" + Constants.IceServer,
				Constants.IceName, Constants.IcePwd));
		iceServers.add(new IceServer("turn:" + Constants.IceServer,
				Constants.IceName, Constants.IcePwd));
		PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(
				iceServers);
		return rtcConfig;
	}

	/**
	 * 开始呼叫
	 */
	public void createOffer() {
		webrtcLooper.execute(new Runnable() {

			@Override
			public void run() {
				/**
				 * 9.设置远程音视频
				 */
				for (String tid : webrtcRemoteMap.keySet()) {
					WebrtcRemote webrtcRemote = webrtcRemoteMap.get(tid);
					webrtcRemote.isInitiator = true;
					MediaConstraints sdpMediaConstraints = new MediaConstraints();
					sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
							"OfferToReceiveAudio", String.valueOf(true)));
					sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
							"OfferToReceiveVideo", String.valueOf(true)));
					webrtcRemote.peerConnection.createOffer(
							new SDPObserver(tid), sdpMediaConstraints);
				}
			}
		});

	}

	/**
	 * 建立应答
	 * 
	 * @param tid
	 */
	public void createAnswer(final String tid) {
		webrtcLooper.execute(new Runnable() {

			@Override
			public void run() {
				WebrtcRemote webrtcRemote = webrtcRemoteMap.get(tid);
				webrtcRemote.isInitiator = false;
				MediaConstraints sdpMediaConstraints = new MediaConstraints();
				sdpMediaConstraints.mandatory
						.add(new MediaConstraints.KeyValuePair(
								"OfferToReceiveAudio", String.valueOf(true)));
				sdpMediaConstraints.mandatory
						.add(new MediaConstraints.KeyValuePair(
								"OfferToReceiveVideo", String.valueOf(true)));
				webrtcRemote.peerConnection.createAnswer(new SDPObserver(tid),
						sdpMediaConstraints);
			}
		});

	}

	/**
	 * 设置远程ice
	 * 
	 * @param candidate
	 * @param tid
	 */
	public void addRemoteIceCandidate(final IceCandidate candidate,
			final String tid) {
		webrtcLooper.execute(new Runnable() {

			@Override
			public void run() {
				WebrtcRemote webrtcRemote = webrtcRemoteMap.get(tid);
				if (webrtcRemote.queuedRemoteCandidates != null) {
					webrtcRemote.queuedRemoteCandidates.add(candidate);
				} else {
					webrtcRemote.peerConnection.addIceCandidate(candidate);
				}
			}
		});
	}

	/**
	 * 设置远程Session
	 * 
	 * @param sdp
	 * @param tid
	 */
	public void setRemoteDescription(final SessionDescription sdp,
			final String tid) {
		webrtcLooper.execute(new Runnable() {
			@Override
			public void run() {
				WebrtcRemote webrtcRemote = webrtcRemoteMap.get(tid);
				String sdpDescription = sdp.description;
				if (webrtcRemote.videoCallEnabled) {
					sdpDescription = preferCodec(sdpDescription,
							parameters.videoCodec, false);
				}
				SessionDescription sdpRemote = new SessionDescription(sdp.type,
						sdpDescription);
				webrtcRemote.peerConnection.setRemoteDescription(
						new SDPObserver(tid), sdpRemote);
			}
		});
	}

	/**
	 * 释放资源
	 */
	public void dispose() {
		webrtcLooper.execute(new Runnable() {
			@Override
			public void run() {
				if (statsTimer != null) {
					statsTimer.cancel();
				}
				for (String tid : webrtcRemoteMap.keySet()) {
					WebrtcRemote webrtcRemote = webrtcRemoteMap.get(tid);
					if (webrtcRemote.peerConnection != null) {
						try {
							webrtcRemote.peerConnection.dispose();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					webrtcRemoteMap.clear();
				}
				if (localVideoSource != null) {
					localVideoSource.dispose();
				}
				if (peerConnectionFactory != null) {
					peerConnectionFactory.dispose();
				}
//				if (webrtcLooper != null) {
//					webrtcLooper.requestStop();
//				}
				PeerConnectionFactory.stopInternalTracingCapture();
				PeerConnectionFactory.shutdownInternalTracer();
			}
		});
	}

	/**
	 * 切换摄像头
	 */
	public void switchCamera() {
		webrtcLooper.execute(new Runnable() {
			@Override
			public void run() {
				if (videoCapturer != null) {
					videoCapturer.switchCamera(null);
				}
			}
		});
	}

	/**
	 * 本地视频开关
	 * 
	 * @param enable
	 */
	public void setLocalVideoEnabled(final boolean enable) {
		webrtcLooper.execute(new Runnable() {
			@Override
			public void run() {
				if (localVideoTrack != null) {
					localVideoTrack.setEnabled(enable);
				}
			}
		});
	}

	/**
	 * 本地音频开关
	 * 
	 * @param enable
	 */
	public void setLocalAudioEnabled(final boolean enable) {
		webrtcLooper.execute(new Runnable() {
			@Override
			public void run() {
				if (localAudioTrack != null) {
					localAudioTrack.setEnabled(enable);
				}
			}
		});
	}

	public class PCObserver implements PeerConnection.Observer {
		private String tid;

		public PCObserver(String tid) {
			this.tid = tid;
		}

		@Override
		public void onIceCandidate(final IceCandidate candidate) {
			events.onIceCandidate(candidate, tid);
		}

		@Override
		public void onSignalingChange(PeerConnection.SignalingState newState) {
			// Log.d(TAG, "SignalingState: " + newState);
		}

		@Override
		public void onIceConnectionChange(
				final PeerConnection.IceConnectionState newState) {
			MtcLog.d("onIceConnectionChange", newState.name());
			if (newState == IceConnectionState.CONNECTED) {
				events.onIceConnected(tid);
			} else if (newState == IceConnectionState.DISCONNECTED) {
				events.onIceDisconnected(tid);
			} else if (newState == IceConnectionState.CLOSED) {
				events.onIceDisconnected(tid);
			} else if (newState == IceConnectionState.FAILED) {
				reportError("ICE connection failed.", tid);
			}
		}

		@Override
		public void onIceGatheringChange(
				PeerConnection.IceGatheringState newState) {
			// Log.d(TAG, "IceGatheringState: " + newState);
		}

		@Override
		public void onIceConnectionReceivingChange(boolean receiving) {
			// Log.d(TAG, "IceConnectionReceiving changed to " + receiving);
		}

		@Override
		public void onAddStream(final MediaStream stream) {
			if (stream.audioTracks.size() > 1 || stream.videoTracks.size() > 1) {
				reportError("Weird-looking stream: " + stream, tid);
				return;
			}
			WebrtcRemote webrtcRemote = webrtcRemoteMap.get(tid);
			if (stream.videoTracks.size() == 1) {
				webrtcRemote.remoteVideoTrack = stream.videoTracks.get(0);
				webrtcRemote.remoteVideoTrack.setEnabled(true);
				webrtcRemote.remoteVideoTrack.addRenderer(new VideoRenderer(
						webrtcRemoteMap.get(tid).remoteRender));
			}
			if (stream.audioTracks.size() == 1) {
				webrtcRemote.remoteAudioTrack = stream.audioTracks.get(0);
				webrtcRemote.remoteAudioTrack.setEnabled(true);
			}
		}

		@Override
		public void onRemoveStream(final MediaStream stream) {
			// executor.execute(new Runnable() {
			// @Override
			// public void run() {
			// remoteVideoTrack = null;
			// }
			// });
		}

		@Override
		public void onDataChannel(final DataChannel dc) {
			reportError(
					"AppRTC doesn't use data channels, but got: " + dc.label()
							+ " anyway!", tid);
		}

		@Override
		public void onRenegotiationNeeded() {

		}
	}

	private class SDPObserver implements SdpObserver {
		private String tid;

		public SDPObserver(String tid) {
			this.tid = tid;
		}

		@Override
		public void onCreateSuccess(final SessionDescription origSdp) {
			WebrtcRemote webrtcRemote = webrtcRemoteMap.get(tid);
			String sdpDescription = origSdp.description;
			if (webrtcRemote.videoCallEnabled) {
				sdpDescription = preferCodec(sdpDescription,
						parameters.videoCodec, false);
			}
			final SessionDescription sdp = new SessionDescription(origSdp.type,
					sdpDescription);

			webrtcRemote.peerConnection.setLocalDescription(
					new SDPObserver(tid), sdp);
			localSdp = sdp;

			if (webrtcRemote.isInitiator) {
				// STEP 主叫方 步骤3 setLocalDescription
				MtcLog.i(WEBRTCSTEP, "主叫方 步骤3 setLocalDescription");
			} else {
				// STEP 被叫方 步骤4 调用setLocalDescription
				MtcLog.i(WEBRTCSTEP, "被叫方 步骤4 调用setLocalDescription");
			}
		}

		@Override
		public void onSetSuccess() {
			WebrtcRemote classes = webrtcRemoteMap.get(tid);
			if (classes.isInitiator) {
				if (classes.peerConnection.getRemoteDescription() == null) {
					events.onLocalDescription(localSdp, tid);
				} else {
					synchronized (classes.queuedRemoteCandidates) {
						if (classes.queuedRemoteCandidates != null) {
							for (IceCandidate candidate : classes.queuedRemoteCandidates) {
								classes.peerConnection.addIceCandidate(candidate);
							}
							classes.queuedRemoteCandidates = null;
						}
					}
				}
			} else {
				if (classes.peerConnection.getLocalDescription() != null) {
					events.onLocalDescription(localSdp, tid);
					synchronized (classes.queuedRemoteCandidates) {
						if (classes.queuedRemoteCandidates != null) {
							Iterator<IceCandidate> iterator = classes.queuedRemoteCandidates
									.iterator();
							while (iterator.hasNext()) {
								classes.peerConnection.addIceCandidate(iterator
										.next());
							}
							classes.queuedRemoteCandidates = null;
						}
					}
				} else {

				}
			}
		}

		@Override
		public void onCreateFailure(final String error) {
			reportError("createSDP error: " + error, tid);
		}

		@Override
		public void onSetFailure(final String error) {
			reportError("setSDP error: " + error, tid);
		}
	}

	private static String preferCodec(String sdpDescription, String codec,
			boolean isAudio) {
		String[] lines = sdpDescription.split("\r\n");
		int mLineIndex = -1;
		String codecRtpMap = null;
		String regex = "^a=rtpmap:(\\d+) " + codec + "(/\\d+)+[\r]?$";
		Pattern codecPattern = Pattern.compile(regex);
		String mediaDescription = "m=video ";
		if (isAudio) {
			mediaDescription = "m=audio ";
		}
		for (int i = 0; (i < lines.length)
				&& (mLineIndex == -1 || codecRtpMap == null); i++) {
			if (lines[i].startsWith(mediaDescription)) {
				mLineIndex = i;
				continue;
			}
			Matcher codecMatcher = codecPattern.matcher(lines[i]);
			if (codecMatcher.matches()) {
				codecRtpMap = codecMatcher.group(1);
				continue;
			}
		}
		if (mLineIndex == -1) {

			return sdpDescription;
		}
		if (codecRtpMap == null) {
			return sdpDescription;
		}

		String[] origMLineParts = lines[mLineIndex].split(" ");
		if (origMLineParts.length > 3) {
			StringBuilder newMLine = new StringBuilder();
			int origPartIndex = 0;
			newMLine.append(origMLineParts[origPartIndex++]).append(" ");
			newMLine.append(origMLineParts[origPartIndex++]).append(" ");
			newMLine.append(origMLineParts[origPartIndex++]).append(" ");
			newMLine.append(codecRtpMap);
			for (; origPartIndex < origMLineParts.length; origPartIndex++) {
				if (!origMLineParts[origPartIndex].equals(codecRtpMap)) {
					newMLine.append(" ").append(origMLineParts[origPartIndex]);
				}
			}
			lines[mLineIndex] = newMLine.toString();
		}
		StringBuilder newSdpDescription = new StringBuilder();
		for (String line : lines) {
			newSdpDescription.append(line).append("\r\n");
		}
		return newSdpDescription.toString();
	}

	private void reportError(final String errorMessage, String tid) {
		events.onPeerConnectionError(errorMessage, tid);
	}
	

	public void enableStatsEvents(boolean enable, int periodMs, final String tid) {
		if (enable) {
			try {
				statsTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						webrtcLooper.execute(new Runnable() {
							@Override
							public void run() {
								getStats(tid);
							}
						});
					}
				}, 0, periodMs);
			} catch (Exception e) {
				MtcLog.e("WebrtcClient", "Can not schedule statistics timer");
			}
		} else {
			statsTimer.cancel();
		}
	}
	
	private void getStats(final String tid) {
		if (webrtcRemoteMap.get(tid) == null || webrtcRemoteMap.get(tid).peerConnection == null) {
			return;
		}
		boolean success = webrtcRemoteMap.get(tid).peerConnection.getStats(new StatsObserver() {
			@Override
			public void onComplete(final StatsReport[] reports) {
				events.onPeerConnectionStatsReady(reports, tid);
			}
		}, null);
		if (!success) {
			MtcLog.e("WebrtcClient", "getStats() returns false!");
		}
	}
}
