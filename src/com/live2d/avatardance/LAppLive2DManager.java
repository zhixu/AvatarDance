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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

public class LAppLive2DManager
{

	static public final String 	TAG = "Live2DManager";

	private LAppView 				view;		
	private DanceActivity 			activity;

	private LAppModel model;
	
	private int modelNum;
	private String modelPath;
	private boolean isDance = false;
	private boolean isUpdateModel = false;
	private boolean isDefaultModel = true;
	private long time;
	private float timePerBeat;
	private long prevTime; // the previous time at which the update function has been called.

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
			
			String modelURI = "miku";
			
			if (modelPath != null) {
				modelURI = modelPath;
			}
			
			Log.d(TAG, modelURI);
			
			if (modelURI.equals("miku")) {
				model.load(gl, LAppDefine.MODEL_HARU, true);
			} else {
				model.load(gl, modelURI, false);
			}
			
			model.feedIn();
			
		} catch (Exception e) {
				Log.e(TAG,"Failed to load.");
				activity.displayError();
				//DanceActivity.exit();
		}
	}
	
	public void switchDefaultModel() {
		isDefaultModel = true;
		
		if (modelNum < 1) {
			modelNum++;
		} else {
			modelNum = 0;
		}
		
		setDefaultModelPath();
		
		isUpdateModel = true;
	}
	
	public void setDefaultModelPath() {
		switch (modelNum) {
		case 0:
			modelPath = LAppDefine.MODEL_MIKU;
			break;
		}
	}
	
	public void switchInputModel(String path) {
		isDefaultModel = false;
		modelPath = path;
		isUpdateModel = true;
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
		
		timePerBeat = 1000;
		
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
		
		if (isUpdateModel) {
			isUpdateModel = false;
			
			releaseModel();
			
			try {
				model.load(gl, modelPath, isDefaultModel);
				
			} catch (Exception e) {
				Log.e(TAG,"Failed to load.");
				activity.displayError();
				setDefaultModelPath();
			}
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
	}


	/*
	 * 画�?��?�最大�?��?��?��?��?��??�?�イベント
	 */
	public void maxScaleEvent()
	{
		if(LAppDefine.DEBUG_LOG)Log.d(TAG, "Max scale event.");
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
