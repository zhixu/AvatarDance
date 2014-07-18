package com.live2d.avatardance;

import java.io.IOException;
import java.io.InputStream;

import com.live2d.avatardance.LAppLive2DManager;
import com.live2d.avatardance.LAppView;

import jp.live2d.motion.Live2DMotion;
import jp.live2d.utils.android.FileManager;

import com.example.avatardance.R;
import com.example.avatardance.R.id;
import com.example.avatardance.R.layout;
import com.example.avatardance.R.menu;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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


public class DanceActivity extends Activity  {

	static private final String TAG = "DANCE ACTIVITY";
	
	private LAppLive2DManager live2DMgr ;
	static private Activity instance;
	
	//private Camera camera;
	//private CameraActivity cameraActivity;
	//private SurfaceTexture surface;
	
	private Live2DMotion motion;
	
	private float currentSongBPM = -1;
	
	BroadcastReceiver mReceiver;

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
	        
	        mReceiver = new BroadcastReceiver() {

				@Override
				public void onReceive(Context context, Intent intent) {
					String artist = intent.getStringExtra("artist");
					String title = intent.getStringExtra("track");
					Log.d(TAG, "artist: " + artist + " title: " + title);
					getBPM(title, artist);
				}
	        };

	        IntentFilter iF = new IntentFilter();
	        
	        // stock music player
	        iF.addAction("com.android.music.metachanged");
	 
	        // MIUI music player
	        iF.addAction("com.miui.player.metachanged");
	 
	        // HTC music player
	        iF.addAction("com.htc.music.metachanged");
	 
	        // WinAmp
	        iF.addAction("com.nullsoft.winamp.metachanged");
	 
	        // MyTouch4G
	        iF.addAction("com.real.IMP.metachanged");
	 
	        registerReceiver(mReceiver, iF);
	        
	      	setupGUI();
	      	FileManager.init(this.getApplicationContext());
	      	
	    }

	
	void setupGUI()
	{
		setContentView(R.layout.activity_dance);
		
		//setting up camera
		//initializeCamera();
		
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
	
	private void getBPM(String title, String artist) {
		currentSongBPM = -1;
		new SongBPMRetriever().getBPM(title, artist, this);
	}
	
	public void setBPM (float _bpm) {
		currentSongBPM = _bpm;
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
		//initializeCamera();
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
		
		//closeCamera();
		//unregisterReceiver(mReceiver);
		live2DMgr.onPause() ;
    	super.onPause();
	}

	protected void onStop() {
		unregisterReceiver(mReceiver);
		super.onStop();
	}




}
