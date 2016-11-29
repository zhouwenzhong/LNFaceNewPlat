package com.lianyao.ftf.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.lianyao.ftf.R;
import com.lianyao.ftf.base.BaseActivity;
import com.lianyao.ftf.config.Constants;
import com.lianyao.ftf.entry.Contact;
import com.lianyao.ftf.util.SPUtil;
import com.lianyao.ftf.util.ToastUtil;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

public class ContactDetailActivity extends BaseActivity implements
		OnClickListener {

	private ImageView img_return;
	private TextView tv_name;
	private TextView tv_phone;
	private TextView tv_delete;
	private ImageView img_audio;
	private ImageView img_video;
	private Contact friend = new Contact();
	private DbUtils db;

	@Override
	protected int layoutId() {
		return R.layout.activity_contact_detail;
	}

	@Override
	protected void setViews() {
		db = DbUtils.create(this, Constants.CONTACT_DB, 1, null);
		img_return = (ImageView) findViewById(R.id.img_return);
		img_return.setOnClickListener(this);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_phone = (TextView) findViewById(R.id.tv_phone);
		tv_name.setText(getIntent().getStringExtra("nickname"));
		tv_phone.setText(getIntent().getStringExtra("mobile"));
		img_audio = (ImageView) findViewById(R.id.img_audio);
		img_video = (ImageView) findViewById(R.id.img_video);
		tv_delete = (TextView) findViewById(R.id.tv_delete);
		tv_delete.setOnClickListener(this);
		img_audio.setOnClickListener(this);
		img_video.setOnClickListener(this);

		JSONObject param = new JSONObject();
		param.put("id", getIntent().getStringExtra("id"));
		getRequest().setRestPost(this, "getUserInfo.json", param);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_return:
			finish();
			break;

		case R.id.tv_delete:
			new AlertDialog.Builder(this).setTitle("确认删除吗？")
					.setIcon(android.R.drawable.ic_dialog_info)
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 点击“确认”后的操作
							try {
								Contact contact = db.findFirst(Selector.from(Contact.class).where(
										"mobile", "=", getIntent().getStringExtra("mobile")));
								if(contact != null) {
									db.delete(contact);
								}
								setResult(1, getIntent());
								ContactDetailActivity.this.finish();
							}catch (DbException ex) {
								ToastUtil.showShort(ContactDetailActivity.this, "联系人删除失败");
							}
						}
					})
					.setNegativeButton("取消", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 点击“返回”后的操作,这里不设置没有任何操作
						}
					}).show();
			break;

		case R.id.img_audio:
			if ((friend.getUserId() + "").equals(SPUtil.get(this, "id", "-1"))) {
				ToastUtil.showShort(this, "不能和自己通信");
			} else {
//				Intent intent = new Intent(ContactDetailActivity.this,
//						PhoneAudioActivity.class);
//				intent.putExtra("nickname", friend.getNickname());
//				intent.putExtra("mobile", friend.getMobile());
//				intent.putExtra("tid", friend.getTid());
//				startActivity(intent);
				Intent intent = new Intent(ContactDetailActivity.this,
						PhoneVideoActivity.class);
				intent.putExtra("nickname", friend.getNickname());
				intent.putExtra("mobile", friend.getMobile());
				intent.putExtra("video", false);
				startActivity(intent);
			}
			break;

		case R.id.img_video:
			if ((friend.getUserId() + "").equals(SPUtil.get(this, "id", "-1"))) {
				ToastUtil.showShort(this, "不能和自己通信");
			} else {
				Intent intent = new Intent(ContactDetailActivity.this,
						PhoneVideoActivity.class);
				intent.putExtra("nickname", friend.getNickname());
				intent.putExtra("mobile", friend.getMobile());
				startActivity(intent);

			}
			break;

		default:
			break;

		}
	}

	@Override
	public void onSuccess(JSONObject json) {
		super.onSuccess(json);
		JSONObject friendObj = json.getJSONObject("obj");
		friend.setUserId(friendObj.getLong("id"));
		friend.setMobile(friendObj.getString("name"));
		friend.setNickname(friendObj.getString("nickname"));
		friend.setTid(friendObj.getString("tid"));
		friend.setUid(friendObj.getString("uid"));
		friend.setHeadUrl(friendObj.getString("headUrl"));
	}

}
