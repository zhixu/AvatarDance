/**
 *
 *  You can modify and use this source freely
 *  only for the development of application related Live2D.
 *
 *  (c) Live2D Inc. All rights reserved.
 */
package jp.live2d.utils.android;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;

/*
 * サウンド関連
 *
 */
public class SoundUtil {

	/*
	 * リソースを解放する。
	 * @param player
	 */
	static public void release(MediaPlayer player){
		if( player != null ){
			player.setOnCompletionListener(null) ;
			player.release() ;
			player=null;
		}
	}


	/*
	 * アセットからデータを読み込む。
	 * @param context
	 * @param filename
	 * @return
	 */
//	static public MediaPlayer loadAssets(Context context,String filename){
//
//
//		final MediaPlayer player = new MediaPlayer() ;
//
//		try {
//			final AssetFileDescriptor assetFileDescritorArticle = context.getAssets().openFd( filename );
//			player.reset();
//
//			player.setDataSource( assetFileDescritorArticle.getFileDescriptor(),
//					assetFileDescritorArticle.getStartOffset(), assetFileDescritorArticle.getLength() );
//			player.setAudioStreamType(AudioManager.STREAM_MUSIC);
//			assetFileDescritorArticle.close();
//
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		return player;
//	}


	/*
	 * アセットファイルから再生。
	 * @param context
	 * @param filename
	 */
	static public void playAssets(Context context,String filename){
		final MediaPlayer player = new MediaPlayer() ;

		try {
			final AssetFileDescriptor assetFileDescritorArticle = context.getAssets().openFd( filename );
			player.reset();

			player.setDataSource( assetFileDescritorArticle.getFileDescriptor(),
					assetFileDescritorArticle.getStartOffset(), assetFileDescritorArticle.getLength() );
			player.setAudioStreamType(AudioManager.STREAM_MUSIC);
			assetFileDescritorArticle.close();

			// 再生準備完了時のイベント登録
			player.setOnPreparedListener(new OnPreparedListener() {

				public void onPrepared(MediaPlayer mp) {
					player.start();

				}
			});

			// 再生完了時のイベント登録
			player.setOnCompletionListener( new MediaPlayer.OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {
					release(player);
				}
			});
			player.prepareAsync();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
