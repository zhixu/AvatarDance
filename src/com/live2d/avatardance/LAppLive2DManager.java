/**
 *  You can modify and use this source freely
 *  only for the development of application related Live2D.
 *
 *  (c) Live2D Inc. All rights reserved.
 */
package com.live2d.avatardance;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import jp.live2d.Live2D;
import jp.live2d.framework.L2DViewMatrix;
import jp.live2d.framework.Live2DFramework;
import jp.live2d.util.UtSystem;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

public class LAppLive2DManager
{

	static public final String 	TAG = "SampleLive2DManager";

	private LAppView 				view;		
	private DanceActivity 			activity;

	private LAppModel model;
	
	private boolean isDance = true;
	private long time;
	private float timePerBeat;
	private long prevTime; // the previous time at which the update function has been called.

	//private String modelURI = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/miku.model.json";

	public LAppLive2DManager(DanceActivity act)
	{
		Live2D.init();
		Live2DFramework.setPlatformManager(new PlatformManager());
		
		activity = act;
		
		model = new LAppModel();
		
		time = 0;
		prevTime = 0;
	}
	
	public void loadModel(GL10 gl) {
		try {
			
			SharedPreferences prefs = activity.getSharedPreferences("user", activity.getApplicationContext().MODE_PRIVATE);
			String modelURI = prefs.getString("avatarJSON", null);
			
			model.load(gl, modelURI);
			model.feedIn();
		} catch (Exception e) {
				Log.e(TAG,"Failed to load.");
				DanceActivity.exit();
		}
	}


	public void releaseModel()
	{
		model.release();
	}
	
	public void danceSetBPM (float bpm) {
		if (bpm == -1) {
			timePerBeat = 1000;
		} else {
			timePerBeat = bpm*1000/60;
		}
	}
	
	public void danceResetBPM (float bpm) {
		
		/*
		UtSystem.updateUserTimeMSec();
		prevTime = UtSystem.getUserTimeMSec();
		UtSystem.setUserTimeMSec(prevTime);
		time = prevTime;*/
		
		timePerBeat = 1000;//(long) 681;//(long) bpm*60/1000;
		
	}


	/*
	 * モデル�?�管�?�状態�?��?��?�更新。
	 *
	 * レンダラ（LAppRenderer）�?�onDrawFrame()�?�らモデル�??画�?��?�?�毎回呼�?�れ�?��?�。
	 * モデル�?�切り替�?��?��?��?�必�?�?�場�?��?��?��?��?�行�?��?��?�下�?��?�。
	 *
	 * モデル�?�パラメータ（モーション）�?��?��?�更新�?�draw�?�行�?��?�下�?��?�。
	 *
	 * @param gl
	 */
	public void update(GL10 gl)
	{
		view.update();
		
		UtSystem.updateUserTimeMSec();
		long currTime = UtSystem.getUserTimeMSec();
		
		if (timePerBeat != 0) {
			float deltaTime = (currTime - prevTime); // to get the % of beat that passed
			float ratio = deltaTime/1000;
			long warpedTime = (long) (ratio*timePerBeat);

			time += warpedTime;
			prevTime = currTime;
			UtSystem.setUserTimeMSec(time);
		}
	}


	/*
	 * noを指定�?��?�モデルを�?�得
	 *
	 * @param no
	 * @return
	 */
	
	public LAppModel getModel() {
		return model;
	}

	//=========================================================
	// 	アプリケーション�?�（SampleApplication）�?�ら呼�?�れる処�?�
	//=========================================================
	/*
	 * LAppView(Live2Dを表示�?�る�?��?�?�View)を生�?�?��?��?�。
	 *
	 * @param act
	 * @return
	 */
	public LAppView  createView(Activity act)
	{
		// View�?��?期化
		view = new LAppView( act ) ;
		view.setLive2DManager(this);
		view.startAccel(act);
		return view ;
	}
	
	public LAppView getView() {
		return view;
	}


	/*
	 * Activity�?��?開�?�れ�?�時�?�イベント
	 */
	public void onResume()
	{
		if(LAppDefine.DEBUG_LOG)Log.d(TAG, "onResume");
		view.onResume();
	}


	/*
	 * Activity�?��?ーズ�?�れ�?�時�?�イベント
	 */
	public void onPause()
	{
		if(LAppDefine.DEBUG_LOG)Log.d(TAG, "onPause");
		view.onPause();
	}


