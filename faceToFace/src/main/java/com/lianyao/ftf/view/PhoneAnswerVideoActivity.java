package com.lianyao.ftf.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.PowerManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lianyao.ftf.R;
import com.lianyao.ftf.base.BaseActivity;
import com.lianyao.ftf.sdk.RtcClient;
import com.lianyao.ftf.sdk.config.CallError;
import com.lianyao.ftf.sdk.config.CallState;
import com.lianyao.ftf.sdk.config.RtcBroadcast;
import com.lianyao.ftf.sdk.inter.CallStateListener;
import com.lianyao.ftf.sdk.inter.RtcConnectionListener;
import com.lianyao.ftf.sdk.layered.RtcCallManager;
import com.lianyao.ftf.sdk.uitl.MtcLog;
import com.lianyao.ftf.sdk.view.PercentFrameLayout;
import com.lianyao.ftf.util.AppUtil;
import com.lianyao.ftf.util.CommonUtil;
import com.lianyao.ftf.util.Logger;
import com.lianyao.ftf.util.ToastUtil;

import org.webrtc.SurfaceViewRenderer;

public class PhoneAnswerVideoActivity extends BaseActivity implements
        OnClickListener, CallStateListener {

    private ImageView img_cut;
    private ImageView img_mianti;
    private boolean isMianti = true;
    private TextView tv_mianti;
    private boolean isTonghua = false;
    private PowerManager.WakeLock mWakeLock;

    private boolean isVideo = true;
    private boolean isAudio = true;

    @SuppressWarnings("deprecation")
    @Override
    protected int layoutId() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag");
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        return R.layout.activity_phone_video;
    }

    @Override
    public void onResume() {
        super.onResume();
        mWakeLock.acquire();
    }

    @Override
    public void onPause() {
        super.onPause();
        mWakeLock.release();
    }

    private PercentFrameLayout localRenderLayout;
    private PercentFrameLayout remoteRenderLayout;
    private SurfaceViewRenderer localRender;
    private SurfaceViewRenderer remoteRender;
    private RtcClient client = RtcClient.getInstance();
    private RtcCallManager rtcCallManager = client.getCallManager();
    private TextView tv_phonetime;
    private TextView tv_phonenumber;
    private TextView tv_phone;
    private TextView tv_name;
    private TextView tv_time;
    private Handler handler = new Handler();
    private long time = 0;
    private String callNumber;
    private LinearLayout ll_answer;
    /**
     * 更新通话时间
     */
    private Runnable updateTime = new Runnable() {

        @Override
        public void run() {
            time += 1;
            tv_phonetime.setText(CommonUtil.formatTimeBySecond(time));
            tv_time.setText(CommonUtil.formatTimeBySecond(time));
            // li：1s后启动这个线程刷新时间，到控件上；1s后再发送循环；
            handler.postDelayed(this, 1000);
        }
    };


    @Override
    protected void setViews() {
        IntentFilter intentFilter = new IntentFilter(RtcBroadcast.onHangUp);
        registerReceiver(broadcastReceiver, intentFilter);
        AppUtil.wakeUpAndUnlock(this);

        isVideo = getIntent().getBooleanExtra("isVideo", false);
        isAudio = getIntent().getBooleanExtra("isAudio", true);

        img_cut = (ImageView) findViewById(R.id.img_cut);
        img_mianti = (ImageView) findViewById(R.id.img_mianti);
        tv_mianti = (TextView) findViewById(R.id.tv_mianti);
        img_mianti.setBackgroundResource(R.drawable.icon_jieting);
        tv_mianti.setText("接听");
        img_cut.setOnClickListener(this);
        img_mianti.setOnClickListener(this);
        if(isVideo) {
            findViewById(R.id.img_video).setOnClickListener(this);
        }
        findViewById(R.id.img_audio).setOnClickListener(this);
        findViewById(R.id.img_turn).setOnClickListener(this);

        localRenderLayout = (PercentFrameLayout) findViewById(R.id.local_video_layout);
        remoteRenderLayout = (PercentFrameLayout) findViewById(R.id.remote_video_layout);
        localRender = (SurfaceViewRenderer) findViewById(R.id.local_video_view);
        localRender.setZOrderMediaOverlay(true);
        remoteRender = (SurfaceViewRenderer) findViewById(R.id.remote_video_view);

        updateVideo(new int[]{65, 65, 35, 35}, new int[]{0, 0, 100, 100});
        rtcCallManager.setLocalSurfaceView(localRender);
        rtcCallManager.setRemoteSurfaceView(remoteRender);
        rtcCallManager.addCallStateListener(this);

        client.addConnectionListener(new RtcConnectionListener() {
            @Override
            public void onDisconnected(int error) {
                finish();
            }

            @Override
            public void onConnected() {
                finish();
            }
        });

        tv_phonenumber = (TextView) findViewById(R.id.tv_phonenumber);
        tv_phonenumber.setText(getIntent().getStringExtra("mobile"));

        tv_phonetime = (TextView) findViewById(R.id.tv_phonetime);
        tv_time = (TextView) findViewById(R.id.tv_time);
        ll_answer = (LinearLayout) findViewById(R.id.ll_answer);

        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_name.setText(getIntent().getStringExtra("nikename"));
        tv_phone.setText(getIntent().getStringExtra("mobile"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_cut:
                finish();
                break;

            case R.id.img_mianti:
                if (!isTonghua) {
                    isTonghua = true;
                    if(isMianti) {
                        img_mianti.setBackgroundResource(R.drawable.wai_3);
                    }else {
                        img_mianti.setBackgroundResource(R.drawable.jingyin_3);
                    }
                    tv_mianti.setText("免提");
                    if (getIntent().getBooleanExtra("isVideo", false)) {
                        rtcCallManager.setUserName(getIntent()
                                .getStringExtra("mobile"));
                        rtcCallManager.answerCall(this);
                    } else {
                        rtcCallManager.setUserName(getIntent()
                                .getStringExtra("mobile"));
                        rtcCallManager.answerAudioCall(this);
                    }
                } else {
                    isMianti = !isMianti;
                    if(isMianti) {
                        img_mianti.setBackgroundResource(R.drawable.wai_3);
                        ToastUtil.showShort(PhoneAnswerVideoActivity.this, "免提已开放！");
                    }else {
                        img_mianti.setBackgroundResource(R.drawable.jingyin_3);
                        ToastUtil.showShort(PhoneAnswerVideoActivity.this, "免提已关闭！");
                    }
                    rtcCallManager.getVideoCallHelper().speaker(isMianti);
                }
                break;
            case R.id.img_video:
                if (isVideo) {
                    rtcCallManager.getVideoCallHelper().stopVideo();
                } else {
                    rtcCallManager.getVideoCallHelper().restoreVideo();
                }
                isVideo = !isVideo;
//                switchVideo();
                if (isVideo) {
                    findViewById(R.id.img_video).setBackgroundResource(R.drawable.camera);
                    ToastUtil.showShort(PhoneAnswerVideoActivity.this, "视频已开启！");
                } else {
                    findViewById(R.id.img_video).setBackgroundResource(R.drawable.camera_no);
                    ToastUtil.showShort(PhoneAnswerVideoActivity.this, "视频已关闭！");
                }
                break;
            case R.id.img_audio:
                if (isAudio) {
                    rtcCallManager.getVideoCallHelper().stopAudio();
                } else {
                    rtcCallManager.getVideoCallHelper().restoreAudio();
                }
                isAudio = !isAudio;
                if (isAudio) {
                    findViewById(R.id.img_audio).setBackgroundResource(R.drawable.mai_6);
                    ToastUtil.showShort(PhoneAnswerVideoActivity.this, "声音已开启！");
                } else {
                    findViewById(R.id.img_audio).setBackgroundResource(R.drawable.mai_3);
                    ToastUtil.showShort(PhoneAnswerVideoActivity.this, "声音已关闭！");
                }
                break;
            case R.id.img_turn:
                rtcCallManager.getVideoCallHelper().switchCamera();
                break;

            default:
                break;
        }

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        if (cutFlag) {
            rtcCallManager.stopCall(getIntent()
                    .getStringExtra("mobile"));
        }
        MtcLog.i("通话时间：" + time + "秒");
        super.onDestroy();
    }

    private void updateVideo(int[] local, int[] remote) {
        remoteRenderLayout.setPosition(remote[0], remote[1], remote[2],
                remote[3]);
        remoteRender.requestLayout();
        localRenderLayout.setPosition(local[0], local[1], local[2], local[3]);
        localRender.requestLayout();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    private boolean cutFlag = true;

    @Override
    public void onCallStateChanged(final CallState callState, final CallError error) {
        AppUtil.wakeUpAndUnlock(this);
        Logger.e(callState + "--" + error);
//        if (callState != null || error != null) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    String msg = (callState == null) ? error + "" : callState + "";
//                    ToastUtil.showShort(PhoneAnswerVideoActivity.this, msg);
//                }
//            });
//        }
        if (callState != null) {
            switch (callState) {
                case CONNECTING:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showShort(PhoneAnswerVideoActivity.this, "正在拨号，请稍等。。。");
                        }
                    });
                    break;

                case DISCONNNECTED:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showShort(PhoneAnswerVideoActivity.this, "通过结束。");
                        }
                    });
                    cutFlag = false;
                    finish();
                    break;

                case CONNECTED:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showShort(PhoneAnswerVideoActivity.this, "对方已响铃。");
                        }
                    });
                    break;
                case ACCEPTED:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showShort(PhoneAnswerVideoActivity.this, "对方已接听。");
                        }
                    });
                    switchVideo();
                    handler.postDelayed(updateTime, 1000);
                    break;

                default:
                    break;
            }
        } else if (error != null) {
            String msg = "";
            switch (error) {
                case REJECTED:
                    msg = "对方已挂断。";
                case ERROR_NO_DATA:
                    msg = "对方号码不存在。";
                case ERROR_TRANSPORT:
                    msg = "数据传输错误。";
                case ERROR_INAVAILABLE:
                    msg = "对方不在线。";
                case ERROR_BUSY:
                    msg = "对方正在通话中。";
                case ERROR_NONENTITY:
                    msg = "对方号码不存在。";
                case ERROR_NORESPONSE:
                    msg = "对方未接通。";
                case ERROR_LOCAL_VERSION_SMALLER:
                case ERROR_PEER_VERSION_SMALLER:
                    final String message = msg;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showShort(PhoneAnswerVideoActivity.this, message);
                        }
                    });
                    finish();
                default:
                    break;
            }
        }
    }

    private void switchVideo() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isVideo) {
                    ll_answer.setVisibility(View.GONE);
                    remoteRenderLayout.setVisibility(View.VISIBLE);
                    localRenderLayout.setVisibility(View.VISIBLE);
                    updateVideo(new int[]{65, 65, 35, 35}, new int[]{0, 0, 100, 100});
                } else {
                    ll_answer.setVisibility(View.VISIBLE);
                    remoteRenderLayout.setVisibility(View.GONE);
                    localRenderLayout.setVisibility(View.GONE);
                }
            }
        });
    }

}
