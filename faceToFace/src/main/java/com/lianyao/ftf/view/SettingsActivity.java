package com.lianyao.ftf.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lianyao.ftf.R;
import com.lianyao.ftf.base.BaseActivity;
import com.lianyao.ftf.util.AppUtil;
import com.lianyao.ftf.util.SPUtil;

public class SettingsActivity extends BaseActivity implements OnClickListener {

	private TextView tv_account;
	private TextView tv_version;
	private TextView tv_audio; // 音频
	private LinearLayout ll_audio;
	private TextView tv_video; // 视频
	private LinearLayout ll_video;

	@Override
	protected int layoutId() {
		return R.layout.activity_settings;
	}

	@Override
	protected void setViews() {
		findViewById(R.id.img_return).setOnClickListener(this);
		tv_account = (TextView) findViewById(R.id.tv_account);
		tv_account.setText(SPUtil.get(this, "mobile", "").toString());
		tv_version = (TextView) findViewById(R.id.tv_version);
		tv_version.setText(AppUtil.getVersionName(this));
		tv_audio = (TextView) findViewById(R.id.tv_audio);
		tv_audio.setText(SPUtil.get(this, "yinpin", "OPUS").toString());
		ll_audio = (LinearLayout) findViewById(R.id.ll_audio);
		ll_audio.setOnClickListener(this);
		
		tv_video = (TextView) findViewById(R.id.tv_video);
		tv_video.setText(SPUtil.get(this, "shipin", "H264").toString());
		ll_video = (LinearLayout) findViewById(R.id.ll_video);
		ll_video.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_return:
			finish();
			break;
		
		case R.id.ll_audio:
			new AlertDialog.Builder(this).setTitle("音频编码")
					.setSingleChoiceItems(new String[] { "OPUS", "ISAC" }, 
							SPUtil.get(getBaseContext(), "yinpin", "OPUS").equals("OPUS")?0:1, 
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if(which == 0) {
								SPUtil.put(SettingsActivity.this, "yinpin", "OPUS");
							} else if(which == 1) {
								SPUtil.put(SettingsActivity.this, "yinpin", "ISAC");
							}
							tv_audio.setText(SPUtil.get(SettingsActivity.this, "yinpin", "OPUS").toString());
							dialog.dismiss();
						}
					}).setNegativeButton("取消", null).show();
			break;
			
		case R.id.ll_video:
			new AlertDialog.Builder(this).setTitle("视频编码")
			.setSingleChoiceItems(new String[] {"VP8", "VP9" }, 
					SPUtil.get(getBaseContext(), "shipin", "VP8").equals("VP8")?0:1, 
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if(which == 0) {
						SPUtil.put(SettingsActivity.this, "shipin", "VP8");
					} else if(which == 1) {
						SPUtil.put(SettingsActivity.this, "shipin", "VP9");
					}
					tv_video.setText(SPUtil.get(SettingsActivity.this, "shipin", "VP8").toString());
					dialog.dismiss();
				}
			}).setNegativeButton("取消", null).show();
			break;
			
		}
	}

}
