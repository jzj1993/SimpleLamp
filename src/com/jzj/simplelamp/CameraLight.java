package com.jzj.simplelamp;

import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;

public class CameraLight {

	public static final int UNEXPECTED_ERROR = -1;
	public static final int SUCCESS = 0;
	public static final int NO_CAMERA = 1;
	public static final int NO_FLASH_LIGHT = 2;
	public static final int TORCH_MODE_UNSUPPORT = 3;
	public static final int CAMERA_OCCUPIRED = 4;

	private Camera camera = null;

	public final int init() {
		try {
			// 获取摄像头数目
			int n = Camera.getNumberOfCameras();
			if (n <= 0) {
				return NO_CAMERA;
			}
			camera = null;
			// 优先尝试打开后置摄像头
			CameraInfo cameraInfo = new CameraInfo();
			for (int i = 0; i < n; ++i) {
				Camera.getCameraInfo(i, cameraInfo);
				if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
					try {
						camera = Camera.open(i);
					} catch (Exception e) {
					}
					if (camera != null)
						break;
				}
			}
			// 没有打开后置摄像头,则尝试其他摄像头
			if (camera == null) {
				for (int i = 0; i < n; ++i) {
					try {
						camera = Camera.open(i);
					} catch (Exception e) {
					}
					if (camera != null)
						break;
				}
			}
			// 摄像头都没打开,则摄像头都被占用
			if (camera == null) {
				return CAMERA_OCCUPIRED;
			}
			// 判断是否有闪光灯特性
			// if (!hasFlashLight(context)) {
			// return NO_FLASH_LIGHT;
			// }
			// 不支持手电筒模式,则释放camera
			if (!isSupportTorchMode(camera)) {
				camera.release();
				camera = null;
				return TORCH_MODE_UNSUPPORT;
			}
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return UNEXPECTED_ERROR;
		}
	}

	/**
	 * 启动手电筒
	 * 
	 * @return 发生错误返回false
	 */
	public final boolean startLight() {
		try {
			Parameters params = camera.getParameters();
			params.setFlashMode(Parameters.FLASH_MODE_TORCH);
			camera.setParameters(params);
			camera.startPreview();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 停止手电筒
	 */
	public final void stopLight() {
		if (camera != null) {
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}

	/**
	 * 判断是否有闪光灯特性
	 * 
	 * @param context
	 * @return
	 */
	static final boolean hasFlashLight(Context context) {
		return context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA_FLASH);
	}

	/**
	 * 判断是否支持手电筒模式
	 * 
	 * @param camera
	 * @return
	 */
	private static final boolean isSupportTorchMode(Camera camera) {
		Parameters params = camera.getParameters();
		if (params == null)
			return false;
		List<String> ls = params.getSupportedFlashModes();
		if (ls == null)
			return false;
		return ls.contains(Parameters.FLASH_MODE_TORCH);
	}
}
