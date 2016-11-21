package com.lianyao.ftf.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.lianyao.ftf.R;
import com.lianyao.ftf.util.Logger;
import com.lianyao.ftf.util.NetUtil;
import com.lianyao.ftf.util.http.RestInterface;
import com.lianyao.ftf.util.http.RestRequest;
import com.lidroid.xutils.exception.HttpException;

public abstract class BaseHasTopActivity extends Activity implements
		RestInterface {

	public View middleView;

	// 0无动画； 1从右到左（进入效果）；2从左到右（退出效果）；3淡入淡出；4淡出淡入
	public int startActivityAnim = 1;
	public int stopActivityAnim = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 同意头部布局
		setContentView(R.layout.activity_base_has_top);
		// 获取头部控件信息
		ImageView img_return = (ImageView) super.findViewById(R.id.img_return);
		img_return.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClickBackIcon();

			}
		});
		RelativeLayout rl_middle = (RelativeLayout) super
				.findViewById(R.id.rl_middle);
		// 添加布局
		middleView = LayoutInflater.from(this).inflate(layoutId(),
				(ViewGroup) null, false);
		middleView.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		rl_middle.addView(middleView);
		setViews();
	}

	/**
	 * 布局文件ID
	 */
	protected abstract int layoutId();

	/**
	 * LayoutId()之后调用该方法 用于实例化控件 数据初始化
	 */
	protected abstract void setViews();

	// 友盟统计需要的两个生命周期方法实现
	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
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
	// 显示进度条
	public RestRequest getRequest() {
		if(!NetUtil.isConnected(this)) {
			Toast.makeText(this, "请检查网络连接", Toast.LENGTH_LONG).show();
		} 
		return RestRequest.getInstance(this);
	}

	public RestRequest getRequestNoPop() {
		if(!NetUtil.isConnected(this)) {
			Toast.makeText(this, "请检查网络连接", Toast.LENGTH_LONG).show();
		}
		return RestRequest.getInstance(this);
	}

	// ViewUtil对象
	// public ViewUtil getViewUtil() {
	// return ViewUtil.getInstance(this);
	// }
	
	protected void setMiddleTitle(int titleResid) {
		((TextView) super.findViewById(R.id.tv_top_middle_title))
			.setText(titleResid);
	}

	/**
	 * 添加title
	 */
	protected void setTopBar(int titleResid, int endtitleResid,
			boolean isShowHome) {
		((TextView) super.findViewById(R.id.tv_top_middle_title))
				.setText(titleResid);
		if (endtitleResid != -1) {
			TextView tv_top_end_title = (TextView) findViewById(R.id.tv_top_end_title);
			tv_top_end_title.setText(endtitleResid);
			tv_top_end_title.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					onClickEndText();
				}
			});
			tv_top_end_title.setVisibility(View.VISIBLE);
		}

		if (isShowHome) {
			ImageView img_return_home = (ImageView) findViewById(R.id.img_return_home);
			img_return_home.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
//					onClickHomeIcon();
				}
			});
			img_return_home.setVisibility(View.VISIBLE);
		}
	}

	// @Override
	// public View findViewById(int id) {
	// return middleView.findViewById(id);
	// }

	// 返回时调用
	protected void onClickBackIcon() {
		finish();
	}

	protected void setEndTitle(int endtitleResid) {
		TextView tv_top_end_title = (TextView) findViewById(R.id.tv_top_end_title);
		if(endtitleResid == -1) {
			tv_top_end_title.setVisibility(View.GONE);
		} else {
			tv_top_end_title.setText(endtitleResid);
			tv_top_end_title.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					onClickEndText();
				}
			});
			tv_top_end_title.setVisibility(View.VISIBLE);
		}
	}
	// 点击头部end文字时执行
	protected void onClickEndText() {

	}

//	// 返回home
//	protected void onClickHomeIcon() {
//		Intent intent = new Intent(this, MainActivity.class);
//		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		startActivity(intent);
//		finish();// kill掉自己
//	}

	// Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		// 退出
		if ((Intent.FLAG_ACTIVITY_CLEAR_TOP & intent.getFlags()) != 0) {
			finish();
		}
	}

	@Override
	public void onLoading(long total, long current, boolean isUploading) {

	}

	@Override
	public void onSuccess(JSONObject json) {
		Logger.e(json.toString());
	}

	public void onFailure(HttpException error, String msg) {
		Logger.e(msg);
	}
	
}
