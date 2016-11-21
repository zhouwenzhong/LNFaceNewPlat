package com.lianyao.ftf.sdk.layered;

import android.annotation.SuppressLint;

import com.lianyao.ftf.sdk.config.Constants;
import com.lianyao.ftf.sdk.inter.FTFCallback;
import com.lianyao.ftf.sdk.uitl.MtcLog;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class RtcUser {
	OkHttpClient mOkHttpClient = new OkHttpClient();

	@SuppressLint("TrulyRandom")
	public RtcUser() {
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[] { new X509TrustManager() {
				@Override
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			} }, new SecureRandom());
			mOkHttpClient.setSslSocketFactory(sc.getSocketFactory());
			mOkHttpClient.setHostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 注册
	@SuppressLint("TrulyRandom")
	public void FTF_Register(String appId, String mobile, String ticket,
			String imei, final FTFCallback callback) {
		MediaType JSON = MediaType.parse("application/json; charset=utf-8");
		String uuid = "";//UUID.randomUUID().toString();
		String pband = android.os.Build.BRAND;
		String ptype = android.os.Build.MODEL;
		if(ptype.length() > 32) {
			ptype = ptype.substring(0,32);
		}
		String content = "{\"rsc\":{\"user\": {\"appid\":\"" + appId + "\",\"name\":\"" + mobile
				+ "\"," + "\"tm\":{\"tid\":\"" + uuid + "\", \"ttype\":\"AD\", \"imei\":\"" + imei + "\",\"uuid\":\""+ uuid +
				"\",\"info\":{\"pband\":\"" + pband + "\",\"ptype\":\"" + ptype + "\"}}}}}";
		MtcLog.e("FTF_Register", content);
		RequestBody body = RequestBody.create(JSON, content);
		final Request request = new Request.Builder()
				.url(Constants.BASE_URL + "/user?ticket=" + ticket).post(body)
				.build();
		Call call = mOkHttpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Request arg0, IOException arg1) {
				callback.onFailure(arg0.body().toString() + ":"
						+ arg1.toString());
			}

			@Override
			public void onResponse(Response arg0) throws IOException {
				callback.onResponse(arg0.body().string(), arg0.code(), arg0.message());
			}
		});
	}

	// 注册刷新
	public void FTF_Refresh(String uid, String appId, String tid,
			String ticket, final FTFCallback callback) {

		MediaType JSON = MediaType.parse("application/json; charset=utf-8");
		RequestBody body = RequestBody.create(JSON,
				"{\"rsc\":{\"user\": {\"uid\":\"" + uid + "\",\"appid\":\""
						+ appId + "\",\"tm\":{\"tid\":\"" + tid + "\"}}}}");
		final Request request = new Request.Builder()
				.url(Constants.BASE_URL + "/user?ticket=" + ticket).post(body)
				.build();
		Call call = mOkHttpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Request arg0, IOException arg1) {
				callback.onFailure(arg0.body().toString() + ":"
						+ arg1.toString());
			}

			@Override
			public void onResponse(Response arg0) throws IOException {
				callback.onResponse(arg0.body().string(), arg0.code(), arg0.message());
			}
		});
	}

	// 个人信息
	public void FTF_Information(String userid, String token,
			final FTFCallback callback) {
		final Request request = new Request.Builder().url(
				Constants.BASE_URL + "/user/" + userid + "?token=" + token)
				.build();
		Call call = mOkHttpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Request arg0, IOException arg1) {
				callback.onFailure(arg0.body().toString() + ":"
						+ arg1.toString());
			}

			@Override
			public void onResponse(Response arg0) throws IOException {
				callback.onResponse(arg0.body().string(), arg0.code(), arg0.message());
			}
		});
	}

}
