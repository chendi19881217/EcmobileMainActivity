package com.insthub.ecmobile.fragment;

import java.io.IOException;

import com.insthub.BeeFramework.fragment.BaseFragment;
import com.insthub.ecmobile.R;
import com.insthub.ecmobile.activity.BannerWebActivity;
import com.zbar.lib.camera.CameraManager;
import com.zbar.lib.decode.CaptureActivityHandler;
import com.zbar.lib.decode.InactivityTimer;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

//public class Zxing_Fragment extends BaseFragment {
//
//	@Override
//	public void OnMessageResponse(String url, JSONObject jo, AjaxStatus status) throws JSONException {
//		super.OnMessageResponse(url, jo, status);
//
//	}
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		
//		View coment_view = inflater.inflate(R.layout.zxing_fragment, null);
//		return super.onCreateView(inflater, container, savedInstanceState);
//	}
//
//}

public class Zxing_Fragment extends BaseFragment implements Callback {

	private CaptureActivityHandler handler;
	private boolean hasSurface;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.50f;
	private boolean vibrate;
	private int x = 0;
	private int y = 0;
	private int cropWidth = 0;
	private int cropHeight = 0;
	private RelativeLayout mContainer = null;
	private RelativeLayout mCropLayout = null;
	private boolean isNeedCapture = false;

	View coment_view;

	public boolean isNeedCapture() {
		return isNeedCapture;
	}

	public void setNeedCapture(boolean isNeedCapture) {
		this.isNeedCapture = isNeedCapture;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getCropWidth() {
		return cropWidth;
	}

	public void setCropWidth(int cropWidth) {
		this.cropWidth = cropWidth;
	}

	public int getCropHeight() {
		return cropHeight;
	}

	public void setCropHeight(int cropHeight) {
		this.cropHeight = cropHeight;
	}

	// @Override
	// public View onCreateView(LayoutInflater inflater, ViewGroup container,
	// Bundle savedInstanceState) {
	//
	// View coment_view = inflater.inflate(R.layout.zxing_fragment, null);
	// return super.onCreateView(inflater, container, savedInstanceState);
	// }

	/** Called when the activity is first created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		coment_view = inflater.inflate(R.layout.zxing_fragment, null);
		// 鍒濆鍖�CameraManager
		CameraManager.init(getActivity().getApplication());
		hasSurface = false;
		inactivityTimer = new InactivityTimer(getActivity());

		mContainer = (RelativeLayout) coment_view.findViewById(R.id.capture_containter);
		mCropLayout = (RelativeLayout) coment_view.findViewById(R.id.capture_crop_layout);

		ImageView mQrLineView = (ImageView) coment_view.findViewById(R.id.capture_scan_line);
		TranslateAnimation mAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f,
				TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0f,
				TranslateAnimation.RELATIVE_TO_PARENT, 0.9f);
		mAnimation.setDuration(1500);
		mAnimation.setRepeatCount(-1);
		mAnimation.setRepeatMode(Animation.REVERSE);
		mAnimation.setInterpolator(new LinearInterpolator());
		mQrLineView.setAnimation(mAnimation);

		return coment_view;
	}

	boolean flag = true;

	protected void light() {
		if (flag == true) {
			flag = false;
			// 寮�棯鍏夌伅
			CameraManager.get().openLight();
		} else {
			flag = true;
			// 鍏抽棯鍏夌伅
			CameraManager.get().offLight();
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) coment_view.findViewById(R.id.capture_preview);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		playBeep = true;
		AudioManager audioService = (AudioManager) getActivity().getSystemService(getActivity().AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	public void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	public void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	public void handleDecode(String result) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();

		if (result != null && (result.startsWith("http") || result.startsWith("https"))) {

			Intent intent = new Intent();
			intent.putExtra("url", result);
			intent.setClass(getActivity(), BannerWebActivity.class);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);

		} else {

			Toast.makeText(getActivity(), "二维码内容:" + result, Toast.LENGTH_SHORT).show();

		}

		// 杩炵画鎵弿锛屼笉鍙戦�姝ゆ秷鎭壂鎻忎竴娆＄粨鏉熷悗灏变笉鑳藉啀娆℃壂鎻� //
		handler.sendEmptyMessage(R.id.restart_preview);
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);

			Point point = CameraManager.get().getCameraResolution();
			int width = point.y;
			int height = point.x;

			int x = mCropLayout.getLeft() * width / mContainer.getWidth();
			int y = mCropLayout.getTop() * height / mContainer.getHeight();

			int cropWidth = mCropLayout.getWidth() * width / mContainer.getWidth();
			int cropHeight = mCropLayout.getHeight() * height / mContainer.getHeight();

			setX(x);
			setY(y);
			setCropWidth(cropWidth);
			setCropHeight(cropHeight);
			// 璁剧疆鏄惁闇�鎴浘
			setNeedCapture(true);

		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			// handler = new CaptureActivityHandler(CaptureActivity.this);
			handler = new CaptureActivityHandler(Zxing_Fragment.this);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public Handler getHandler() {
		return handler;
	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getActivity().getSystemService(getActivity().VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};
}
