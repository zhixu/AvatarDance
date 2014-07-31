/**
 *  You can modify and use this source freely
 *  only for the development of application related Live2D.
 *
 *  (c) Live2D Inc. All rights reserved.
 */
package jp.live2d.utils.android;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.TextView;

public class FileManager {
	static Context context ;
	static int width, height;
	private static boolean isAssets;

	public static void init( Context c, int _width, int _height ){
		context = c ;
		width = _width;
		height = _height;
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
			Log.d("DANCE ACTIVITY", "opening background");
			BitmapFactory.Options o = new BitmapFactory.Options();
			
			o.inJustDecodeBounds = true;
	        BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, o);
	        Log.d("DANCE ACTIVITY", "stream decoded");
	        int width_tmp = o.outWidth
	                , height_tmp = o.outHeight;
	        int scale = 1;
	        
	        Log.d("DANCE ACTIVITY", "ratio width: " + width_tmp + " ratio height: " + height_tmp
	        		+ "\n width: " + width + " height: " + height);

	        if (width_tmp > width && height_tmp > height) {
		        while(true) {
		            if((width_tmp / 2 < height) && (height_tmp / 2 < height))
		                break;
		            width_tmp /= 2;
		            height_tmp /= 2;
		            scale *= 2;
		        }
		        Log.d("DANCE ACTIVITY", "ratio width: " + width_tmp + " ratio height: " + height_tmp);
		        BitmapFactory.Options o2 = new BitmapFactory.Options();
		        o2.inSampleSize = scale;
		        Bitmap tempBitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, o2);
		        
		        ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
		        tempBitmap.compress(CompressFormat.PNG, 0, bos); 
		        byte[] bitmapdata = bos.toByteArray();
		        ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);
				
		        return bs;
	        } else {
	        	ContentResolver cr = context.getContentResolver();
				InputStream in = cr.openInputStream(uri);
				return in;
	        }
	        
	        //ContentResolver cr = context.getContentResolver();
			//InputStream in = cr.openInputStream(uri);
			//return in;
			
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
