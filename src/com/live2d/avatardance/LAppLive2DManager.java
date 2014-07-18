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
import android.util.Log;

/*
 *  LAppLive2DManagerã?¯ã€?Live2Dé–¢é€£ã?®å?¸ä»¤å¡”ã?¨ã?—ã?¦ãƒ¢ãƒ‡ãƒ«ã€?ãƒ“ãƒ¥ãƒ¼ã€?ã‚¤ãƒ™ãƒ³ãƒˆç­‰ã‚’ç®¡ç?†ã?™ã‚‹ã‚¯ãƒ©ã‚¹ï¼ˆã?®ã‚µãƒ³ãƒ—ãƒ«å®Ÿè£…ï¼‰ã?«ã?ªã‚Šã?¾ã?™ã€‚
 *
 *  å¤–éƒ¨ï¼ˆã‚²ãƒ¼ãƒ ç­‰ã‚¢ãƒ—ãƒªã‚±ãƒ¼ã‚·ãƒ§ãƒ³æœ¬ä½“ï¼‰ã?¨Live2Dé–¢é€£ã‚¯ãƒ©ã‚¹ã?¨ã?®é€£æ?ºã‚’ã?“ã?®ã‚¯ãƒ©ã‚¹ã?§ãƒ©ãƒƒãƒ—ã?—ã?¦ç‹¬ç«‹æ€§ã‚’é«˜ã‚?ã?¦ã?„ã?¾ã?™ã€‚
 *
 *  ãƒ“ãƒ¥ãƒ¼ï¼ˆLAppViewï¼‰ã?§ç™ºç”Ÿã?—ã?Ÿã‚¤ãƒ™ãƒ³ãƒˆã?¯ã€?ã?“ã?®ã‚¯ãƒ©ã‚¹ã?®ã‚¤ãƒ™ãƒ³ãƒˆå‡¦ç?†ç”¨ãƒ¡ã‚½ãƒƒãƒ‰ï¼ˆtapEvent()ç­‰ï¼‰ã‚’å‘¼ã?³å‡ºã?—ã?¾ã?™ã€‚
 *  ã‚¤ãƒ™ãƒ³ãƒˆå‡¦ç?†ç”¨ãƒ¡ã‚½ãƒƒãƒ‰ã?«ã?¯ã€?ã‚¤ãƒ™ãƒ³ãƒˆç™ºç”Ÿæ™‚ã?®ã‚­ãƒ£ãƒ©ã‚¯ã‚¿ãƒ¼ã?®å??å¿œï¼ˆç‰¹å®šã‚¢ãƒ‹ãƒ¡ãƒ¼ã‚·ãƒ§ãƒ³é–‹å§‹ç­‰ï¼‰ã‚’è¨˜è¿°ã?—ã?¾ã?™ã€‚
 *
 *  ã?“ã?®ã‚µãƒ³ãƒ—ãƒ«ã?§å?–å¾—ã?—ã?¦ã?„ã‚‹ã‚¤ãƒ™ãƒ³ãƒˆã?¯ã€?ã‚¿ãƒƒãƒ—ã€?ãƒ€ãƒ–ãƒ«ã‚¿ãƒƒãƒ—ã€?ã‚·ã‚§ã‚¤ã‚¯ã€?ãƒ‰ãƒ©ãƒƒã‚°ã€?ãƒ•ãƒªãƒƒã‚¯ã€?åŠ é€Ÿåº¦ã€?ã‚­ãƒ£ãƒ©æœ€å¤§åŒ–ãƒ»æœ€å°?åŒ–ã?§ã?™ã€‚
 *
 */
public class LAppLive2DManager
{
	//  ãƒ­ã‚°ç”¨ã‚¿ã‚°
	static public final String 	TAG = "SampleLive2DManager";

	private LAppView 				view;						// ãƒ¢ãƒ‡ãƒ«è¡¨ç¤ºç”¨View

	// ãƒ¢ãƒ‡ãƒ«ãƒ‡ãƒ¼ã‚¿
	private ArrayList<LAppModel>	models;


	//  ãƒœã‚¿ãƒ³ã?‹ã‚‰å®Ÿè¡Œã?§ã??ã‚‹ã‚µãƒ³ãƒ—ãƒ«æ©Ÿèƒ½
	private int 					modelCount		=-1;
	private boolean 				reloadFlg;					//  ãƒ¢ãƒ‡ãƒ«å†?èª­ã?¿è¾¼ã?¿ã?®ãƒ•ãƒ©ã‚°



	public LAppLive2DManager()
	{
		Live2D.init();
		Live2DFramework.setPlatformManager(new PlatformManager());

		models = new ArrayList<LAppModel>();
	}


	public void releaseModel()
	{
		for(int i=0;i<models.size();i++)
		{
			models.get(i).release();// ãƒ†ã‚¯ã‚¹ãƒ?ãƒ£ã?ªã?©ã‚’è§£æ”¾
		}

		models.clear();
	}
	
