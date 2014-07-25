package com.live2d.avatardance;

import com.example.avatardance.R;
import com.example.avatardance.R.id;
import com.example.avatardance.R.layout;
import com.example.avatardance.R.menu;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class AvatarActivity extends Activity {

	final private Integer BROWSE_FILE_CODE = 1;
	
	private String avatarJSON;
	
	private Button buttonAvatar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_avatar);
		
		setupUI();
		
	}
	
	private void setupUI() {
		buttonAvatar = (Button) findViewById(R.id.button_avatar);
		ButtonAvatarListener buttonAvatarListener = new ButtonAvatarListener();
		buttonAvatar.setOnClickListener(buttonAvatarListener);
	}
	
	class ButtonAvatarListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Uri path = Uri.fromFile(Environment.getExternalStorageDirectory());
			
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_GET_CONTENT);
			intent.setDataAndType(path, "file/*");
			startActivityForResult(intent, BROWSE_FILE_CODE);
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == BROWSE_FILE_CODE) {
			if (resultCode == RESULT_OK) {
				Uri uri = data.getData();
				avatarJSON = uri.getPath();
				savePreferences();
				
				Intent i = new Intent(this, PlaylistActivity.class);
				startActivity(i);
				
				//Toast.makeText(getApplicationContext(), "path: " + s, Toast.LENGTH_LONG).show();
			}
			
			if (resultCode == RESULT_CANCELED) {	
				Toast.makeText(getApplicationContext(), "Please choose valid model .json file", Toast.LENGTH_LONG).show();
			}
		}
	}
	
	private void savePreferences() {
		SharedPreferences prefs = this.getSharedPreferences("user", MODE_PRIVATE);
		prefs.edit().putString("avatarJSON", avatarJSON).commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.avatar, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
