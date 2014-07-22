package com.live2d.avatardance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class SongBPMRetriever extends AsyncTask<String, Float, Float> {
	private final String API_KEY = "OVYVMAZX0QWOOT3CI";
	private final String TAG = "SONG BPM RETRIEVER";
	
	
	DanceActivity activity;
	
	public void getBPM(String song, String artist, DanceActivity a) {
		activity = a;
		this.execute(song, artist);
	}
	
	@Override
	protected Float doInBackground(String... params) {
		String song = params[0];
		String artists = params[1];
		try {
			return getBPMTask(song, artists);
		} catch (UnsupportedEncodingException e) {
			return -1f;
		}
	}
	
	protected void onPostExecute(Float f) {
		super.onPostExecute(f);
		if (activity != null) {
			activity.setBPM(f);
		}
	}
	
	private float getBPMTask(String song, String artist) throws UnsupportedEncodingException {

		String base = "http://developer.echonest.com/api/v4/song/";
		String url1 = base + "search?api_key=" + API_KEY + "&artist="
				+ URLEncoder.encode(artist, "UTF-8") + "&title="
				+ URLEncoder.encode(song, "UTF-8");
		try {
			JSONArray songsArray1 = getJSON(url1).getJSONObject("response")
					.getJSONArray("songs");
			if (songsArray1.length() > 0) {
				String songID = songsArray1.getJSONObject(0)
						.getString("id");
				String url2 = base + "profile?api_key=" + API_KEY + "&id="
						+ songID + "&bucket=audio_summary";
				JSONArray songsArray2 = getJSON(url2).getJSONObject(
						"response").getJSONArray("songs");
				if (songsArray2.length() > 0) {
					String tempo = songsArray2.getJSONObject(0)
							.getJSONObject("audio_summary")
							.getString("tempo");
					
					Log.d(TAG, "tempo: " + tempo);
					
					return Float.parseFloat(tempo);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	private JSONObject getJSON(String url) throws JSONException {
		final StringBuilder builder = new StringBuilder();
		final HttpClient client = new DefaultHttpClient();
		final HttpGet httpGet = new HttpGet(url);
		
		try {
			HttpResponse response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			InputStream content = entity.getContent();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(content));
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new JSONObject(builder.toString());
	}

}
