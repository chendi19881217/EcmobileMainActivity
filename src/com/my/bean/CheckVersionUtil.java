package com.my.bean;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Xml;

public class CheckVersionUtil implements Runnable {

	public final int UPDATA_NONEED = 0;
	public final int UPDATA_CAN = 1;
	public final int UPDATA_ERR = 2;

	public Context context;
	public Handler handler;
	public String apk_info_path;

	public String version_name = "";
	InputStream is;

	public CheckVersionUtil(Context context, Handler handler, String apk_info_path) {

		this.context = context;
		this.handler = handler;
		this.apk_info_path = apk_info_path;

		version_name = GetLocalVersion.getLocalVersionCode(context);

	}

	Message msg = null;

	@Override
	public void run() {

		if (version_name.equals("")) {

			msg = handler.obtainMessage();
			msg.arg1 = UPDATA_ERR;
			msg.obj = "版本检测失败";
			handler.sendMessage(msg);
			return;

		}

		URL url;
		try {
			url = new URL(apk_info_path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			int responseCode = conn.getResponseCode();
			if (responseCode == 200) {
				// 从服务器获得一个输入流
				is = conn.getInputStream();
			}

			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(is, "utf-8");
			int type = parser.getEventType();
			UpdataInfo info = new UpdataInfo();
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:
					if ("version".equals(parser.getName())) {
						info.setVersion(parser.nextText());
					} else if ("url".equals(parser.getName())) {
						info.setUrl(parser.nextText());
					} else if ("description".equals(parser.getName())) {
						info.setDescription(parser.nextText());
					}
					break;
				}
				type = parser.next();
			}

			if (Double.parseDouble(version_name) < Double.parseDouble(info.getVersion())) {
				msg = handler.obtainMessage();
				msg.arg1 = UPDATA_CAN;
				msg.obj = info.getUrl();

			} else {
				msg = handler.obtainMessage();
				msg.arg1 = UPDATA_NONEED;
				msg.obj = "已经是最新版的app了";

			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
			msg = handler.obtainMessage();
			msg.arg1 = UPDATA_ERR;
			msg.obj = e.getMessage();
		} catch (ProtocolException e) {
			e.printStackTrace();
			msg = handler.obtainMessage();
			msg.arg1 = UPDATA_ERR;
			msg.obj = e.getMessage();
		} catch (IOException e) {
			e.printStackTrace();
			msg = handler.obtainMessage();
			msg.arg1 = UPDATA_ERR;
			msg.obj = e.getMessage();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			msg = handler.obtainMessage();
			msg.arg1 = UPDATA_ERR;
			msg.obj = e.getMessage();
		}

		handler.sendMessage(msg);

	}

}
