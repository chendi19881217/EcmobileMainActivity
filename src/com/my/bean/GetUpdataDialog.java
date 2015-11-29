package com.my.bean;

import java.io.File;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

public class GetUpdataDialog {

	static GetUpdataDialog getUpdataDialog;

	public static GetUpdataDialog getInstance() {

		if (getUpdataDialog == null) {
			getUpdataDialog = new GetUpdataDialog();
		}

		return getUpdataDialog;

	}

	public AlertDialog GetDialog(final Context context, String msg, final String apk_url, final Handler handle) {

		AlertDialog.Builder builer = new Builder(context);
		builer.setTitle("版本升级");
		builer.setMessage(msg);
		// 当点确定按钮时从服务器上下载 新的apk 然后安装 װ
		builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Log.i(TAG, "下载apk,更新");
				downLoadApk(context, handle, apk_url);
			}
		});
		builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// do sth
			}
		});
		AlertDialog dialog = builer.create();
		// dialog.show();
		return dialog;
	}

	/*
	 * 从服务器中下载APK
	 */
	protected void downLoadApk(final Context context, final Handler handle, final String apk_url) {
		final ProgressDialog pd; // 进度条对话框
		pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("正在下载更新");
		pd.show();
		new Thread() {
			@Override
			public void run() {
				try {
					File file = DownLoadManager.getFileFromServer(apk_url, pd);
					sleep(2000);
					installApk(context, file);
					pd.dismiss(); // 结束掉进度条对话框
				} catch (Exception e) {
					e.printStackTrace();
					Message msg = handle.obtainMessage();
					msg.arg1 = 3;
					msg.obj = e.getMessage();
					handle.sendMessage(msg);
					pd.dismiss(); // 结束掉进度条对话框

				}
			}
		}.start();
	}

	// 安装apk
	protected void installApk(Context context, File file) {
		Intent intent = new Intent();
		// 执行动作
		intent.setAction(Intent.ACTION_VIEW);
		// 执行的数据类型
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		context.startActivity(intent);
	}

}
