package com.live2d.avatardance;

import java.io.IOException;

import android.app.Activity;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraActivity extends Activity implements SurfaceHolder.Callback {

	static private final String TAG = "CameraActivity";
	
	private DanceActivity dance;
	private Camera camera;
	private SurfaceView view;
	
	public CameraActivity(DanceActivity d, SurfaceView v, Camera c) {
		camera = c;
		view = v;
		dance = d;
	}
	
	public void setCamera(Camera c) {
		camera = c;
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		if (camera != null) {
			try {
				camera.setPreviewDisplay(holder);
			} catch (IOException e) {
				Log.e(TAG, "IOException", e);
			}
			
			camera.startPreview();
		} else {
			Log.d(TAG, "Camera is null and should not be");
		}
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		camera.stopPreview();
		camera.release();
		
	}

}
