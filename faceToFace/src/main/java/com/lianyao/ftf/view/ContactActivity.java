package com.lianyao.ftf.view;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lianyao.ftf.ConnectionChangeReceiver;
import com.lianyao.ftf.R;
import com.lianyao.ftf.base.BaseActivity;
import com.lianyao.ftf.sdk.RtcClient;
import com.lianyao.ftf.sdk.inter.LoginCallback;
import com.lianyao.ftf.util.SPUtil;

public class ContactActivity extends BaseActivity implements OnClickListener {

	private LinearLayout ll_contact, ll_number, ll_my;
	private ImageView img_contact, img_number, img_my;
	private TextView tv_contact, tv_number, tv_my;

	private int oldIndex = 0;

	@Override
	protected int layoutId() {
		return R.layout.activity_contact;
	}

	@Override
	protected void setViews() {
		registerReceiver();
		ll_contact = (LinearLayout) findViewById(R.id.ll_contact);
		ll_number = (LinearLayout) findViewById(R.id.ll_number);
		ll_my = (LinearLayout) findViewById(R.id.ll_my);

		img_contact = (ImageView) findViewById(R.id.img_contact);
		img_number = (ImageView) findViewById(R.id.img_number);
		img_my = (ImageView) findViewById(R.id.img_my);

		tv_contact = (TextView) findViewById(R.id.tv_contact);
		tv_number = (TextView) findViewById(R.id.tv_number);
		tv_my = (TextView) findViewById(R.id.tv_my);

		ll_contact.setOnClickListener(this);
		ll_number.setOnClickListener(this);
		ll_my.setOnClickListener(this);

		setChangeMiddleView(1);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		String mobile = (String) SPUtil.get(this, "mobile", "");
		String token = (String) SPUtil.get(this, "token", "");
		RtcClient.getInstance().login(mobile, token, new LoginCallback() {
			@Override
			public void onUpdateToken(String tInfo) {
			}
			
			@Override
			public void onSussess() {
			}
			
			@Override
			public void onFailure(int state, String message) {
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_contact:
			img_contact.setBackgroundResource(R.drawable.icon_lianxiren_h);
			tv_contact.setTextColor(getResources().getColor(R.color.cblue));
			img_number.setBackgroundResource(R.drawable.icon_number);
			tv_number.setTextColor(getResources().getColor(R.color.c666));
			img_my.setBackgroundResource(R.drawable.icon_wode);
			tv_my.setTextColor(getResources().getColor(R.color.c666));
			setChangeMiddleView(1);
			break;

		case R.id.ll_number:
			img_contact.setBackgroundResource(R.drawable.icon_lianxiren);
			tv_contact.setTextColor(getResources().getColor(R.color.c666));
			img_number.setBackgroundResource(R.drawable.icon_number_h);
			tv_number.setTextColor(getResources().getColor(R.color.cblue));
			img_my.setBackgroundResource(R.drawable.icon_wode);
			tv_my.setTextColor(getResources().getColor(R.color.c666));
			setChangeMiddleView(2);
			break;

		case R.id.ll_my:
			img_contact.setBackgroundResource(R.drawable.icon_lianxiren);
			tv_contact.setTextColor(getResources().getColor(R.color.c666));
			img_number.setBackgroundResource(R.drawable.icon_number);
			tv_number.setTextColor(getResources().getColor(R.color.c666));
			img_my.setBackgroundResource(R.drawable.icon_wode_h);
			tv_my.setTextColor(getResources().getColor(R.color.cblue));
			setChangeMiddleView(3);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(myReceiver);
		RtcClient.getInstance().logOut();
//		RtcClient.getInstance().release();
	}

	private ContactFragment homeFragment;
	private NumberFragment numberFragment;
	private MyFragment myFragment;

	private void setChangeMiddleView(int i) {
		if (oldIndex != i) {
			FragmentManager fm = getFragmentManager();
			FragmentTransaction transaction = fm.beginTransaction();
			transaction.setCustomAnimations(android.R.animator.fade_in,
					android.R.animator.fade_out);
			if (i == 1) {
				if (homeFragment == null) {
					homeFragment = new ContactFragment();
				}
				transaction.replace(R.id.middle, homeFragment);
			} else if (i == 2) {
				if (numberFragment == null) {
					numberFragment = new NumberFragment();
				}
				transaction.replace(R.id.middle, numberFragment);
			} else if (i == 3) {
				if (myFragment == null) {
					myFragment = new MyFragment();
				}
				transaction.replace(R.id.middle, myFragment);
			}
			oldIndex = i;
			transaction.commit();
		}
	}

	ConnectionChangeReceiver myReceiver;

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION);
		myReceiver = new ConnectionChangeReceiver();
		this.registerReceiver(myReceiver, filter);
	}

	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
//		Intent intent = new Intent(Intent.ACTION_MAIN);
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent.addCategory(Intent.CATEGORY_HOME);
//		startActivity(intent);
	}
}
