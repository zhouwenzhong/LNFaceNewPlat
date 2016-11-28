package com.lianyao.ftf.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.lianyao.ftf.R;
import com.lianyao.ftf.base.BaseActivity;
import com.lianyao.ftf.config.UpdateInfo;
import com.lianyao.ftf.sdk.RtcClient;
import com.lianyao.ftf.sdk.inter.LoginCallback;
import com.lianyao.ftf.update.AppUpdate;
import com.lianyao.ftf.update.AppUpdateService;
import com.lianyao.ftf.update.Version;
import com.lianyao.ftf.util.AppUtil;
import com.lianyao.ftf.util.CommonUtil;
import com.lianyao.ftf.util.SPUtil;
import com.lianyao.ftf.util.ToastUtil;
import com.lianyao.ftf.util.http.RestInterface;
import com.lidroid.xutils.exception.HttpException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SplashActivity extends BaseActivity implements Animation.AnimationListener, LoginCallback, AppUpdateService.OnCliListener {

	private ImageView startActivity_bg_img;
	// 更新版本要用到的一些信息
	private UpdateInfo info = new UpdateInfo();;
	private ProgressDialog pBar;
	@SuppressLint("HandlerLeak")

	private Handler handler1 = new Handler() {
		public void handleMessage(Message msg) {
			// 如果有更新就提示
			if (isNeedUpdate()) {   //在下面的代码段
				showUpdateDialog();  //下面的代码段
			}else {
				startApp();
			}
		};
	};

	private void showUpdateDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle("请升级APP至版本" + info.getName());
		builder.setMessage(info.getFeature());
		builder.setCancelable(false);

		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					downFile(info.getTargetUrl());     //在下面的代码段
				} else {
					Toast.makeText(SplashActivity.this, "升级失败",
							Toast.LENGTH_SHORT).show();
					startApp();
				}
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				startApp();
			}

		});
		builder.setCancelable(false);
		builder.create().show();
	}

	private boolean isNeedUpdate() {
		int code;
		try {
			PackageManager packageManager = getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					getPackageName(), 0);
			code = packageInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			code = 0;
		}
		if (info.getCode()<=code) {
			return false;
		} else {
			return true;
		}
	}

	// 获取当前版本的版本号
	private String getVersion() {
		try {
			PackageManager packageManager = getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					getPackageName(), 0);
			return packageInfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			return "版本号未知";
		}
	}
	void downFile(final String url) {
		pBar = new ProgressDialog(SplashActivity.this);    //进度条，在下载的时候实时更新进度，提高用户友好度
		pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pBar.setTitle("正在下载");
		pBar.setMessage("请稍候...");
		pBar.setProgress(0);
		pBar.show();
		new Thread() {
			public void run() {
				HttpURLConnection urlcon = null;
				URL urll;
				try {
//					HttpClient client = new DefaultHttpClient();
//					HttpGet get = new HttpGet(url);
//					HttpResponse response;
					urll = new URL(url);
					urlcon = (HttpURLConnection)urll.openConnection();
					int length = (int) urlcon.getContentLength();   //获取文件大小
					pBar.setMax(length);
					InputStream is = urlcon.getInputStream();
					FileOutputStream fileOutputStream = null;
					if (is != null) {
						File file = new File(
								Environment.getExternalStorageDirectory(),
								"LNFaceToFace.apk");
						fileOutputStream = new FileOutputStream(file);
						byte[] buf = new byte[4096];   //缓冲区
						int ch = -1;
						int process = 0;
						while ((ch = is.read(buf)) != -1) {
							fileOutputStream.write(buf, 0, ch);
							process += ch;
							pBar.setProgress(process);       //这里就是关键的实时更新进度了
						}

					}
					fileOutputStream.flush();
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}
					down();
				}catch (Exception e) {
					e.printStackTrace();
					startApp();

				}
			}

		}.start();
	}

	void down() {
		handler1.post(new Runnable() {
			public void run() {
				pBar.cancel();
				update();
			}
		});
	}
	//安装文件，一般固定写法
	void update() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(Environment
						.getExternalStorageDirectory(), "LNFaceToFace.apk")),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}

	public class GetUpdateInfoResult implements RestInterface {
		@Override
		public void onSuccess(JSONObject json) {
			String result = json.getString("result");
			if (!result.equals("1")) {
				startApp();
			} else {
				JSONObject obj = json.getJSONObject("obj");
				int code = obj.getIntValue("code");
//				String name = obj.getString("name");
//				String feature = obj.getString("feature");
//				String url = obj.getString("downLoadUrl");
//
//				info.setCode(code);
//				info.setName(name);
//				info.setFeature(feature);
//				info.setTargetUrl(url);
//				handler1.sendEmptyMessage(0);

				downloadUrl = obj.getString("downLoadUrl");
				Version version = new Version(obj.getIntValue("code"),
						obj.getString("name"), obj.getString("feature"),
						downloadUrl);
				if (comparedWithCurrentPackage(version)) {
					appUpdate.setOnCliListener(SplashActivity.this);
					appUpdate.onFoundLatestV(version);
				} else {
					startApp();
				}
			}
		}
		public void onLoading(long total, long current, boolean isUploading) {

		}
		public void onFailure(HttpException error, String msg) {
			startApp();
		}
	}

	void startApp() {
		startActivity_bg_img = (ImageView) findViewById(R.id.startActivity_bg_img);

		AnimationSet animationSet = new AnimationSet(true);
		AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0.2f);
		ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 1.2f, 1f,
				1.2f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		animationSet.setDuration(3000);
		animationSet.addAnimation(alphaAnimation);
		animationSet.addAnimation(scaleAnimation);
		animationSet.setFillAfter(true);
		animationSet.setAnimationListener(SplashActivity.this);
		startActivity_bg_img.startAnimation(animationSet);
	}

	boolean comparedWithCurrentPackage(Version version) {
		if (version == null)
			return false;
		int currentVersionCode = 0;
		try {
			PackageInfo pkg = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			currentVersionCode = pkg.versionCode;
		} catch (PackageManager.NameNotFoundException exp) {
			exp.printStackTrace();
		}

		return version.code > currentVersionCode;
	}

	private AppUpdate appUpdate;

	@Override
	public void onResume() {
		super.onResume();
		appUpdate.callOnResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		appUpdate.callOnPause();
	}

	@Override
	public void OnOkListener() {
		Uri uri = Uri.parse(downloadUrl);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
		finish();
	}

	/**
	 * 取消
	 */
	@Override
	public void CeOkListener() {
		startApp();
	}

	/**
	 * 更新
	 */
	String downloadUrl = "";

	@Override
	protected int layoutId() {
		appUpdate = AppUpdateService.getAppUpdate(this);
		return R.layout.activity_start_splash;
	}

	@Override
	protected void setViews() {
		Toast.makeText(SplashActivity.this, "正在检查版本更新..", Toast.LENGTH_SHORT).show();

		// 自动检查有没有新版本 如果有新版本就提示更新
		JSONObject param = new JSONObject();
		param.put("type", "1");//手机
		getRequest().setRestPost(new GetUpdateInfoResult(), "getVersionInfo.json", param);

		if (AppUtil.isPhone(this)) {
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("该安装包仅适用于手机");
			builder.setMessage("您好，该该安装包为手机版安装包，机顶盒可去下载对应的机顶盒版本。");
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							finish();
						}
					});
			builder.show();
		}
	}

	@Override
	public void onAnimationEnd(Animation arg0) {
		startActivityAnim = 3;
		stopActivityAnim = 3;

		String uname = SPUtil.get(this, "username", "").toString();
		String password = SPUtil.get(this, "password", "").toString();
		if(CommonUtil.isEmpty(uname) || CommonUtil.isEmpty(password)) {
			startActivity(new Intent(this, LoginActivity.class));
			finish();
		} else {
			JSONObject param = new JSONObject();
			param.put("mobile", uname);
			param.put("password", password);
			getRequest().setRestPost(this, "login.json", param);
		}
	}

	@Override
	public void onSuccess(JSONObject json) {
		dismissDialog();
		String result = json.getString("result");
		if (!result.equals("1")) {
			ToastUtil.showShort(this, json.getString("message"));
			startActivity(new Intent(this, LoginActivity.class));
			finish();
		} else {
			JSONObject obj = json.getJSONObject("obj");
			String mobile = obj.getString("uid").split("_")[1];
//			String mobile = obj.getString("uid").substring(0, obj.getString("uid").indexOf("_"));
			String token = obj.getString("token");
			String headUrl = obj.getString("headUrl");
			SPUtil.put(this, "id", obj.getString("id"));
			SPUtil.put(this, "mobile", mobile);
			SPUtil.put(this, "token", token);
			if(!TextUtils.isEmpty(headUrl)) SPUtil.put(this, "headUrl", headUrl);
			RtcClient.getInstance().login(mobile, token, this);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onFailure(HttpException error, String msg) {
		super.onFailure(error, msg);
		startActivity(new Intent(this, LoginActivity.class));
		finish();
	}

	@Override
	public void onSussess() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(SplashActivity.this,
						ContactActivity.class);
				startActivity(intent);
//				ToastUtil.showShort(SplashActivity.this, "onSussess");
				finish();
			}

		});

	}

	@Override
	public void onFailure(int state, String message) {
		startActivity(new Intent(this, LoginActivity.class));
		finish();
	}

	@Override
	public void onUpdateToken(final String tInfo) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO 保存新获取到的token
//				ToastUtil.showShort(SplashActivity.this, "onUpdateToken>>"
//						+ tInfo);
				SPUtil.put(SplashActivity.this, "token", tInfo);
			}
		});

	}

	@Override
	public void onAnimationRepeat(Animation arg0) {

	}

	@Override
	public void onAnimationStart(Animation arg0) {

	}
}
