package com.my.bean;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class GetLocalVersion {

	public static final String apk_info_path = "";

	static String version_name = "";

	public static String getLocalVersionCode(Context context)

	{
		PackageManager paManager = context.getPackageManager();
		try {
			PackageInfo pInfo = paManager.getPackageInfo(context.getPackageName(), 0);
			version_name = pInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return version_name;
	}

}
