package com.live2d.avatardance;

import java.util.ArrayList;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class SonglistActivity extends ListActivity {
	
	String playlistID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_songlist);
		
		Intent intent = getIntent();
		playlistID = intent.getExtras().getString("playlistID");

		TextView title = (TextView) findViewById(R.id.songlist_title);
		title.setText(intent.getExtras().getString("name"));

		Cursor cursor;
		ContentResolver cr = this.getContentResolver();
		String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";

		ArrayList<SongItem> songData = new ArrayList<SongItem>();
		
		//shuffle option
		songData.add(new SongItem( this, "Shuffle", "", null));

		if (playlistID.equals("all")) {

			// Query the MediaStore for all music files
			String[] projection = { MediaStore.Audio.Media.TITLE,
					MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DATA };
			String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
			Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
			cr = this.getContentResolver();
			cursor = cr.query(uri, projection, selection, null, sortOrder);
			cursor.moveToFirst();

			for (int i = 0; i < cursor.getCount(); i++) {
				songData.add(new SongItem(
						this,
						cursor.getString(cursor
								.getColumnIndex(MediaStore.Audio.Media.TITLE)),
						cursor.getString(cursor
								.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
						cursor.getString(cursor
								.getColumnIndex(MediaStore.Audio.Media.DATA))));
				cursor.moveToNext();
			}
			cursor.close();

		} else {

			String[] projection = { MediaStore.Audio.Playlists.Members.TITLE,
					MediaStore.Audio.Playlists.Members.ARTIST,
					MediaStore.Audio.Playlists.Members.DATA,
					MediaStore.Audio.Playlists.Members.ALBUM_ID };
			Long id = Long.parseLong(playlistID);
			Uri uri = MediaStore.Audio.Playlists.Members.getContentUri(
					"external", id);
			cursor = cr.query(uri, projection, selection, null, null);
			cursor.moveToFirst();

			for (int i = 0; i < cursor.getCount(); i++) {
				songData.add(new SongItem(
						this,
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

		SongAdapter adapter = new SongAdapter(this, R.layout.songlist_row_item,
				songData.toArray(new SongItem[0]));

		ListView view = getListView(); // (ListView)
										// findViewById(R.id.songlist);
		view.setAdapter(adapter);
		
	}
	
	protected void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

		Intent i = new Intent();
		i.putExtra("playlistID", playlistID);
		
		if (position == 0) {
			i.putExtra("songPosition", "shuffle");
		} else {
			i.putExtra("songPosition", Integer.toString(position-1));
		}
		setResult(Activity.RESULT_OK, i);
		
		finish();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.songlist, menu);
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
