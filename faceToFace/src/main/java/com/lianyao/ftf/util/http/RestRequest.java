package com.lianyao.ftf.util.http;

import org.apache.http.entity.StringEntity;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.lianyao.ftf.config.Constants;
import com.lianyao.ftf.util.Logger;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class RestRequest {

	// private Context mContext; // 得到对应请求的Activity

	private static RestRequest restRequest = null;// 单例模式

	private RestRequest(Context context) {
		// this.mContext = context;
	}

	public static RestRequest getInstance(Context context) {
		if (restRequest == null) {
			synchronized (RestRequest.class) {
				if (restRequest == null) {
					restRequest = new RestRequest(context);
				}
			}
		}
		return restRequest;
	}

	public void setRestPost(final RestInterface restInterface,
			String serviceName, JSONObject param) {
		HttpUtils http = new HttpUtils();
		RequestParams requestParams = new RequestParams();
		requestParams.setContentType("application/json");
		try {
			requestParams.setBodyEntity(new StringEntity(param.toString(),
					"UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		String reqStr = Constants.BASE_URL + serviceName;

		Logger.e(reqStr);

		http.send(HttpRequest.HttpMethod.POST, reqStr, requestParams,
				new RequestCallBack<String>() {
					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
						restInterface.onLoading(total, current, isUploading);
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						Logger.e(responseInfo.result);
						try {
							restInterface.onSuccess(JSONObject
									.parseObject(responseInfo.result));
						} catch(Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onStart() {
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						restInterface.onFailure(error, msg);
					}
				});
	}

}
