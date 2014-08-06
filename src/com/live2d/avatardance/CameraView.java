package com.live2d.avatardance;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

public class CameraView extends ViewGroup implements SurfaceHolder.Callback {

	SurfaceView surfaceView;
	SurfaceHolder surfaceHolder;
	Camera camera;
	
	@SuppressWarnings("deprecation")
	public CameraView(Context context, Camera camera) {
		super(context);
		
		Log.d("CameraView", "cameraview constructed");
		
		this.camera = camera;
		
		surfaceView = new SurfaceView(context);
		addView(surfaceView);

		// Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d("CameraView", "surface created");
		
		
		// The Surface has been created, now tell the camera where to draw the preview.
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
		Log.d("CameraView", "camera surface changed");
		
		if (surfaceHolder.getSurface() == null){
		      // preview surface does not exist
		      return;
		}

	    // stop preview before making changes
	    try {
	        camera.stopPreview();
	    } catch (Exception e){
	      // ignore: tried to stop a non-existent preview
	    }

		// Now that the size is known, set up the camera parameters and begin
	    // the preview.
	    Camera.Parameters parameters = camera.getParameters();
	    parameters.setPreviewSize(width, height);
	    requestLayout();
	    camera.setParameters(parameters);

	    // Important: Call startPreview() to start updating the preview surface.
	    // Preview must be started before you can take a picture.
	    camera.startPreview();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
		
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		
	}

	
	
}
