package com.lianyao.ftf.view;

import org.json.JSONException;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.lianyao.ftf.R;
import com.lianyao.ftf.base.BaseActivity;
import com.lianyao.ftf.sdk.RtcClient;
import com.lianyao.ftf.sdk.inter.RegisterCallBack;
import com.lianyao.ftf.util.AppUtil;
import com.lianyao.ftf.util.CommonUtil;
import com.lianyao.ftf.util.Logger;
import com.lianyao.ftf.util.ToastUtil;

public class RegistActivity extends BaseActivity implements OnClickListener,
		RegisterCallBack {

	private ImageView img_return;
	private EditText edit_nickname;
	private TextView tv_username;
	private EditText edit_password;
	private EditText edit_password_ok;
	private Button btn_registe;
	String mobile;

	@Override
	protected int layoutId() {
		return R.layout.activity_start_regist;
	}

	@Override
	protected void setViews() {
		img_return = (ImageView) findViewById(R.id.img_return);
		edit_nickname = (EditText) findViewById(R.id.edit_nickname);
		tv_username = (TextView) findViewById(R.id.tv_username);
		edit_password = (EditText) findViewById(R.id.edit_password);
		edit_password_ok = (EditText) findViewById(R.id.edit_password_ok);
		btn_registe = (Button) findViewById(R.id.btn_registe);
		mobile = getIntent().getStringExtra("mobile");
		tv_username.setText(mobile);
		img_return.setOnClickListener(this);
		btn_registe.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_return:
			finish();
			break;

		case R.id.btn_registe:
			String nickname = edit_nickname.getText().toString();
			String password = edit_password.getText().toString();
			String password_ok = edit_password_ok.getText().toString();
			if (CommonUtil.isEmpty(nickname)
					|| CommonUtil.isEmpty(password)
					|| CommonUtil.isEmpty(password_ok)) {
				ToastUtil.showShort(this, "请输入完整内容");
			} else if (!password.equals(password_ok)) {
				ToastUtil.showShort(this, "两次密码不一致");
			} else {
				RtcClient.getInstance().register(mobile, this);
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onSuccess(JSONObject json) {
		super.onSuccess(json);
		if (json.containsKey("result") && json.getString("result").equals("2")) {
			ToastUtil.showShort(this, json.getString("message"));
		} else {
			ToastUtil.showShort(this, "注册成功");
			finish();
		}
	}

	@Override
	public void onSuccess(org.json.JSONObject jsonUser) {
		Logger.i(jsonUser.toString());
		try {
			org.json.JSONObject jsonTm = jsonUser.getJSONObject("tm");
			String tid = jsonTm.getString("tid");
			String token = jsonTm.getString("token");
			String uid = jsonUser.getString("uid");
			
			JSONObject param = new JSONObject();
			param.put("imei", AppUtil.getDeviceIMEI(this));
			param.put("mobile", mobile);
			param.put("nickname", edit_nickname.getText().toString());
			param.put("password", edit_password.getText().toString());
			param.put("type", "1");
			param.put("uid", uid);
			param.put("tid", tid);
			param.put("token", token);
			getRequest().setRestPost(this, "registe.json", param);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onError(String message) {
		ToastUtil.showShort(this, message);
		// TODO 注册失败

	}

}
