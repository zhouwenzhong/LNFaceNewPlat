package com.lianyao.ftf.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.lianyao.ftf.MainApplication;
import com.lianyao.ftf.R;
import com.lianyao.ftf.base.BaseActivity;
import com.lianyao.ftf.base.BaseFragment;
import com.lianyao.ftf.sdk.RtcClient;
import com.lianyao.ftf.update.AppUpdate;
import com.lianyao.ftf.update.AppUpdateService;
import com.lianyao.ftf.update.Version;
import com.lianyao.ftf.util.SPUtil;
import com.lianyao.ftf.util.ToastUtil;
import com.lianyao.ftf.util.http.RestInterface;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyFragment extends BaseFragment implements OnClickListener {

	private TextView tv_nickname;
	private TextView tv_phone;
	private LinearLayout ll_newversion;
	private RelativeLayout rl_settings;
	private View view;
	private CircleImageView iv_head;
	public static final int TAKE_PHOTO=1;
	public static final int CROP_PHOTO=2;

	/**
	 * 头像地址
	 */
	private Uri imageUri;
	/**
	 * 更新url
	 */
	String downloadUrl = "";
	private AppUpdate appUpdate;

	@Override
	protected int layoutId() {
		appUpdate = AppUpdateService.getAppUpdate(getActivity());
		return R.layout.fragment_contact_3;
	}

	@Override
	protected void setViews(View rootView) {
		view = rootView;
		tv_nickname = (TextView) view.findViewById(R.id.tv_nickname);
		tv_phone = (TextView) view.findViewById(R.id.tv_phone);
		iv_head = (CircleImageView) view.findViewById(R.id.iv_head);
		rl_settings = (RelativeLayout) view.findViewById(R.id.rl_settings);
		ll_newversion = (LinearLayout) view.findViewById(R.id.ll_newversion);
		ll_newversion.setOnClickListener(this);

		iv_head.setOnClickListener(this);

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

						new AlertDialog.Builder(getActivity()).setTitle("确认退出吗？")
								.setIcon(android.R.drawable.ic_dialog_info)
								.setPositiveButton("确定", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										// 点击“确认”后的操作
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
								})
								.setNegativeButton("取消", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										// 点击“返回”后的操作,这里不设置没有任何操作
									}
								}).show();
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
		String url = obj.getString("headUrl");
		MainApplication.imageLoader.displayImage(url, iv_head, MainApplication.options);
	}

	@Override
	public void onActivityResult(int requestCode,int resultCode,Intent data){
		if(resultCode == getActivity().RESULT_OK) {
			switch(requestCode){
				case TAKE_PHOTO:
					if(resultCode==getActivity().RESULT_OK){
						Intent intent=new Intent("com.android.camera.action.CROP");
						intent.setDataAndType(imageUri,"image/*");
						intent.putExtra("scale",true); //开启剪裁
						intent.putExtra("aspectX", 1); // 宽高比例
						intent.putExtra("aspectY", 1);
						intent.putExtra("outputX", 150); // 宽高
						intent.putExtra("outputY", 150);
						intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
						startActivityForResult(intent,CROP_PHOTO);//启动裁剪程序
					}
					break;
				case CROP_PHOTO:
					if(resultCode==getActivity().RESULT_OK){
						try{
							Bitmap bitmap= BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
							iv_head.setImageBitmap(bitmap);//将裁剪后的照片显示出来

							File photoFile = new File(Environment.getExternalStorageDirectory()
									.getPath() + "/touxiang.png");
							FileOutputStream b = new FileOutputStream(photoFile);
							bitmap.compress(Bitmap.CompressFormat.JPEG, 50, b);

							String id = (String) SPUtil.get(view.getContext(), "id", "-1");
							((BaseActivity)getActivity()).getRequest().setRestPostFile(new UploadFileRest(), id+"", "avatar", photoFile);

						}catch(FileNotFoundException e){
							e.printStackTrace();
						}
					}
					break;

				default:
					break;
			}
		}
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
			case R.id.iv_head:
				final CharSequence[] items = { "相册", "拍照" };
				AlertDialog dlg = new AlertDialog.Builder(getActivity())
						.setItems(items, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
												int which) {
								if (which == 1) { //拍照
									File outputImage=new File(Environment.getExternalStorageDirectory(),new Date().getTime() + ".png");
									try{
										if(outputImage.exists()){
											outputImage.delete();
										}
										outputImage.createNewFile();
									}catch(IOException e){
										e.printStackTrace();
									}
									imageUri=Uri.fromFile(outputImage);
									Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
									intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
									startActivityForResult(intent,TAKE_PHOTO);//启动相机程序
								} else {
									File outputImage=new File(Environment.getExternalStorageDirectory(),new Date().getTime() + ".png");
									Intent intent = new Intent(Intent.ACTION_PICK);
									intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");//相片类型
									imageUri=Uri.fromFile(outputImage);
									intent.putExtra("output", imageUri);
									intent.putExtra("crop", "true");
									intent.putExtra("aspectX", 1);// 裁剪框比例
									intent.putExtra("aspectY", 1);
									intent.putExtra("outputX", 150);// 输出图片大小
									intent.putExtra("outputY", 150);
									startActivityForResult(intent, CROP_PHOTO);
								}

							}
						}).create();
				dlg.show();
				break;
			case R.id.ll_newversion:
				Toast.makeText(getActivity(), "正在检查版本更新..", Toast.LENGTH_SHORT).show();

				// 自动检查有没有新版本 如果有新版本就提示更新
				JSONObject param = new JSONObject();
				param.put("type", "1");//手机
				getRequest().setRestPost(new GetUpdateInfoResult(), "getVersionInfo.json", param);
				break;
		}
	}

	public class GetUpdateInfoResult implements RestInterface {
		@Override
		public void onSuccess(JSONObject json) {
			String result = json.getString("result");
			if (!result.equals("1")) {
				ToastUtil.showShort(getActivity(), "获取版本信息失败");
			} else {
				JSONObject obj = json.getJSONObject("obj");
				int code = obj.getIntValue("code");

				downloadUrl = obj.getString("downLoadUrl");
				Version version = new Version(obj.getIntValue("code"),
						obj.getString("name"), obj.getString("feature"),
						downloadUrl);
				if (comparedWithCurrentPackage(version)) {
					appUpdate.setOnCliListener(new UpdateOnCliListener());
					appUpdate.onFoundLatestV(version);
				} else {
					ToastUtil.showShort(getActivity(), "当前已是最新版本");
				}
			}
		}
		public void onLoading(long total, long current, boolean isUploading) {

		}
		public void onFailure(HttpException error, String msg) {
			ToastUtil.showShort(getActivity(), "获取版本信息失败");
		}
	}

	public class UpdateOnCliListener implements AppUpdateService.OnCliListener {

		@Override
		public void OnOkListener() {
			Uri uri = Uri.parse(downloadUrl);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
			getActivity().finish();
		}

		@Override
		public void CeOkListener() {

		}
	}

	boolean comparedWithCurrentPackage(Version version) {
		if (version == null)
			return false;
		int currentVersionCode = 0;
		try {
			PackageInfo pkg = getActivity().getPackageManager().getPackageInfo(
					getActivity().getPackageName(), 0);
			currentVersionCode = pkg.versionCode;
		} catch (PackageManager.NameNotFoundException exp) {
			exp.printStackTrace();
		}

		return version.code > currentVersionCode;
	}

	@Override
	public void onResume() {
		super.onResume();
		appUpdate.callOnResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		appUpdate.callOnPause();
	}

	class UploadFileRest implements RestInterface {

		@Override
		public void onLoading(long total, long current, boolean isUploading) {

		}

		@Override
		public void onSuccess(JSONObject json) {
			String result = json.getString("result");
			if(result != null || result.equals("1")) {
				String url = json.getString("obj");
				MainApplication.imageLoader.displayImage(url, iv_head, MainApplication.options);
			}
		}

		@Override
		public void onFailure(HttpException error, String msg) {
			ToastUtil.showShort(getActivity(), "头像上传失败");
		}
	}
}
