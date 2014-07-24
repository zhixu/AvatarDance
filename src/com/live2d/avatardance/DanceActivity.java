package com.live2d.avatardance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import com.live2d.avatardance.LAppLive2DManager;
import com.live2d.avatardance.LAppView;

import jp.live2d.motion.Live2DMotion;
import jp.live2d.utils.android.FileManager;

import com.example.avatardance.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
	
	private ImageButton buttonPlay;
	private ImageButton buttonBack;
	private ImageButton buttonFwd;
	
	private String playlistID;
	
	ArrayList<SongItem> songData;
	Stack<Integer> songHistory;
	
	private MediaPlayer mp;
	private Cursor cursor = null;
	private boolean isPlaying = true;
	private boolean isShuffle = false;
	private int currentSongIndex;
	private float currentSongBPM = -1;
	
	private LAppLive2DManager live2DMgr ;
	static private Activity instance;
	
	//private Camera camera;
	//private CameraActivity cameraActivity;
	//private SurfaceTexture surface;
	
	private Live2DMotion motion;

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
        
        setupGUI();
        
        mp = new MediaPlayer();
        songHistory = new Stack<Integer>();
        
        Intent intent = getIntent();
        String playlistID = intent.getExtras().getString("playlistID");
        String songPosition = intent.getExtras().getString("songPosition");
        
        setPlaylist(playlistID);
        setSongIndex(songPosition);
        setNewSong(currentSongIndex);
        
      	
      	FileManager.init(this.getApplicationContext());
    }
	
	void setupGUI()
	{
		setContentView(R.layout.activity_dance);
		
		//setting up camera
		//initializeCamera();
		
        LAppView view = live2DMgr.createView(this) ;

        FrameLayout layout=(FrameLayout) findViewById(R.id.live2DLayout);
		layout.addView(view, 0, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		findViewById(R.id.controls).bringToFront();
		
		buttonPlay = (ImageButton) findViewById(R.id.button_play);
		ButtonPlayListener buttonPlayListener = new ButtonPlayListener();
		buttonPlay.setOnClickListener(buttonPlayListener);
		
		buttonBack = (ImageButton) findViewById(R.id.button_back);
		ButtonBackListener buttonBackListener = new ButtonBackListener();
		buttonBack.setOnClickListener(buttonBackListener);
		
		buttonFwd = (ImageButton) findViewById(R.id.button_forward);
		ButtonFwdListener buttonFwdListener = new ButtonFwdListener();
		buttonFwd.setOnClickListener(buttonFwdListener);
	}
	
	public void setPlaylist(String playlistID) {

		songData = new ArrayList<SongItem>();
		String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";
		ContentResolver cr = this.getContentResolver();
		String[] projection = { MediaStore.Audio.Media.TITLE,
				MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DATA };
		
		if (playlistID == null || playlistID.equals("all")) {
			// Query the MediaStore for all music files
			String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
			Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
			cursor = cr.query(uri, projection, selection, null, sortOrder);
		} else {
			Long id = Long.parseLong(playlistID);
			Uri uri = MediaStore.Audio.Playlists.Members.getContentUri(
					"external", id);
			cursor = cr.query(uri, projection, selection, null, null);
		}
		
		cursor.moveToFirst();
		for (int i = 0; i < cursor.getCount(); i++) {
			songData.add(new SongItem(
					cursor.getString(cursor
							.getColumnIndex(MediaStore.Audio.Playlists.Members.TITLE)),
					cursor.getString(cursor
							.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST)),
					cursor.getString(cursor
							.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA))));
			cursor.moveToNext();
		}
		cursor.close();
	}
	
	private void setSongIndex(String songPosition) {
		if (songPosition == null || songPosition.equals("shuffle")) {
			isShuffle = true;
			currentSongIndex = pickRandomSong();
		} else {
			isShuffle = false;
			currentSongIndex = Integer.parseInt(songPosition);
		}
	}
	
	private int pickRandomSong() {
		return new Random().nextInt(songData.size());
	}
	
	private void setNewSong(int i) {
		
		getBPM();
		try {
			mp.reset();
			mp.setDataSource(songData.get(i).getFilepath());
			mp.prepare();
			if (isPlaying) {
				mp.start();
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void getBPM() {
		SongItem i = songData.get(currentSongIndex);
		String title = i.getTitle();
		String artist = i.getArtist();
		
		new SongBPMRetriever().getBPM(title, artist, this);
	}
	
	public void setBPM (float _bpm) {
		currentSongBPM = _bpm;
		Toast.makeText(getApplicationContext(), "bpm: " + _bpm, Toast.LENGTH_SHORT).show();
		live2DMgr.danceSetBPM(_bpm);
	}

	/*
	// ボタンを押した時のイベント
	class ClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			Toast.makeText(getApplicationContext(), "change model", Toast.LENGTH_SHORT).show();
			live2DMgr.changeModel();//Live2D Event
		}
	}*/
	
	class ButtonPlayListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			if (mp != null) {
				if (isPlaying) {
					buttonPlay.setBackground(getResources().getDrawable(R.drawable.play));
					live2DMgr.danceStop();
					live2DMgr.danceResetBPM(currentSongBPM);
					mp.pause();
				} else {
					buttonPlay.setBackground(getResources().getDrawable(R.drawable.pause));
					live2DMgr.danceStart();
					live2DMgr.danceSetBPM(currentSongBPM);
					mp.start();
				}
				isPlaying = !isPlaying;
			}
		}
	}
	
	class ButtonBackListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			if (isShuffle) {
				
				if (!songHistory.isEmpty()) {
					int i = songHistory.pop();
					setNewSong(i);
				}
				
			} else {
				
				if (currentSongIndex > 0) {
					currentSongIndex--;
				} else {
					currentSongIndex = songData.size() - 1;
				}
				setNewSong(currentSongIndex);
			}
		}
	}
	
	class ButtonFwdListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			if (isShuffle) {
				int i = pickRandomSong();
				songHistory.push(i);
				setNewSong(i);
			} else {
				if (currentSongIndex < songData.size() - 1) {
					currentSongIndex++;
				} else {
					currentSongIndex = 0;
				}
				setNewSong(currentSongIndex);	
			}
		}
	}
	
	public long getSongPosition() {
		return mp.getCurrentPosition();
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
		super.onStop();
	}

	protected void onDestroy() {
		super.onDestroy();
		mp.release();
	}
}
