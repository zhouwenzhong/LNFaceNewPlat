package com.lianyao.ftf.update;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;

import com.lianyao.ftf.R;

public class FoundVersionDialog {

	private Context context;
	private Version version;
	private VersionDialogListener listener;

	public FoundVersionDialog(Context context, Version version,
			VersionDialogListener listener) {
		this.context = context;
		this.version = version;
		this.listener = listener;
	}

	public void show() {
		final Dialog dialog = new Dialog(context, R.style.dialog);
		dialog.setContentView(R.layout.dialog_found_version);

		TextView title = (TextView) dialog.findViewById(R.id.title);
		TextView feature = (TextView) dialog.findViewById(R.id.feature);
		title.setText(String
				.format(context.getResources().getString(
						R.string.latest_version_title), version.name));
		feature.setText(version.feature);

		View ignore = dialog.findViewById(R.id.ignore);
		View update = dialog.findViewById(R.id.update);
		final CheckBox laterOnWifi = (CheckBox) dialog
				.findViewById(R.id.only_wifi);

		if (NetworkUtil.getNetworkType(context) != NetworkUtil.WIFI) {
			laterOnWifi.setVisibility(View.VISIBLE);
		} else {
			laterOnWifi.setVisibility(View.GONE);
		}

		ignore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				listener.doIgnore();
			}
		});

		update.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				boolean laterOnWifiFlag = laterOnWifi.isChecked();
				listener.doUpdate(laterOnWifiFlag);
			}
		});

		dialog.show();

		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		int width = metrics.heightPixels;
		int height = metrics.widthPixels;
		if (height > width) {
			lp.width = (int) (width * 0.9);
		} else {
			lp.width = (int) (width * 0.5);
		}
		dialog.getWindow().setAttributes(lp);
	}
}