	public void setModelTime () {
		UtSystem.setUserTimeMSec((long) 1);
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
		if(reloadFlg)
		{
			// ãƒ¢ãƒ‡ãƒ«åˆ‡ã‚Šæ›¿ã?ˆãƒœã‚¿ãƒ³ã?ŒæŠ¼ã?•ã‚Œã?Ÿæ™‚ã€?ãƒ¢ãƒ‡ãƒ«ã‚’å†?èª­ã?¿è¾¼ã?¿ã?™ã‚‹
			reloadFlg=false;

			int no = modelCount % 4;

			try {
				switch (no) {
				case 0:// ãƒ?ãƒ«
					releaseModel();

					models.add(new LAppModel());
					models.get(0).load(gl, LAppDefine.MODEL_HARU);
					models.get(0).feedIn();
					break;
				case 1:// ã?—ã?šã??
					releaseModel();

					models.add(new LAppModel());
					models.get(0).load(gl, LAppDefine.MODEL_SHIZUKU);
					models.get(0).feedIn();
					break;
				case 2:// ã‚?ã‚“ã?“
					releaseModel();

					models.add(new LAppModel());
					models.get(0).load(gl, LAppDefine.MODEL_WANKO);
					models.get(0).feedIn();
					break;
				case 3:// è¤‡æ•°ãƒ¢ãƒ‡ãƒ«
					releaseModel();

					models.add(new LAppModel());
					models.get(0).load(gl, LAppDefine.MODEL_HARU_A);
					models.get(0).feedIn();

					models.add(new LAppModel());
					models.get(1).load(gl, LAppDefine.MODEL_HARU_B);
					models.get(1).feedIn();
					break;
				default:

					break;
				}
			} catch (Exception e) {
				// ãƒ•ã‚¡ã‚¤ãƒ«ã?®æŒ‡å®šãƒŸã‚¹ã?‹ãƒ¡ãƒ¢ãƒªä¸?è¶³ã?Œè€ƒã?ˆã‚‰ã‚Œã‚‹ã€‚å¾©å¸°ã?‹ä¸­æ–­ã?Œå¿…è¦?
				Log.e(TAG,"Failed to load.");
				DanceActivity.exit();
			}
		}
	}


	/*
	 * noã‚’æŒ‡å®šã?—ã?¦ãƒ¢ãƒ‡ãƒ«ã‚’å?–å¾—
	 *
	 * @param no
	 * @return
	 */
	public LAppModel getModel(int no)
	{
		if(no>=models.size())return null;
		return models.get(no);
	}


	public int getModelNum()
	{
		return models.size();
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

		if(getModelNum()==0)
		{

			changeModel();

		}
	}


	//=========================================================
	// 	ã‚¢ãƒ—ãƒªã‚±ãƒ¼ã‚·ãƒ§ãƒ³ã?‹ã‚‰ã?®ã‚µãƒ³ãƒ—ãƒ«ã‚¤ãƒ™ãƒ³ãƒˆ
	//=========================================================
	/*
	 * ãƒ¢ãƒ‡ãƒ«ã?®åˆ‡ã‚Šæ›¿ã?ˆ
	 */
	public void changeModel()
	{
		reloadFlg=true;// ãƒ•ãƒ©ã‚°ã? ã?‘ç«‹ã?¦ã?¦æ¬¡å›župdateæ™‚ã?«åˆ‡ã‚Šæ›¿ã?ˆ
		modelCount++;
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
		}
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

		for (int i=0; i<models.size(); i++)
		{
			if(models.get(i).hitTest( LAppDefine.HIT_AREA_HEAD, x, y ))
			{
				if(LAppDefine.DEBUG_LOG)Log.d(TAG, "Flick head.");
				models.get(i).startRandomMotion(LAppDefine.MOTION_GROUP_FLICK_HEAD, LAppDefine.PRIORITY_NORMAL );
			}
		}
	}


	/*
	 * ç”»é?¢ã?Œæœ€å¤§ã?«ã?ªã?£ã?Ÿã?¨ã??ã?®ã‚¤ãƒ™ãƒ³ãƒˆ
	 */
	public void maxScaleEvent()
	{
		if(LAppDefine.DEBUG_LOG)Log.d(TAG, "Max scale event.");

		for (int i=0; i<models.size(); i++)
		{
			models.get(i).startRandomMotion(LAppDefine.MOTION_GROUP_PINCH_IN,LAppDefine.PRIORITY_NORMAL );
		}
	}


	/*
	 * ç”»é?¢ã?Œæœ€å°?ã?«ã?ªã?£ã?Ÿã?¨ã??ã?®ã‚¤ãƒ™ãƒ³ãƒˆ
	 */
	public void minScaleEvent()
	{
		if(LAppDefine.DEBUG_LOG)Log.d(TAG, "Min scale event.");

		for (int i=0; i<models.size(); i++)
		{
			models.get(i).startRandomMotion(LAppDefine.MOTION_GROUP_PINCH_OUT,LAppDefine.PRIORITY_NORMAL );
		}
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

		for (int i=0; i<models.size(); i++)
		{
			models.get(i).startRandomMotion(LAppDefine.MOTION_GROUP_DANCE,LAppDefine.PRIORITY_FORCE );
		}
	}


	public void setAccel(float x,float y,float z)
	{
		for (int i=0; i<models.size(); i++)
		{
			models.get(i).setAccel(x, y, z);
		}
	}


	public void setDrag(float x,float y)
	{
		for (int i=0; i<models.size(); i++)
		{
			models.get(i).setDrag(x, y);
		}
	}


	public L2DViewMatrix getViewMatrix()
	{
		return view.getViewMatrix();
	}
}
