package com.live2d.avatardance;

import android.content.Context;

public class SongItem {
    private String title;
    private String artist;
    private String filepath;

    public SongItem(String title, String artist) {
    	this.title = title;
    	this.artist = artist;
    }
    
    public SongItem(String title, String artist, String filepath) {
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
