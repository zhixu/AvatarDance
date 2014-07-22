package com.live2d.avatardance;

import android.content.Context;

public class SongItem {
	private Context context;
    private String title;
    private String artist;
    private String filepath;
    private String albumid;

    public SongItem(Context context, String title, String artist, String filepath) {
        this.context = context;
        this.title = title;
        this.artist = artist;
        this.filepath = filepath;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public String getFilepath() {
        return filepath;
    }
	
}
