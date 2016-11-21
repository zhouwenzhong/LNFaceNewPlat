package com.lianyao.ftf.view;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.lianyao.ftf.R;
import com.lianyao.ftf.base.BaseActivity;
import com.lianyao.ftf.util.ToastUtil;

public class Forget2Activity extends BaseActivity implements OnClickListener {

	private ImageView img_return;
	private TextView txt_sendmobile;
	private TextView tv_getcheck;
	private String code = "";
	private Button btn_next;
	private EditText edit_yanzhengma;
	
	@Override
	protected int layoutId() {
		return R.layout.activity_start_forget_2;
	}

	@Override
	protected void setViews() {
		img_return = (ImageView) findViewById(R.id.img_return);
		img_return.setOnClickListener(this);
		txt_sendmobile = (TextView) findViewById(R.id.txt_sendmobile);
		txt_sendmobile.setText(String.format(getString(R.string.verify_phone), getIntent().getStringExtra("mobile")));
		tv_getcheck = (TextView) findViewById(R.id.tv_getcheck);
		tv_getcheck.setOnClickListener(this);
		btn_next = (Button) findViewById(R.id.btn_next);
		btn_next.setOnClickListener(this);
		edit_yanzhengma = (EditText) findViewById(R.id.edit_yanzhengma);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.img_return:
			finish();
			break;
			
		case R.id.tv_getcheck:
			JSONObject param = new JSONObject();
			param.put("mobile", getIntent().getStringExtra("mobile"));
			getRequest().setRestPost(this, "sendMessage.json", param);
			break;
			
		case R.id.btn_next:
			if(code.length() == 6 && code.equals(edit_yanzhengma.getText().toString())) {
				Intent intent = new Intent(Forget2Activity.this, Forget3Activity.class);
				intent.putExtra("mobile", getIntent().getStringExtra("mobile"));
				startActivity(intent);
				stopActivityAnim = 1;
				finish();
			} else {
				ToastUtil.showShort(this, "验证码错误");
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
			code = json.getString("obj");
		}
	}
	
}
