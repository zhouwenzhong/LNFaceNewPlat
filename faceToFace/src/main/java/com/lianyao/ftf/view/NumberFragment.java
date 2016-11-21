package com.lianyao.ftf.view;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.lianyao.ftf.R;
import com.lianyao.ftf.base.BaseFragment;
import com.lianyao.ftf.util.CommonUtil;
import com.lianyao.ftf.util.SPUtil;
import com.lianyao.ftf.util.ToastUtil;

public class NumberFragment extends BaseFragment implements OnClickListener {

	private EditText edit_number;
	private TextView tv_number_1;
	private TextView tv_number_2;
	private TextView tv_number_3;
	private TextView tv_number_4;
	private TextView tv_number_5;
	private TextView tv_number_6;
	private TextView tv_number_7;
	private TextView tv_number_8;
	private TextView tv_number_9;
	private TextView tv_number_0;
	private TextView tv_number_c;
	private TextView tv_number_d;
	private TextView tv_audio;
	private TextView tv_video;
	private View view;
	private int clickFlag = 0; //1语音 2视频
	
	@Override
	protected int layoutId() {
		return R.layout.fragment_contact_2;
	}

	@Override
	protected void setViews(View rootView) {
		view = rootView;
		edit_number = (EditText) rootView.findViewById(R.id.edit_number);
		tv_number_1 = (TextView) rootView.findViewById(R.id.tv_number_1);
		tv_number_2 = (TextView) rootView.findViewById(R.id.tv_number_2);
		tv_number_3 = (TextView) rootView.findViewById(R.id.tv_number_3);
		tv_number_4 = (TextView) rootView.findViewById(R.id.tv_number_4);
		tv_number_5 = (TextView) rootView.findViewById(R.id.tv_number_5);
		tv_number_6 = (TextView) rootView.findViewById(R.id.tv_number_6);
		tv_number_7 = (TextView) rootView.findViewById(R.id.tv_number_7);
		tv_number_8 = (TextView) rootView.findViewById(R.id.tv_number_8);
		tv_number_9 = (TextView) rootView.findViewById(R.id.tv_number_9);
		tv_number_0 = (TextView) rootView.findViewById(R.id.tv_number_0);
		tv_number_c = (TextView) rootView.findViewById(R.id.tv_number_c);
		tv_number_d = (TextView) rootView.findViewById(R.id.tv_number_d);
		tv_audio = (TextView) rootView.findViewById(R.id.tv_audio);
		tv_video = (TextView) rootView.findViewById(R.id.tv_video);
		tv_number_1.setOnClickListener(this);
		tv_number_2.setOnClickListener(this);
		tv_number_3.setOnClickListener(this);
		tv_number_4.setOnClickListener(this);
		tv_number_5.setOnClickListener(this);
		tv_number_6.setOnClickListener(this);
		tv_number_7.setOnClickListener(this);
		tv_number_8.setOnClickListener(this);
		tv_number_9.setOnClickListener(this);
		tv_number_0.setOnClickListener(this);
		tv_number_c.setOnClickListener(this);
		tv_number_d.setOnClickListener(this);
		tv_audio.setOnClickListener(this);
		tv_video.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.tv_number_1:
			edit_number.setText(edit_number.getText().toString() + "1");
			break;
			
		case R.id.tv_number_2:
			edit_number.setText(edit_number.getText().toString() + "2");
			break;
			
		case R.id.tv_number_3:
			edit_number.setText(edit_number.getText().toString() + "3");
			break;
			
		case R.id.tv_number_4:
			edit_number.setText(edit_number.getText().toString() + "4");
			break;
			
		case R.id.tv_number_5:
			edit_number.setText(edit_number.getText().toString() + "5");
			break;

		case R.id.tv_number_6:
			edit_number.setText(edit_number.getText().toString() + "6");
			break;
			
		case R.id.tv_number_7:
			edit_number.setText(edit_number.getText().toString() + "7");
			break;
			
		case R.id.tv_number_8:
			edit_number.setText(edit_number.getText().toString() + "8");
			break;
			
		case R.id.tv_number_9:
			edit_number.setText(edit_number.getText().toString() + "9");
			break;
			
		case R.id.tv_number_0:
			edit_number.setText(edit_number.getText().toString() + "0");
			break;
			
		case R.id.tv_number_c:
			edit_number.setText("");
			break;
			
		case R.id.tv_number_d:
			if(edit_number.getText().toString().length() > 0) {
				edit_number.setText(edit_number.getText().toString()
						.substring(0, edit_number.getText().toString().length() - 1));
			}
			break;
		
		case R.id.tv_audio:
			clickFlag = 1;
			String mobile = edit_number.getText().toString().trim();
			if(CommonUtil.isEmpty(mobile)) {
				ToastUtil.showShort(view.getContext(), "未输入号码");
			} else {
				JSONObject param = new JSONObject();
				param.put("mobile", mobile);
				getRequest().setRestPost(this, "getUserInfoByMobile.json", param);
			}
			break;
			
		case R.id.tv_video:
			clickFlag = 2;
			String mobile2 = edit_number.getText().toString().trim();
			if(CommonUtil.isEmpty(mobile2)) {
				ToastUtil.showShort(view.getContext(), "未输入号码");
			} else {
				JSONObject param = new JSONObject();
				param.put("mobile", mobile2);
				getRequest().setRestPost(this, "getUserInfoByMobile.json", param);
			}
			break;
			
		default:
			break;
		}
	}
	
	@Override
	public void onSuccess(JSONObject json) {
		super.onSuccess(json);
		if(json.getString("result").equals("2")) {
			ToastUtil.showShort(view.getContext(), "用户不存在");
			return;
		}
		json = json.getJSONObject("obj");
		if (json.getString("id").equals(SPUtil.get(view.getContext(), "id", "-1"))) {
			ToastUtil.showShort(view.getContext(), "不能和自己通信");
		} else if(clickFlag == 1) {
//			Intent intent = new Intent(view.getContext(),
//					PhoneAudioActivity.class);
//			intent.putExtra("nickname", json.getString("nickname"));
//			intent.putExtra("mobile", json.getString("name"));
//			intent.putExtra("tid", json.getString("tid"));
//			startActivity(intent);
			Intent intent = new Intent(view.getContext(),
					PhoneVideoActivity.class);
			intent.putExtra("nickname", json.getString("nickname"));
			intent.putExtra("mobile", json.getString("name"));
			intent.putExtra("video", false);
			startActivity(intent);
		} else if(clickFlag == 2) {
			Intent intent = new Intent(view.getContext(),
					PhoneVideoActivity.class);
			intent.putExtra("nickname", json.getString("nickname"));
			intent.putExtra("mobile", json.getString("name"));
			intent.putExtra("tid", json.getString("tid"));
			startActivity(intent);
		}
		
	}

}
