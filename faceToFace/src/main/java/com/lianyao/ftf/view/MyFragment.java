package com.lianyao.ftf.view;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.lianyao.ftf.R;
import com.lianyao.ftf.base.BaseFragment;
import com.lianyao.ftf.sdk.RtcClient;
import com.lianyao.ftf.util.SPUtil;

public class MyFragment extends BaseFragment {

	private TextView tv_nickname;
	private TextView tv_phone;
	private RelativeLayout rl_settings;
	private View view;

	@Override
	protected int layoutId() {
		return R.layout.fragment_contact_3;
	}

	@Override
	protected void setViews(View rootView) {
		view = rootView;
		tv_nickname = (TextView) view.findViewById(R.id.tv_nickname);
		tv_phone = (TextView) view.findViewById(R.id.tv_phone);
		rl_settings = (RelativeLayout) view.findViewById(R.id.rl_settings);
		rl_settings.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(view.getContext(),
						SettingsActivity.class);
				startActivity(intent);
			}
		});
		view.findViewById(R.id.tv_out).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						RtcClient.getInstance().logOut();
//						RtcClient.getInstance().release();
						SPUtil.remove(getActivity(), "password");
						SPUtil.remove(getActivity(), "tid");
						SPUtil.remove(getActivity(), "token");
						Intent intent = new Intent(view.getContext(),
								LoginActivity.class);
						startActivity(intent);
						getActivity().finish();
					}
				});

		JSONObject param = new JSONObject();
		param.put("id", SPUtil.get(view.getContext(), "id", "-1"));
		getRequest().setRestPost(this, "getUserInfo.json", param);
	}

	@Override
	public void onSuccess(JSONObject json) {
		super.onSuccess(json);
		JSONObject obj = json.getJSONObject("obj");
		tv_nickname.setText(obj.getString("nickname"));
		tv_phone.setText(obj.getString("name"));
	}

}
