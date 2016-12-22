package com.lianyao.ftf.base;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONObject;
import com.lianyao.ftf.R;
import com.lianyao.ftf.util.Logger;
import com.lianyao.ftf.util.http.RestInterface;
import com.lianyao.ftf.util.http.RestRequest;
import com.lidroid.xutils.exception.HttpException;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseFragment extends Fragment implements RestInterface {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(layoutId(), container, false);
		setViews(rootView);
		return rootView;

	}
	
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("MainScreen"); //统计页面，"MainScreen"为页面名称，可自定义
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("MainScreen"); 
	}

	// 页面跳转增加动画效果
	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		getActivity().overridePendingTransition(R.anim.right_to_middle, R.anim.middle_to_40left);
	}
	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		getActivity().overridePendingTransition(R.anim.right_to_middle, R.anim.middle_to_40left);
	}


	// 异步请求对象
	public RestRequest getRequest() {
		return RestRequest.getInstance(getActivity());
	}
		
	/**
	 * 布局文件ID
	 */
	protected abstract int layoutId();

	/**
	 * LayoutId()之后调用
	 */
	protected abstract void setViews(View rootView);

	@Override
	public void onLoading(long total, long current, boolean isUploading) {
		
	}

	@Override
	public void onSuccess(JSONObject json) {
		
	}

	@Override
	public void onFailure(HttpException error, String msg) {
		Logger.e(error.toString() + ":" + msg);
	}
}
