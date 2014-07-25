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
	 * ãƒ¢ãƒ‡ãƒ«ã?®ç®¡ç?†çŠ¶æ…‹ã?ªã?©ã?®æ›´æ–°ã€‚
	 *
	 * ãƒ¬ãƒ³ãƒ€ãƒ©ï¼ˆLAppRendererï¼‰ã?®onDrawFrame()ã?‹ã‚‰ãƒ¢ãƒ‡ãƒ«æ??ç”»ã?®å‰?ã?«æ¯Žå›žå‘¼ã?°ã‚Œã?¾ã?™ã€‚
	 * ãƒ¢ãƒ‡ãƒ«ã?®åˆ‡ã‚Šæ›¿ã?ˆã?ªã?©ã?Œå¿…è¦?ã?ªå ´å?ˆã?¯ã?“ã?“ã?§è¡Œã?ªã?£ã?¦ä¸‹ã?•ã?„ã€‚
	 *
	 * ãƒ¢ãƒ‡ãƒ«ã?®ãƒ‘ãƒ©ãƒ¡ãƒ¼ã‚¿ï¼ˆãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ï¼‰ã?ªã?©ã?®æ›´æ–°ã?¯drawã?§è¡Œã?£ã?¦ä¸‹ã?•ã?„ã€‚
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
	 * noã‚’æŒ‡å®šã?—ã?¦ãƒ¢ãƒ‡ãƒ«ã‚’å?–å¾—
	 *
	 * @param no
	 * @return
	 */
	
	public LAppModel getModel() {
		return model;
	}

	//=========================================================
	// 	ã‚¢ãƒ—ãƒªã‚±ãƒ¼ã‚·ãƒ§ãƒ³å?´ï¼ˆSampleApplicationï¼‰ã?‹ã‚‰å‘¼ã?°ã‚Œã‚‹å‡¦ç?†
	//=========================================================
	/*
	 * LAppView(Live2Dã‚’è¡¨ç¤ºã?™ã‚‹ã?Ÿã‚?ã?®View)ã‚’ç”Ÿæˆ?ã?—ã?¾ã?™ã€‚
	 *
	 * @param act
	 * @return
	 */
	public LAppView  createView(Activity act)
	{
		// Viewã?®åˆ?æœŸåŒ–
		view = new LAppView( act ) ;
		view.setLive2DManager(this);
		view.startAccel(act);
		return view ;
	}
	
	public LAppView getView() {
		return view;
	}


	/*
	 * Activityã?Œå†?é–‹ã?•ã‚Œã?Ÿæ™‚ã?®ã‚¤ãƒ™ãƒ³ãƒˆ
	 */
	public void onResume()
	{
		if(LAppDefine.DEBUG_LOG)Log.d(TAG, "onResume");
		view.onResume();
	}


	/*
	 * Activityã?Œãƒ?ãƒ¼ã‚ºã?•ã‚Œã?Ÿæ™‚ã?®ã‚¤ãƒ™ãƒ³ãƒˆ
	 */
	public void onPause()
	{
		if(LAppDefine.DEBUG_LOG)Log.d(TAG, "onPause");
		view.onPause();
	}


	/*
	 * GLSurfaceViewã?®ç”»é?¢å¤‰æ›´æ™‚ã?®ã‚¤ãƒ™ãƒ³ãƒˆ
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
	// 	ã‚¢ãƒ—ãƒªã‚±ãƒ¼ã‚·ãƒ§ãƒ³ã?‹ã‚‰ã?®ã‚µãƒ³ãƒ—ãƒ«ã‚¤ãƒ™ãƒ³ãƒˆ
	//=========================================================
	/*
	 * ãƒ¢ãƒ‡ãƒ«ã?®åˆ‡ã‚Šæ›¿ã?ˆ
	 */
	/*public void changeModel()
	{
		reloadFlg=true;// ãƒ•ãƒ©ã‚°ã? ã?‘ç«‹ã?¦ã?¦æ¬¡å›župdateæ™‚ã?«åˆ‡ã‚Šæ›¿ã?ˆ
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
	// 	LAppViewã?‹ã‚‰å‘¼ã?°ã‚Œã‚‹å?„ç¨®ã‚¤ãƒ™ãƒ³ãƒˆ
	//=========================================================
	/*
	 * ã‚¿ãƒƒãƒ—ã?—ã?Ÿã?¨ã??ã?®ã‚¤ãƒ™ãƒ³ãƒˆ
	 * @param x	ã‚¿ãƒƒãƒ—ã?®åº§æ¨™ x
	 * @param y	ã‚¿ãƒƒãƒ—ã?®åº§æ¨™ y
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
				// é¡”ã‚’ã‚¿ãƒƒãƒ—ã?—ã?Ÿã‚‰è¡¨æƒ…åˆ‡ã‚Šæ›¿ã?ˆ
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
	 * ãƒ•ãƒªãƒƒã‚¯ã?—ã?Ÿæ™‚ã?®ã‚¤ãƒ™ãƒ³ãƒˆ
	 *
	 * LAppViewå?´ã?§ãƒ•ãƒªãƒƒã‚¯ã‚¤ãƒ™ãƒ³ãƒˆã‚’æ„ŸçŸ¥ã?—ã?Ÿæ™‚ã?«å‘¼ã?°ã‚Œ
	 * ãƒ•ãƒªãƒƒã‚¯æ™‚ã?®ãƒ¢ãƒ‡ãƒ«ã?®å‹•ã??ã‚’é–‹å§‹ã?—ã?¾ã?™ã€‚
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
	 * ç”»é?¢ã?Œæœ€å¤§ã?«ã?ªã?£ã?Ÿã?¨ã??ã?®ã‚¤ãƒ™ãƒ³ãƒˆ
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
	 * ç”»é?¢ã?Œæœ€å°?ã?«ã?ªã?£ã?Ÿã?¨ã??ã?®ã‚¤ãƒ™ãƒ³ãƒˆ
	 */
	public void minScaleEvent()
	{
		if(LAppDefine.DEBUG_LOG)Log.d(TAG, "Min scale event.");
	}


	/*
	 * ã‚·ã‚§ã‚¤ã‚¯ã‚¤ãƒ™ãƒ³ãƒˆ
	 *
	 * LAppViewå?´ã?§ã‚·ã‚§ã‚¤ã‚¯ã‚¤ãƒ™ãƒ³ãƒˆã‚’æ„ŸçŸ¥ã?—ã?Ÿæ™‚ã?«å‘¼ã?°ã‚Œã€?
	 * ã‚·ã‚§ã‚¤ã‚¯æ™‚ã?®ãƒ¢ãƒ‡ãƒ«ã?®å‹•ã??ã‚’é–‹å§‹ã?—ã?¾ã?™ã€‚
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
