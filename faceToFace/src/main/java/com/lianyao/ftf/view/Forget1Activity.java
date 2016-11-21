package com.lianyao.ftf.view;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.lianyao.ftf.R;
import com.lianyao.ftf.base.BaseActivity;

public class Forget1Activity extends BaseActivity implements OnClickListener {

	private ImageView img_return;
	private EditText edit_username;
	private Button btn_next;
	
	@Override
	protected int layoutId() {
		return R.layout.activity_start_forget_1;
	}

	@Override
	protected void setViews() {
		img_return = (ImageView) findViewById(R.id.img_return);
		img_return.setOnClickListener(this);
		edit_username = (EditText) findViewById(R.id.edit_username);
		btn_next = (Button) findViewById(R.id.btn_next);
		btn_next.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.img_return:
			finish();
			break;
			
		case R.id.btn_next:
			Intent intent = new Intent(Forget1Activity.this, Forget2Activity.class);
			intent.putExtra("mobile", edit_username.getText().toString());
			startActivity(intent);
			stopActivityAnim = 1;
			finish();
			break;
			
		default:
			break;
		}
	}

}