	/*
	 * GLSurfaceView�?�画�?�変更時�?�イベント
	 * @param context
	 * @param width
	 * @param height
	 */
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		if(LAppDefine.DEBUG_LOG)Log.d(TAG, "onSurfaceChanged "+width+" "+height);
		view.setupView(width,height);
	}


	//=========================================================
	// 	アプリケーション�?�ら�?�サンプルイベント
	//=========================================================
	/*
	 * モデル�?�切り替�?�
	 */
	/*public void changeModel()
	{
		reloadFlg=true;// フラグ�?��?�立�?��?�次回update時�?�切り替�?�
		modelCount++;
	}*/

	public void danceStop() {
		isDance = false;
		model.danceStop();
	}
	
	public void danceStart() {
		isDance = true;
		model.danceStart();
	}

	//=========================================================
	// 	LAppView�?�ら呼�?�れる�?�種イベント
	//=========================================================
	/*
	 * タップ�?��?��?��??�?�イベント
	 * @param x	タップ�?�座標 x
	 * @param y	タップ�?�座標 y
	 * @return
	 */
	
	public boolean tapEvent(float x,float y)
	{
		if(LAppDefine.DEBUG_LOG)Log.d(TAG, "tapEvent view x:"+x+" y:"+y);
		model.startRandomMotion(LAppDefine.MOTION_GROUP_IDLE, LAppDefine.PRIORITY_NORMAL );

		/*
		for (int i=0; i<models.size(); i++)
		{
			if(models.get(i).hitTest(  LAppDefine.HIT_AREA_HEAD,x, y ))
			{
				// 顔をタップ�?��?�ら表情切り替�?�
				if(LAppDefine.DEBUG_LOG)Log.d(TAG, "Tap face.");
				models.get(i).setRandomExpression();
			}
			else if(models.get(i).hitTest( LAppDefine.HIT_AREA_BODY,x, y))
			{
				if(LAppDefine.DEBUG_LOG)Log.d(TAG, "Tap body.");
				models.get(i).startRandomMotion(LAppDefine.MOTION_GROUP_TAP_BODY, LAppDefine.PRIORITY_NORMAL );
			}
		}*/
		return true;
	}


	/*
	 * フリック�?��?�時�?�イベント
	 *
	 * LAppView�?��?�フリックイベントを感知�?��?�時�?�呼�?�れ
	 * フリック時�?�モデル�?�動�??を開始�?��?��?�。
	 *
	 * @param
	 * @param
	 * @param flickDist
	 */
	public void flickEvent(float x,float y)
	{
		if(LAppDefine.DEBUG_LOG)Log.d(TAG, "flick x:"+x+" y:"+y);

		/*
		for (int i=0; i<models.size(); i++)
		{
			if(models.get(i).hitTest( LAppDefine.HIT_AREA_HEAD, x, y ))
			{
				if(LAppDefine.DEBUG_LOG)Log.d(TAG, "Flick head.");
				models.get(i).startRandomMotion(LAppDefine.MOTION_GROUP_FLICK_HEAD, LAppDefine.PRIORITY_NORMAL );
			}
		}*/
	}


	/*
	 * 画�?��?�最大�?��?��?��?��?��??�?�イベント
	 */
	public void maxScaleEvent()
	{
		if(LAppDefine.DEBUG_LOG)Log.d(TAG, "Max scale event.");

		/*
		for (int i=0; i<models.size(); i++)
		{
			models.get(i).startRandomMotion(LAppDefine.MOTION_GROUP_PINCH_IN,LAppDefine.PRIORITY_NORMAL );
		}*/
	}


	/*
	 * 画�?��?�最�?�?��?��?��?��?��??�?�イベント
	 */
	public void minScaleEvent()
	{
		if(LAppDefine.DEBUG_LOG)Log.d(TAG, "Min scale event.");
	}


	/*
	 * シェイクイベント
	 *
	 * LAppView�?��?�シェイクイベントを感知�?��?�時�?�呼�?�れ�?
	 * シェイク時�?�モデル�?�動�??を開始�?��?��?�。
	 */
	public void shakeEvent()
	{
		if(LAppDefine.DEBUG_LOG)Log.d(TAG, "Shake event.");
	}
	
	public void setAccel(float x,float y,float z)
	{
		model.setAccel(x, y, z);
	}


	public void setDrag(float x,float y)
	{
		model.setDrag(x, y);
	}


	public L2DViewMatrix getViewMatrix()
	{
		return view.getViewMatrix();
	}
}
