package com.jzj.simplelamp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class LightService extends Service {

	private CameraLight light = null;
	private Toast toast = null;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int d = super.onStartCommand(intent, flags, startId);
		if (switchLight()) {
			this.setForeground();
		} else {
			this.stopSelf();
		}
		return d;
	}

	@Override
	public void onDestroy() {
		if (light != null) {
			light.stopLight();
			light = null;
		}
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * @return 是否启动了闪光灯(保留Service启动状态)
	 */
	private final boolean switchLight() {
		if (light == null) {
			light = new CameraLight();
			switch (light.init()) {
			case CameraLight.NO_CAMERA:
				toast(R.string.no_camera);
				return false;
			case CameraLight.NO_FLASH_LIGHT:
				toast(R.string.no_flash_light);
				return false;
			case CameraLight.TORCH_MODE_UNSUPPORT:
				toast(R.string.torch_mode_unsupport);
				return false;
			case CameraLight.CAMERA_OCCUPIRED:
				toast(R.string.camera_occupied);
				return false;
			case CameraLight.SUCCESS:
				if (!light.startLight()) {
					toast(R.string.error);
					return false;
				} else {
					toast(R.string.light_on);
					return true;
				}
			default:
			case CameraLight.UNEXPECTED_ERROR:
				toast(R.string.error);
				return false;
			}
		} else {
			toast(R.string.light_off);
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	private final void setForeground() {
		Notification nt = new Notification();
		nt.icon = R.drawable.ic_launcher;
		nt.when = System.currentTimeMillis();
		nt.flags |= Notification.FLAG_FOREGROUND_SERVICE
				| Notification.FLAG_NO_CLEAR;
		nt.defaults |= 0;
		PendingIntent pi = PendingIntent.getService(getBaseContext(), 0,
				new Intent(this, LightService.class), 0);
		nt.setLatestEventInfo(getBaseContext(), getText(R.string.app_name),
				getText(R.string.noti_text), pi);
		this.startForeground(1, nt);
	}

	private final void toast(int resId) {
		if (toast == null) {
			toast = Toast.makeText(this, resId, Toast.LENGTH_SHORT);
			toast.getView().setBackgroundResource(R.drawable.toast_background);
		} else {
			toast.setText(getText(resId));
		}
		toast.show();
	}
}
