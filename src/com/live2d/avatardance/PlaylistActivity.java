package com.live2d.avatardance;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.avatardance.R;
import com.example.avatardance.R.id;
import com.example.avatardance.R.layout;
import com.example.avatardance.R.menu;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class PlaylistActivity extends ListActivity {
	
	final Integer PLAYLIST_ACTIVITY = 1;
	final Integer BROWSE_PLAYLIST_CODE = 1;

	private HashMap<String, String> playlists;

	private ArrayList<String> playlistN;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_playlist);
		
		ContentResolver cr = this.getContentResolver();
		Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
		String[] projection = { MediaStore.Audio.Playlists._ID,
				MediaStore.Audio.Playlists.NAME };
		Cursor cursor = cr.query(uri, projection, null, null, null);
		cursor.moveToFirst();

		playlists = new HashMap<String, String>();
		playlistN = new ArrayList<String>();

		String id = "all";
		String name = "All Songs";
		playlists.put(name, id);
		playlistN.add(name);

		for (int i = 0; i < cursor.getCount(); i++) {
			id = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Playlists._ID));
			name = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Playlists.NAME));

			playlists.put(name, id);
			playlistN.add(name);
			cursor.moveToNext();
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.playlist_row_item, playlistN);

		setListAdapter(adapter);
	}
	
	protected void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);
		// PlaylistItem item = (PlaylistItem)
		// listView.getItemAtPosition(position);

		String name = (String) getListAdapter().getItem(position);
		String playlistID = playlists.get(name);

		Intent i = new Intent(this, SonglistActivity.class);
		i.putExtra("playlistID", playlistID);
		i.putExtra("name", name);
		i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

		startActivity(i);
	}
	
	public void onBackPressed() {
		DanceActivity.a.finish();
		
		Intent i = new Intent(this, DanceActivity.class);
		startActivity(i);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.playlist, menu);
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
