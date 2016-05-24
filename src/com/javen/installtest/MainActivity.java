package com.javen.installtest;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 仿360手机助手秒装和智能安装功能的主Activity。
 */
public class MainActivity extends Activity {

	TextView apkPathText;

	String apkPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		apkPathText = (TextView) findViewById(R.id.apkPathText);
		Button id_clickMe=(Button)findViewById(R.id.id_clickMe);
		//启动之后模拟点击显示Toast
		setSimulateClick(id_clickMe, 10, 5);
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0 && resultCode == RESULT_OK) {
			apkPath = data.getStringExtra("apk_path");
			apkPathText.setText(apkPath);
		}
	}

	public void onChooseApkFile(View view) {
		Intent intent = new Intent(this, FileExplorerActivity.class);
		startActivityForResult(intent, 0);
	}

	public void onSilentInstall(View view) {
		if (!isRoot()) {
			Toast.makeText(this, "没有ROOT权限，不能使用秒装", Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(apkPath)) {
			Toast.makeText(this, "请选择安装包！", Toast.LENGTH_SHORT).show();
			return;
		}
		final Button button = (Button) view;
		button.setText("安装中");
		new Thread(new Runnable() {
			@Override
			public void run() {
				// SilentInstall installHelper = new SilentInstall();
				// final boolean result = installHelper.install(apkPath);
				final boolean result = ApkController.clientInstall(apkPath);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (result) {
							Toast.makeText(MainActivity.this, "安装成功！",
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(MainActivity.this, "安装失败！",
									Toast.LENGTH_SHORT).show();
						}
						button.setText("秒装");
					}
				});

			}
		}).start();

	}
	/**
	 * 静默卸载
	 * @param view
	 */
	public void onSilentUninstall(View view) {
		String packageName = "org.cocos2dx.benbenyu";

		if (!isRoot()) {
			Toast.makeText(this, "没有ROOT权限，不能使用秒装", Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(packageName)) {
			Toast.makeText(this, "请输入卸载程序的包名！", Toast.LENGTH_SHORT).show();
			return;
		}
		final Button button = (Button) view;
		button.setText("卸载中");
		final boolean result = ApkController.uninstall(packageName, this);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (result) {
					Toast.makeText(MainActivity.this, "卸装成功！",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(MainActivity.this, "卸载失败！",
							Toast.LENGTH_SHORT).show();
				}
				button.setText("秒卸");
			}
		});
	}
	
	public void onForwardToAccessibility(View view) {
		Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
		startActivity(intent);
	}
	
	public void onSmartInstall(View view) {
		if (TextUtils.isEmpty(apkPath)) {
			Toast.makeText(this, "请选择安装包！", Toast.LENGTH_SHORT).show();
			return;
		}
		Uri uri = Uri.fromFile(new File(apkPath));
		Intent localIntent = new Intent(Intent.ACTION_VIEW);
		localIntent.setDataAndType(uri,
				"application/vnd.android.package-archive");
		startActivity(localIntent);
	}

	/**
	 * 判断手机是否拥有Root权限。
	 * 
	 * @return 有root权限返回true，否则返回false。
	 */
	public boolean isRoot2() {
		boolean bool = false;
		try {
			bool = (new File("/system/bin/su").exists())
					|| (new File("/system/xbin/su").exists());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bool;
	}

	/**
	 * 判断手机是否拥有Root权限。
	 * 
	 * @return 有root权限返回true，否则返回false。
	 */
	public boolean isRoot() {
		boolean bool = false;
		try {
			if (Runtime.getRuntime().exec("su").getOutputStream() == null) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bool;
	}
	 public void clickMe(View view) {
		    Toast.makeText(this, "clicked", Toast.LENGTH_LONG).show();
	}
	 
	 
	//模拟点击事件
	private void setSimulateClick(View view, float x, float y) {
		long downTime = SystemClock.uptimeMillis();
		final MotionEvent downEvent = MotionEvent.obtain(downTime, downTime,
				MotionEvent.ACTION_DOWN, x, y, 0);
		downTime += 1000;
		final MotionEvent upEvent = MotionEvent.obtain(downTime, downTime,
				MotionEvent.ACTION_UP, x, y, 0);
		view.onTouchEvent(downEvent);
		view.onTouchEvent(upEvent);
		downEvent.recycle();
		upEvent.recycle();
	}

}
