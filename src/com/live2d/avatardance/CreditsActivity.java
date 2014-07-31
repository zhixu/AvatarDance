package com.live2d.avatardance;

import com.example.avatardance.R;
import com.example.avatardance.R.id;
import com.example.avatardance.R.layout;
import com.example.avatardance.R.menu;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class CreditsActivity extends Activity {
	
	Button buttonBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_credits);
		
		buttonBack = (Button) findViewById(R.id.button_back_home);
		ButtonBackListener buttonBackListener = new ButtonBackListener();
		buttonBack.setOnClickListener(buttonBackListener);
	}
	
	class ButtonBackListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent i = new Intent(CreditsActivity.this, DanceActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(i);
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.credits, menu);
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
