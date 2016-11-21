package com.lianyao.ftf.view;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.lianyao.ftf.R;
import com.lianyao.ftf.base.BaseActivity;
import com.lianyao.ftf.util.ToastUtil;

public class Forget3Activity extends BaseActivity implements OnClickListener {

	private ImageView img_return;
	private EditText edit_password;
	private EditText edit_password_ok;
	private Button btn_finish;
	
	@Override
	protected int layoutId() {
		return R.layout.activity_start_forget_3;
	}

	@Override
	protected void setViews() {
		img_return = (ImageView) findViewById(R.id.img_return);
		img_return.setOnClickListener(this);
		edit_password = (EditText) findViewById(R.id.edit_password);
		edit_password_ok = (EditText) findViewById(R.id.edit_password_ok);
		btn_finish = (Button) findViewById(R.id.btn_finish);
		btn_finish.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.img_return:
			finish();
			break;
			
		case R.id.btn_finish:
			if(edit_password.getText().toString().length() == 0) {
				ToastUtil.showShort(Forget3Activity.this, "请输入新密码");
			} else if(!edit_password.getText().toString().equals(edit_password_ok.getText().toString())) {
				ToastUtil.showShort(Forget3Activity.this, "两次密码不一致");
			} else {
				JSONObject param = new JSONObject();
				param.put("mobile", getIntent().getStringExtra("mobile"));
				param.put("newPassword", edit_password.getText().toString());
				getRequest().setRestPost(this, "updatePwd.json", param);
			}
			break;
			
		default:
			break;
		}
	}
	
	@Override
	public void onSuccess(JSONObject json) {
		super.onSuccess(json);
		if("2".equals(json.getString("result"))) {
			ToastUtil.showShort(this, json.getString("message"));
		} else {
			ToastUtil.showShort(this, "密码修改成功");
			stopActivityAnim = 1;
			finish();
		}
	}
	
}
