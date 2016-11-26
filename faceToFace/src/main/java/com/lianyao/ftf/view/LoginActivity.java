package com.lianyao.ftf.view;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.lianyao.ftf.MainApplication;
import com.lianyao.ftf.R;
import com.lianyao.ftf.base.BaseActivity;
import com.lianyao.ftf.sdk.RtcClient;
import com.lianyao.ftf.sdk.inter.LoginCallback;
import com.lianyao.ftf.util.AppUtil;
import com.lianyao.ftf.util.CommonUtil;
import com.lianyao.ftf.util.SPUtil;
import com.lianyao.ftf.util.ToastUtil;

public class LoginActivity extends BaseActivity implements OnClickListener,
		LoginCallback {

	private EditText edit_username;
	private EditText edit_password;
	private TextView txt_forget_password;
	private Button btn_login;
	private TextView txt_registe;

	@Override
	protected int layoutId() {
		return R.layout.activity_start_login;
	}

	@Override
	protected void setViews() {
		edit_username = (EditText) findViewById(R.id.edit_username);
		edit_password = (EditText) findViewById(R.id.edit_password);
		txt_forget_password = (TextView) findViewById(R.id.txt_forget_password);
		btn_login = (Button) findViewById(R.id.btn_login);
		txt_registe = (TextView) findViewById(R.id.txt_registe);
		txt_forget_password.setOnClickListener(this);
		btn_login.setOnClickListener(this);
		txt_registe.setOnClickListener(this);
		String uname = SPUtil.get(this, "username", "").toString();
		if (!CommonUtil.isEmpty(uname)) {
			edit_username.setText(uname);
			edit_password.setFocusable(true);
			edit_password.requestFocus();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txt_forget_password:
			Intent intent2 = new Intent(LoginActivity.this,
					Forget1Activity.class);
			startActivity(intent2);
			break;

		case R.id.btn_login:
			if(!AppUtil.isNetWorkAvailable(LoginActivity.this)) {
				ToastUtil.showShort(this.getApplicationContext(), "请检查网络连接");
				return;
			}
			btn_login.setClickable(false);
			if(!RtcClient.isInited()) {
				((MainApplication)getApplication()).initSdk();
			}
			btn_login.setClickable(false);
			JSONObject param = new JSONObject();
			param.put("mobile", edit_username.getText().toString());
			param.put("password", edit_password.getText().toString());
			SPUtil.put(this, "mobile", edit_username.getText().toString());
			SPUtil.put(this, "password", edit_password.getText().toString());
			getRequestDialog().setRestPost(this, "login.json", param);
			btn_login.setClickable(true);
			break;

		case R.id.txt_registe:
			Intent intent = new Intent(LoginActivity.this, RegistFirstActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	@Override
	public void onSuccess(JSONObject json) {
		dismissDialog();
		String result = json.getString("result");
		if (!result.equals("1")) {
			ToastUtil.showShort(this, json.getString("message"));
		} else {
			JSONObject obj = json.getJSONObject("obj");
			String mobile = obj.getString("uid").split("_")[1];
			String token = obj.getString("token");
			SPUtil.put(this, "id", obj.getString("id"));
			SPUtil.put(this, "mobile", mobile);
			SPUtil.put(this, "token", token);
			RtcClient.getInstance().login(mobile, token, this);
		}
	}

	@Override
	public void onSussess() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				SPUtil.put(LoginActivity.this, "username", edit_username
						.getText().toString());
				Intent intent = new Intent(LoginActivity.this,
						ContactActivity.class);
				startActivity(intent);
				ToastUtil.showShort(LoginActivity.this, "onSussess");
				finish();
			}

		});

	}

	@Override
	public void onFailure(int state, final String message) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				ToastUtil
						.showShort(LoginActivity.this, "onFailure>>" + message);
			}
		});

	}

	@Override
	public void onUpdateToken(final String tInfo) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO 保存新获取到的token
				ToastUtil.showShort(LoginActivity.this, "onUpdateToken>>"
						+ tInfo);
				SPUtil.put(LoginActivity.this, "token", tInfo);
			}
		});

	}
}
