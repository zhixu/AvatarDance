package com.live2d.avatardance;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import com.live2d.avatardance.LAppLive2DManager;
import com.live2d.avatardance.LAppView;

import jp.live2d.utils.android.FileManager;

import com.example.avatardance.R;
import com.soundcloud.android.crop.Crop;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DanceActivity extends Activity  {

	static private final String TAG = "DANCE ACTIVITY";
	
	public static DanceActivity a;
	
	final private Integer BROWSE_FILE_CODE = 1;
	final private Integer BROWSE_SONG_CODE = 2;
	final private Integer BROWSE_BG_CODE = 3;
	
	private Button buttonLoad;
	private Button buttonStart;
	private Button buttonBG;
	private Button buttonAbout;
	private ImageButton buttonAvatarBack;
	private ImageButton buttonAvatarFwd;
	private TextView textError;
	
	private ImageButton buttonPlay;
	private ImageButton buttonBack;
	private ImageButton buttonFwd;
	private ImageButton buttonMenu;
	private ImageButton buttonUp;
	private ImageButton buttonDown;
	
	private TextView valueBPM;
	
	ArrayList<SongItem> songData;
	Stack<Integer> songHistory;
	
	private MediaPlayer mp;
	private MusicOnCompletionListener musicCompletionListener;
	private Cursor cursor = null;
	private boolean isPlaying = true;
	private boolean isShuffle = false;
	private int currentSongIndex;
	private float currentSongBPM = -1;
	
	private Camera camera;
	
	private LAppLive2DManager live2DMgr ;
	private LAppView view;
	static private Activity instance;

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
     
        a = this;
        
        setupGUIAvatar();

      	DisplayMetrics metrics = this.getApplicationContext().getResources().getDisplayMetrics();
      	FileManager.init(this.getApplicationContext(), metrics.widthPixels, metrics.heightPixels);
      	
      	view.getHolder().setFormat( PixelFormat.TRANSLUCENT );

    }
	
	public void onNewIntent(Intent intent) {
		
		if (intent.hasExtra("playlistID") && intent.hasExtra("songPosition")) {
			
			String playlistID = intent.getExtras().getString("playlistID");
	        String songPosition = intent.getExtras().getString("songPosition");
	        
	        setPlaylist(playlistID);
	        setSongIndex(songPosition);
	        setNewSong(currentSongIndex);
	        
	        if (isPlaying) {
	        	live2DMgr.danceStart();
	        }
		}
	}
	
	public void onBackPressed() {

		Intent i = new Intent(this, SonglistActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(i);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == BROWSE_FILE_CODE) {
			if (resultCode == RESULT_OK) {
				textError.setVisibility(TextView.INVISIBLE);
				Uri uri = data.getData();
				String avatarJSON = uri.getPath();
				
				String s = avatarJSON.substring(0,avatarJSON.lastIndexOf("/") + 1);
				String fileExt = avatarJSON.substring(avatarJSON.lastIndexOf("."), avatarJSON.length());
				
				if (fileExt.equals(".json")) {
					textError.setVisibility(TextView.INVISIBLE);
					live2DMgr.switchInputModel(avatarJSON);
				} else {
					textError.setVisibility(TextView.VISIBLE);
				}
			}
			if (resultCode == RESULT_CANCELED) {	
				textError.setVisibility(TextView.VISIBLE);
			}
		}
		
		if (requestCode == BROWSE_BG_CODE) {
			if (resultCode == RESULT_OK) {
				textError.setVisibility(TextView.INVISIBLE);
				Uri uri = data.getData();
				Uri output = Uri.fromFile(new File(getCacheDir(), "cropped"));
				new Crop(uri).output(output).asSquare().start(this);
			}
			if (resultCode == RESULT_CANCELED) {	
				textError.setVisibility(TextView.VISIBLE);
			}
		}
		
		if (requestCode == BROWSE_SONG_CODE) {
			if (resultCode == RESULT_OK) {
				textError.setVisibility(TextView.INVISIBLE);
				String playlistID = data.getExtras().getString("playlistID");
		        String songPosition = data.getExtras().getString("songPosition");
		        
		        setPlaylist(playlistID);
		        setSongIndex(songPosition);
		        setNewSong(currentSongIndex);
			}
			if (resultCode == RESULT_CANCELED) {
				textError.setText("Error has occurred with song selection.");
				textError.setVisibility(TextView.VISIBLE);
			}
		}
		
		if (requestCode == Crop.REQUEST_CROP) {
			if (resultCode == RESULT_OK) {
				textError.setVisibility(TextView.INVISIBLE);
				view.setBackground(Uri.fromFile(new File(getCacheDir(), "cropped")));
			} else {
				textError.setText("Error has occurred with image selection.");
				textError.setVisibility(TextView.VISIBLE);
			}
		}
		
	}
	
	void setupGUIAvatar() {
		setContentView(R.layout.activity_avatar);
		
		view = live2DMgr.createView(this) ;
		
		FrameLayout layout=(FrameLayout) findViewById(R.id.live2DLayout);
		layout.addView(view, 0, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		findViewById(R.id.layout_avatar).bringToFront();
		
		buttonAbout = (Button) findViewById(R.id.button_about);
		ButtonAboutListener buttonAboutListener = new ButtonAboutListener();
		buttonAbout.setOnClickListener(buttonAboutListener);
		
		buttonLoad = (Button) findViewById(R.id.button_load);
		ButtonLoadListener buttonLoadListener = new ButtonLoadListener();
		buttonLoad.setOnClickListener(buttonLoadListener);
		
		buttonStart = (Button) findViewById(R.id.button_start);
		ButtonStartListener buttonStartListener = new ButtonStartListener();
		buttonStart.setOnClickListener(buttonStartListener);
		
		buttonBG = (Button) findViewById(R.id.button_background);
		ButtonBGListener buttonBGListener = new ButtonBGListener();
		buttonBG.setOnClickListener(buttonBGListener);
		
		buttonAvatarBack = (ImageButton) findViewById(R.id.avatar_back);
		ButtonABackListener buttonABackListener = new ButtonABackListener();
		buttonAvatarBack.setOnClickListener(buttonABackListener);
		
		buttonAvatarFwd = (ImageButton) findViewById(R.id.avatar_fwd);
		ButtonAFwdListener buttonAFwdListener = new ButtonAFwdListener();
		buttonAvatarFwd.setOnClickListener(buttonAFwdListener);

		textError = (TextView) findViewById(R.id.text_error);
		textError.setVisibility(TextView.INVISIBLE);
	}
	
	void setupGUIDance()
	{
		setContentView(R.layout.activity_dance);
		
		mp = new MediaPlayer();
		musicCompletionListener = new MusicOnCompletionListener();
		mp.setOnCompletionListener(musicCompletionListener);
        songHistory = new Stack<Integer>();
        
		//setting up camera
		//initializeCamera();

        FrameLayout layout=(FrameLayout) findViewById(R.id.live2DLayout2);
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
		
		buttonMenu = (ImageButton) findViewById(R.id.button_menu);
		ButtonMenuListener buttonMenuListener = new ButtonMenuListener();
		buttonMenu.setOnClickListener(buttonMenuListener);
		
		buttonUp = (ImageButton) findViewById(R.id.button_up);
		ButtonUpListener buttonUpListener = new ButtonUpListener();
		buttonUp.setOnClickListener(buttonUpListener);
		buttonUp.setOnTouchListener(buttonUpListener);
		
		buttonDown = (ImageButton) findViewById(R.id.button_down);
		ButtonDownListener buttonDownListener = new ButtonDownListener();
		buttonDown.setOnClickListener(buttonDownListener);
		buttonDown.setOnTouchListener(buttonDownListener);
		
		valueBPM = (TextView) findViewById(R.id.value_bpm);
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
		
		if (songData.size() <= 0) {
			isPlaying = false;
		} else {
			live2DMgr.danceStart();
		}
	}
	
	private void setSongIndex(String songPosition) {
		if (songData.size() > 0) {
			if (songPosition == null || songPosition.equals("shuffle")) {
				isShuffle = true;
				currentSongIndex = pickRandomSong();
			} else {
				isShuffle = false;
				currentSongIndex = Integer.parseInt(songPosition);
			}
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
		if (songData.size() > 0) {
			SongItem i = songData.get(currentSongIndex);
			String title = i.getTitle();
			String artist = i.getArtist();
			
			Toast.makeText(getApplicationContext(), artist + " - " + title, Toast.LENGTH_SHORT).show();
			
			new SongBPMRetriever().getBPM(title, artist, this);
		}
	}
	
	public void setBPM (float _bpm) {
		if (_bpm == -1) {
			currentSongBPM = 60;
		} else {
			currentSongBPM = _bpm;
		}
		
		int bpm = (int) currentSongBPM;
		valueBPM.setText(Integer.toString(bpm));
		
		live2DMgr.danceSetBPM(currentSongBPM);
	}
	
	class MusicOnCompletionListener implements MediaPlayer.OnCompletionListener {

		@Override
		public void onCompletion(MediaPlayer mp) {
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
	
	class ButtonBGListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Uri path = Uri.fromFile(Environment.getExternalStorageDirectory());
			
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_GET_CONTENT);
			intent.setDataAndType(path, "image/*");
			startActivityForResult(intent, BROWSE_BG_CODE);
		}
	}
	
	class ButtonAboutListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent i = new Intent(DanceActivity.this, CreditsActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(i);
		}
	}
	
	class ButtonABackListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			live2DMgr.switchDefaultModel(true);
		}
		
	}
	
	class ButtonAFwdListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			live2DMgr.switchDefaultModel(false);
			
		}
		
	}
	
	// retrieves URI for avatar .json file
	class ButtonLoadListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Uri path = Uri.fromFile(Environment.getExternalStorageDirectory());
			
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_GET_CONTENT);
			intent.setDataAndType(path, "application/json");
			startActivityForResult(intent, BROWSE_FILE_CODE);
		}
	}
	
	class ButtonStartListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent i = new Intent(DanceActivity.this, PlaylistActivity.class);
			ViewGroup parent = (ViewGroup) view.getParent();
			live2DMgr.danceSetBPM(-1);
			startActivityForResult(i,BROWSE_SONG_CODE);
			parent.removeView(view);
			setupGUIDance();
		}
		
	}
	
	class ButtonPlayListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			
			if (songData.size() > 0) {
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
	
	class ButtonMenuListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			
			RelativeLayout view = (RelativeLayout) findViewById(R.id.controls);
			
			if (view.getVisibility() == View.VISIBLE){
				buttonMenu.setBackgroundResource(R.drawable.up);
				view.setVisibility(View.INVISIBLE);
			} else if (view.getVisibility() == View.INVISIBLE){
				buttonMenu.setBackgroundResource(R.drawable.down);
				view.setVisibility(View.VISIBLE);
			}
		}
	}
	
	class ButtonUpListener implements OnClickListener, OnTouchListener {

		@Override
		public void onClick(View v) {
			if (currentSongBPM < 400) {
				currentSongBPM++;
				live2DMgr.danceSetBPM(currentSongBPM);
				int bpm = (int) currentSongBPM;
				valueBPM.setText(Integer.toString(bpm));
			}
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (currentSongBPM < 400) {
				currentSongBPM++;
				live2DMgr.danceSetBPM(currentSongBPM);
				int bpm = (int) currentSongBPM;
				valueBPM.setText(Integer.toString(bpm));
			}
			return true;
		}
	}
	
	class ButtonDownListener implements OnClickListener, OnTouchListener {

		@Override
		public void onClick(View v) {
			
			if (currentSongBPM > 0) {
				currentSongBPM--;
				live2DMgr.danceSetBPM(currentSongBPM);
				int bpm = (int) currentSongBPM;
				valueBPM.setText(Integer.toString(bpm));
			}
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			if (currentSongBPM > 0) {
				currentSongBPM--;
				live2DMgr.danceSetBPM(currentSongBPM);
				int bpm = (int) currentSongBPM;
				valueBPM.setText(Integer.toString(bpm));
			}
			return true;
			
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
		super.onStop();
	}

	protected void onDestroy() {
		super.onDestroy();
		if (mp != null) {
			mp.release();
		}
	}
}
