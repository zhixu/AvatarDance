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
import android.os.Environment;
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
	
	private String modelJSON = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/miku.model.json";
	
	BroadcastReceiver mReceiver;

	public DanceActivity( )
	{
		instance=this;
		live2DMgr = new LAppLive2DManager(this) ;
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
		//view.bringToFront();
		findViewById(R.id.controls).bringToFront();
		
		
		// モデル切り替えボタン
		//ImageButton iBtn = (ImageButton)findViewById(R.id.imageButton1);
		//ClickListener listener = new ClickListener();
		//iBtn.setOnClickListener(listener);
		
		ImageButton buttonPlay = (ImageButton) findViewById(R.id.button_play);
		ButtonPlayListener buttonPlayListener = new ButtonPlayListener();
		buttonPlay.setOnClickListener(buttonPlayListener);
		
		ImageButton buttonBack = (ImageButton) findViewById(R.id.button_back);
		ButtonBackListener buttonBackListener = new ButtonBackListener();
		buttonBack.setOnClickListener(buttonBackListener);
		
		ImageButton buttonFwd = (ImageButton) findViewById(R.id.button_forward);
		ButtonFwdListener buttonFwdListener = new ButtonFwdListener();
		buttonFwd.setOnClickListener(buttonFwdListener);
		
	}
	
	private void getBPM(String title, String artist) {
		currentSongBPM = -1;
		new SongBPMRetriever().getBPM(title, artist, this);
	}
	
	public void setBPM (float _bpm) {
		currentSongBPM = _bpm;
		Toast.makeText(getApplicationContext(), "bpm: " + _bpm, Toast.LENGTH_SHORT).show();
		live2DMgr.setBPM(_bpm);
	}
	
	public String getModelJSON() {
		return modelJSON;
	}


	// ボタンを押した時のイベント
	class ClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			Toast.makeText(getApplicationContext(), "change model", Toast.LENGTH_SHORT).show();
			live2DMgr.changeModel();//Live2D Event
		}
	}
	
	class ButtonPlayListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			
			
		}
	}
	
	class ButtonBackListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
	}
	
	class ButtonFwdListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
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

	protected void onDestroy() {
		super.onDestroy();
	}


}
