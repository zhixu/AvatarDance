package com.live2d.avatardance;

import java.io.IOException;

import com.live2d.avatardance.LAppLive2DManager;
import com.live2d.avatardance.LAppView;
import jp.live2d.utils.android.FileManager;

import com.example.avatardance.R;
import com.example.avatardance.R.id;
import com.example.avatardance.R.layout;
import com.example.avatardance.R.menu;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;


public class DanceActivity extends Activity implements SurfaceTexture.OnFrameAvailableListener {

	static private final String TAG = "DANCE ACTIVITY";
	
	private LAppLive2DManager live2DMgr ;
	static private Activity instance;
	
	private Camera camera;
	private CameraActivity cameraActivity;
	private SurfaceTexture surface;

	public DanceActivity( )
	{
		instance=this;
		live2DMgr = new LAppLive2DManager() ;
		}


	 static public void exit()
    {
    	instance.finish();
    }
	 
	 @Override
	    public void onCreate(Bundle savedInstanceState)
		{
	        super.onCreate(savedInstanceState);
	        
	        requestWindowFeature(Window.FEATURE_NO_TITLE);

	      	setupGUI();
	      	FileManager.init(this.getApplicationContext());
	    }
	 
	 public void startCamera(int texture) {
		 
		 surface = new SurfaceTexture(texture);
		 surface.setOnFrameAvailableListener(this);
		 live2DMgr.getView().setRendererSurface(surface);
		 
		 camera = Camera.open();
		 
		 try {
			 camera.setPreviewTexture(surface);
			 camera.startPreview();
		 } catch (IOException e) {
			 Log.e(TAG, "Could not set SurfaceTexture to camera", e);
		 }
	 }
	 
	@Override
	public void onFrameAvailable(SurfaceTexture surfaceTexture) {
		
		live2DMgr.getView().requestRender();
		
	}
	 
	public void initializeCamera() {
		
		try {
			// 1 is front of camera 0 is back
			camera = Camera.open(0);
		} catch (Exception e) {
			Log.d(TAG, "Cannot create camera object");
		}
		
		SurfaceView preview = (SurfaceView) findViewById(R.id.camera);
		cameraActivity = new CameraActivity(this, preview, camera);
		preview.getHolder().addCallback(cameraActivity);
	
	}
	
	public void closeCamera() {
		if (camera != null) {
			camera.stopPreview();
			camera.setPreviewCallback(null);
			camera.lock();
			camera.release();
			camera = null;
		}
	}

	void setupGUI()
	{
		setContentView(R.layout.activity_dance);
		
		//setting up camera
		//startCamera();
		
        LAppView view = live2DMgr.createView(this) ;

        // activity_main.xmlにLive2DのViewをレイアウトする
        FrameLayout layout=(FrameLayout) findViewById(R.id.live2DLayout);
		layout.addView(view, 0, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		view.bringToFront();
		findViewById(R.id.imageButton1).bringToFront();
		
		// モデル切り替えボタン
		ImageButton iBtn = (ImageButton)findViewById(R.id.imageButton1);
		ClickListener listener = new ClickListener();
		iBtn.setOnClickListener(listener);
	}


	// ボタンを押した時のイベント
	class ClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			Toast.makeText(getApplicationContext(), "change model", Toast.LENGTH_SHORT).show();
			live2DMgr.changeModel();//Live2D Event
		}
	}


	/*
	 * Activityを再開したときのイベント。
	 */
	@Override
	protected void onResume()
	{
		initializeCamera();
		//cameraActivity.setCamera(camera);
		//live2DMgr.onResume() ;
		super.onResume();
		
	}


	/*
	 * Activityを停止したときのイベント。
	 */
	@Override
	protected void onPause()
	{
		
		closeCamera();
		live2DMgr.onPause() ;
    	super.onPause();
	}






}
