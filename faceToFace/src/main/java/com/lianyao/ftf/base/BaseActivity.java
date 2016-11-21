package com.lianyao.ftf.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.lianyao.ftf.R;
import com.lianyao.ftf.util.Logger;
import com.lianyao.ftf.util.NetUtil;
import com.lianyao.ftf.util.http.RestInterface;
import com.lianyao.ftf.util.http.RestRequest;
import com.lidroid.xutils.exception.HttpException;
import com.umeng.analytics.MobclickAgent;

/**
 * 基础Activity
 */
public abstract class BaseActivity extends Activity implements RestInterface {

	// 0无动画； 1从右到左（进入效果）；2从左到右（退出效果）；3淡入淡出；4淡出淡入
	public int startActivityAnim = 1;
	public int stopActivityAnim = 2;
	private ProgressDialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(layoutId());
		mDialog = new ProgressDialog(this, R.style.noTitleDialog);
		setViews();
	}

	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("SplashScreen"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
	    MobclickAgent.onResume(this);          //统计时长
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("SplashScreen"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
	    MobclickAgent.onPause(this);
	}
	
	/**
	 * 布局文件ID
	 */
	protected abstract int layoutId();

	/**
	 * LayoutId()之后调用该方法 用于实例化控件 数据初始化
	 */
	protected abstract void setViews();
	
	public void showDialog() {
		if(!this.isFinishing()) {
			if (mDialog == null) {
				mDialog = new ProgressDialog(this);
			}
			mDialog.setCanceledOnTouchOutside(false);
			mDialog.setCancelable(true);
			mDialog.show();
		}
	}

	// 进度条消失
	public void dismissDialog() {
		if(!this.isFinishing()) {
			if (mDialog != null) {
				mDialog.cancel();
			}
		}
	}

	// 页面跳转增加动画效果
	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		startAnim();
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		startAnim();
	}

	private void startAnim() {
		if (startActivityAnim == 1) {
			overridePendingTransition(R.anim.right_to_middle,
					R.anim.middle_to_40left);
		} else if (startActivityAnim == 2) {
			overridePendingTransition(R.anim.left30_to_middle,
					R.anim.middle_to_right);
		} else if (startActivityAnim == 3) {
			overridePendingTransition(android.R.anim.fade_in,
					android.R.anim.fade_out);
		} else if (startActivityAnim == 4) {
			overridePendingTransition(android.R.anim.fade_out,
					android.R.anim.fade_in);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mDialog != null) {
			mDialog.dismiss();
		}
	}

	// 页面退出增加动画效果
	@Override
	public void finish() {
		super.finish();
		if (stopActivityAnim == 1) {
			overridePendingTransition(R.anim.right_to_middle,
					R.anim.middle_to_40left);
		} else if (stopActivityAnim == 2) {
			overridePendingTransition(R.anim.left30_to_middle,
					R.anim.middle_to_right);
		} else if (stopActivityAnim == 3) {
			overridePendingTransition(android.R.anim.fade_in,
					android.R.anim.fade_out);
		} else if (stopActivityAnim == 4) {
			overridePendingTransition(android.R.anim.fade_out,
					android.R.anim.fade_in);
		}
	}

	// 异步请求对象
	public RestRequest getRequest() {
		if(!NetUtil.isConnected(this)) {
			Toast.makeText(this, "请检查网络连接", Toast.LENGTH_LONG).show();
		}
		return RestRequest.getInstance(this);
	}
	public RestRequest getRequestDialog() {
		if(!NetUtil.isConnected(this)) {
			Toast.makeText(this, "请检查网络连接", Toast.LENGTH_LONG).show();
		}
		showDialog();
		return RestRequest.getInstance(this);
	}

	@Override
	public void onLoading(long total, long current, boolean isUploading) {

	}

	@Override
	public void onSuccess(JSONObject json) {
		Logger.e(json.toString());
	}

	@Override
	public void onFailure(HttpException error, String msg) {
		Logger.e(msg);
	}
	
}
