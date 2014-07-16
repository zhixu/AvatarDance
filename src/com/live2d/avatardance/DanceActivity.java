package com.live2d.avatardance;

import com.live2d.avatardance.LAppLive2DManager;
import com.live2d.avatardance.LAppView;
import jp.live2d.utils.android.FileManager;

import com.example.avatardance.R;
import com.example.avatardance.R.id;
import com.example.avatardance.R.layout;
import com.example.avatardance.R.menu;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;


public class DanceActivity extends Activity {

	private final String TAG = "DANCE ACTIVITY";
	
	private LAppLive2DManager live2DMgr ;
	static private Activity instance;

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

	        Log.d(TAG, "path: " + LAppDefine.MODEL_HARU);
	        
	        requestWindowFeature(Window.FEATURE_NO_TITLE);

	      	setupGUI();
	      	FileManager.init(this.getApplicationContext());
	    }


	void setupGUI()
	{
    	setContentView(R.layout.activity_dance);

        LAppView view = live2DMgr.createView(this) ;

        // activity_main.xmlにLive2DのViewをレイアウトする
        FrameLayout layout=(FrameLayout) findViewById(R.id.live2DLayout);
		layout.addView(view, 0, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));


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
		//live2DMgr.onResume() ;
		super.onResume();
	}


	/*
	 * Activityを停止したときのイベント。
	 */
	@Override
	protected void onPause()
	{
		live2DMgr.onPause() ;
    	super.onPause();
	}

}
