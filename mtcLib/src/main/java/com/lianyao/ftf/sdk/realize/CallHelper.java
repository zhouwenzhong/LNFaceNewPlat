package com.lianyao.ftf.sdk.realize;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;

import com.lianyao.ftf.sdk.config.MtcCodec;

/**
 * 震动 响铃 控制类
 * 
 * @author guochao
 * 
 */
public final class CallHelper {
	// 来电是否震动
	public static boolean isShock = false;

	// 来电是否响铃
	public static boolean isRing = false;

	// 来电声音文件名Id
	public static int ringfileRawId;
	// 来电声音文件名Uri
	public static Uri ringfileUri;

	// 呼叫是否响铃
	public static boolean isCallRing = false;

	// 呼叫声音文件名Id
	public static int callRingfileRawId;
	// 呼叫声音文件名Uri
	public static Uri callRingfileUri;

	public static String videoCodec = MtcCodec.VIDEO_CODEC_VP8;

	public static String audioCodec = MtcCodec.AUDIO_CODEC_OPUS;

	/**
	 * 设置扬声器
	 * 
	 * @param flag
	 */
	public void setSpeaker(boolean flag, Context context) {
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		if (flag) {
			audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
			audioManager.setSpeakerphoneOn(true);
		} else {
			audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
			audioManager.setSpeakerphoneOn(false);
		}
	}

	private Vibrator vibrator;
	private MediaPlayer mPlayer;

	/**
	 * 开始 响铃 震动
	 */
	public void beganComeCall(Context context) {
		if (isShock && vibrator == null) {
			vibrator = (Vibrator) context
					.getSystemService(Service.VIBRATOR_SERVICE);
			vibrator.vibrate(new long[] { 700, 700 }, 0);
		}
		if (isRing && (ringfileRawId != 0 || ringfileUri != null) && mPlayer == null) {
			if(ringfileRawId != 0) {
				mPlayer = MediaPlayer.create(context, ringfileRawId);
			}else {
				mPlayer = MediaPlayer.create(context, ringfileUri);
			}
			mPlayer.setLooping(true);
			mPlayer.start();
		}
	}

	/**
	 * 呼叫 开始 响铃
	 */
	public void beganCall(Context context) {
		if (isCallRing && (callRingfileRawId != 0 || callRingfileUri != null) && mPlayer == null) {
			if(callRingfileRawId != 0) {
				mPlayer = MediaPlayer.create(context, callRingfileRawId);
			}else {
				mPlayer = MediaPlayer.create(context, callRingfileUri);
			}
			mPlayer.setLooping(true);
			mPlayer.start();
		}
	}

	/**
	 * 结束 响铃 震动
	 */
	public void endComeCall() {
		if (vibrator != null) {
			vibrator.cancel();
			vibrator = null;
		}
		if (mPlayer != null) {
			mPlayer.stop();
			mPlayer = null;
		}
	}
}
