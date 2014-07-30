/**
 *  You can modify and use this source freely
 *  only for the development of application related Live2D.
 *
 *  (c) Live2D Inc. All rights reserved.
 */
package jp.live2d.utils.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.widget.TextView;

public class FileManager {
	static Context context ;
	
	private static boolean isAssets;

	public static void init( Context c ){
		context = c ;
	}

	public static void setAsset(boolean a) {
		isAssets = a;
	}

	public static boolean exists_resource( String path ){
		try {
			InputStream afd = context.getAssets().open(path) ;
			afd.close() ;
			return true ;
		} catch (IOException e) {
			return false ;
		}
	}


	public static InputStream open_resource( String path ) throws IOException{
		if (!isAssets) {
			return new FileInputStream(path);
		} else {
			return open_asset(path);
		}
	}
	
	public static InputStream open_background(Uri uri) {
		try {
			ContentResolver cr = context.getContentResolver();
			InputStream in = cr.openInputStream(uri);
			return in;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static InputStream open_background (String path, boolean isAsset) throws IOException {
		if (!isAsset) {
			return new FileInputStream(path);
		} else {
			return open_asset(path);
		}
	}
	
	public static InputStream open_asset (String path) throws IOException {
		return context.getAssets().open(path) ;
	}


	public static boolean exists_cache( String path ){
		File f = new File( context.getCacheDir() , path ) ;
		return f.exists() ;
	}


	public static InputStream open_cache( String path ) throws FileNotFoundException{
		File f = new File( context.getCacheDir() , path ) ;
		return new FileInputStream(f) ;
	}


	/*
	 *
	 * @param path
	 * @param isCache trueã�ªã‚‰ã‚­ãƒ£ãƒƒã‚·ãƒ¥ã‚’é–‹ã��ã€�falseã�ªã‚‰ãƒªã‚½ãƒ¼ã‚¹ã‚’é–‹ã��
	 * @return
	 * @throws IOException
	 */
	public static InputStream open( String path , boolean isCache ) throws IOException{
		if( isCache ){
			return open_cache(path) ;
		}
		else{
			return open_resource(path) ;
		}
	}


	public static InputStream open( String path  ) throws IOException{
		return open(path,false);
	}


	public static AssetFileDescriptor openFd( String path ) throws IOException
	{
		return context.getAssets().openFd(path);
	}
}
