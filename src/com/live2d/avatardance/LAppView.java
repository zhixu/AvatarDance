/**
 *
 *  You can modify and use this source freely
 *  only for the development of application related Live2D.
 *
 *  (c) Live2D Inc. All rights reserved.
 */
package com.live2d.avatardance;

import jp.live2d.framework.L2DMatrix44;
import jp.live2d.framework.L2DTargetPoint;
import jp.live2d.framework.L2DViewMatrix;
import jp.live2d.utils.android.AccelHelper;
import jp.live2d.utils.android.TouchManager;
import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

/*
 * LAppViewã?¯ã€?GLSurfaceViewã‚’ç¶™æ‰¿ã?™ã‚‹OpenGLã?®Viewã?®ã‚µãƒ³ãƒ—ãƒ«
 *
 * ç”»é?¢ã?®åˆ?æœŸåŒ–ã€?ãƒ“ãƒ¥ãƒ¼ã?«é–¢é€£ã?™ã‚‹ã‚¤ãƒ™ãƒ³ãƒˆå‡¦ç?†ï¼ˆã‚¿ãƒƒãƒ?é–¢é€£ï¼‰ã?ªã?©ã‚’è¡Œã?„ã?¾ã?™ã€‚
 * Live2Dã‚’å?«ã‚€OpenGLã?®æ??ç”»å‡¦ç?†ã?¯ã€?LAppRenderã?«ç§»è­²ã?—ã?¾ã?™ã€‚
 *
 */
public class LAppView extends GLSurfaceView {
	//  ãƒ­ã‚°ç”¨ã‚¿ã‚°
	static public final String 		TAG = "LAppView";

	// æ??ç”»ã‚’è¡Œã?†View
	private LAppRenderer 				renderer ;

	// Live2Dé–¢é€£ã?®ã‚¤ãƒ™ãƒ³ãƒˆã?®ç®¡ç?†ã€‚ã‚³ãƒ³ã‚¹ãƒˆãƒ©ã‚¯ã‚¿ã?§å?‚ç…§ã‚’å?—ã?‘å?–ã‚‹ã€‚
	private LAppLive2DManager 			delegate;
	private L2DMatrix44 				deviceToScreen;
	private L2DViewMatrix 				viewMatrix;// ç”»é?¢ã?®æ‹¡å¤§ç¸®å°?ã€?ç§»å‹•ç”¨ã?®è¡Œåˆ—
	private AccelHelper 				accelHelper;// åŠ é€Ÿåº¦ã‚»ãƒ³ã‚µã?®åˆ¶å¾¡
	private TouchManager 				touchMgr;// ãƒ”ãƒ³ãƒ?ã?ªã?©
	private L2DTargetPoint 				dragMgr;// ãƒ‰ãƒ©ãƒƒã‚°ã?«ã‚ˆã‚‹ã‚¢ãƒ‹ãƒ¡ãƒ¼ã‚·ãƒ§ãƒ³ã?®ç®¡ç?†

	GestureDetector 					gestureDetector;

	public LAppView(  Context context )
	{
		super( context ) ;
		setFocusable(true);
	}


	public void setLive2DManager( LAppLive2DManager live2DMgr)
	{
		this.delegate = live2DMgr ;
		this.renderer = new LAppRenderer( live2DMgr  ) ;

		
		setEGLConfigChooser(false);
		//getHolder().setFormat(PixelFormat.TRANSLUCENT);
		setRenderer(renderer);

		gestureDetector = new GestureDetector(this.getContext()  , simpleOnGestureListener ) ;


		// ãƒ‡ãƒ?ã‚¤ã‚¹åº§æ¨™ã?‹ã‚‰ã‚¹ã‚¯ãƒªãƒ¼ãƒ³åº§æ¨™ã?«å¤‰æ?›ã?™ã‚‹ã?Ÿã‚?ã?®
		deviceToScreen=new L2DMatrix44();

		// ç”»é?¢ã?®è¡¨ç¤ºã?®æ‹¡å¤§ç¸®å°?ã‚„ç§»å‹•ã?®å¤‰æ?›ã‚’è¡Œã?†è¡Œåˆ—
		viewMatrix=new L2DViewMatrix();

		// è¡¨ç¤ºç¯„å›²ã?®è¨­å®š
		viewMatrix.setMaxScale( LAppDefine.VIEW_MAX_SCALE );// é™?ç•Œæ‹¡å¤§çŽ‡
		viewMatrix.setMinScale( LAppDefine.VIEW_MIN_SCALE );// é™?ç•Œç¸®å°?çŽ‡


		// è¡¨ç¤ºã?§ã??ã‚‹æœ€å¤§ç¯„å›²
		viewMatrix.setMaxScreenRect(
				LAppDefine.VIEW_LOGICAL_MAX_LEFT,
				LAppDefine.VIEW_LOGICAL_MAX_RIGHT,
				LAppDefine.VIEW_LOGICAL_MAX_BOTTOM,
				LAppDefine.VIEW_LOGICAL_MAX_TOP
				);

		// ã‚¿ãƒƒãƒ?é–¢ä¿‚ã?®ã‚¤ãƒ™ãƒ³ãƒˆç®¡ç?†
		touchMgr=new TouchManager();

		dragMgr  = new L2DTargetPoint();
		
		
	}


	public void startAccel(Activity activity)
	{
		// åŠ é€Ÿåº¦é–¢ä¿‚ã?®ã‚¤ãƒ™ãƒ³ãƒˆ
		accelHelper = new AccelHelper(activity) ;
	}


	/*
	 * ã‚¿ãƒƒãƒ?ã‚¤ãƒ™ãƒ³ãƒˆã€‚
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
	    boolean ret = false ;
	    int touchNum;
	    switch (event.getAction())
	    {
	    case MotionEvent.ACTION_DOWN:
	        ret = true ;

			// ã‚¿ãƒƒãƒ?æ•°ã‚’å?–å¾—
			touchNum = event.getPointerCount() ;

			if( touchNum == 1 )
			{
				touchesBegan(event.getX(),event.getY());
			}
			else if( touchNum == 2 )
			{
				touchesBegan(event.getX(0),event.getY(0),event.getX(1),event.getY(1));
			}
			else
			{
				// ã‚¿ãƒƒãƒ?æ•° 3ä»¥ä¸Š
			}

	        break;
	    case MotionEvent.ACTION_UP:
	    	touchesEnded();
	        break;
	    case MotionEvent.ACTION_MOVE:
	    	// ã‚¿ãƒƒãƒ?æ•°ã‚’å?–å¾—
			touchNum = event.getPointerCount() ;

			if( touchNum == 1 )
			{
				touchesMoved(event.getX(),event.getY());
			}
			else if( touchNum == 2 )
			{
				touchesMoved(event.getX(0),event.getY(0),event.getX(1),event.getY(1));
			}
			else
			{
				// ã‚¿ãƒƒãƒ?æ•° 3ä»¥ä¸Š
			}
	        break;
	    case MotionEvent.ACTION_CANCEL:
	        break;
	    }
        ret |= gestureDetector.onTouchEvent(event) ;

        return ret ;
	}


	/*
	 * Activityã?Œå†?é–‹ã?•ã‚Œã?Ÿæ™‚ã?®ã‚¤ãƒ™ãƒ³ãƒˆ
	 */
	public void onResume()
	{
		if(accelHelper!=null)
		{
			if(LAppDefine.DEBUG_LOG)Log.d(TAG, "start accelHelper");
			accelHelper.start();
		}
	}


	/*
	 * Activityã?Œãƒ?ãƒ¼ã‚ºã?•ã‚Œã?Ÿæ™‚ã?®ã‚¤ãƒ™ãƒ³ãƒˆ
	 */
	public void onPause()
	{
		if(accelHelper!=null)
		{
			if(LAppDefine.DEBUG_LOG)Log.d(TAG, "stop accelHelper");
			accelHelper.stop();
		}
	}


	public void setupView( int width, int height)
	{
		float ratio=(float)height/width;
		float left = LAppDefine.VIEW_LOGICAL_LEFT;
		float right = LAppDefine.VIEW_LOGICAL_RIGHT;
		float bottom = -ratio;
		float top = ratio;

		viewMatrix.setScreenRect(left,right,bottom,top);// ãƒ‡ãƒ?ã‚¤ã‚¹ã?«å¯¾å¿œã?™ã‚‹ç”»é?¢ã?®ç¯„å›²ã€‚ Xã?®å·¦ç«¯, Xã?®å?³ç«¯, Yã?®ä¸‹ç«¯, Yã?®ä¸Šç«¯

		float screenW=Math.abs(left-right);
		deviceToScreen.identity() ;
		deviceToScreen.multTranslate(-width/2.0f,height/2.0f );
		deviceToScreen.multScale( screenW/width , screenW/width );
	}


	public void update()
	{
		dragMgr.update();// ãƒ‰ãƒ©ãƒƒã‚°ç”¨ãƒ‘ãƒ©ãƒ¡ãƒ¼ã‚¿ã?®æ›´æ–°
		delegate.setDrag(dragMgr.getX(), dragMgr.getY());

		accelHelper.update();

		if( accelHelper.getShake() > 1.5f )
		{
			if(LAppDefine.DEBUG_LOG)Log.d(TAG, "shake event");
			// ã‚·ã‚§ã‚¤ã‚¯ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ã‚’èµ·å‹•ã?™ã‚‹
			delegate.shakeEvent() ;
			accelHelper.resetShake() ;
		}

		delegate.setAccel(accelHelper.getAccelX(), accelHelper.getAccelY(), accelHelper.getAccelZ());
		renderer.setAccel(accelHelper.getAccelX(), accelHelper.getAccelY(), accelHelper.getAccelZ());
	}


	/*
	 * ç”»é?¢è¡¨ç¤ºã?®è¡Œåˆ—ã‚’æ›´æ–°ã€‚
	 *
	 * @param dx ç§»å‹•å¹…
	 * @param dy ç§»å‹•å¹…
	 * @param cx æ‹¡å¤§ã?®ä¸­å¿ƒ
	 * @param cy æ‹¡å¤§ã?®ä¸­å¿ƒ
	 * @param scale æ‹¡å¤§çŽ‡
	 */
	public void updateViewMatrix(float dx, float dy, float cx, float cy,float scale,boolean enableEvent)
	{
		boolean isMaxScale=viewMatrix.isMaxScale();
		boolean isMinScale=viewMatrix.isMinScale();

		// æ‹¡å¤§ç¸®å°?
		viewMatrix.adjustScale(cx, cy, scale);

		// ç§»å‹•
		viewMatrix.adjustTranslate(dx, dy) ;

		if(enableEvent)
		{
			// ç”»é?¢ã?Œæœ€å¤§ã?«ã?ªã?£ã?Ÿã?¨ã??ã?®ã‚¤ãƒ™ãƒ³ãƒˆ
			if( ! isMaxScale)
			{
				if(viewMatrix.isMaxScale())
				{
					delegate.maxScaleEvent();
				}
			}
			// ç”»é?¢ã?Œæœ€å°?ã?«ã?ªã?£ã?Ÿã?¨ã??ã?®ã‚¤ãƒ™ãƒ³ãƒˆ
			if( ! isMinScale)
			{
				if(viewMatrix.isMinScale())
				{
					delegate.minScaleEvent();
				}
			}
		}
	}


	private float transformDeviceToViewX(float deviceX)
	{
		float screenX = deviceToScreen.transformX( deviceX );// è«–ç?†åº§æ¨™å¤‰æ?›ã?—ã?Ÿåº§æ¨™ã‚’å?–å¾—ã€‚
		return  viewMatrix.invertTransformX(screenX);// æ‹¡å¤§ã€?ç¸®å°?ã€?ç§»å‹•å¾Œã?®å€¤ã€‚
	}


	private float transformDeviceToViewY(float deviceY)
	{
		float screenY = deviceToScreen.transformY( deviceY );// è«–ç?†åº§æ¨™å¤‰æ?›ã?—ã?Ÿåº§æ¨™ã‚’å?–å¾—ã€‚
		return  viewMatrix.invertTransformY(screenY);// æ‹¡å¤§ã€?ç¸®å°?ã€?ç§»å‹•å¾Œã?®å€¤ã€‚
	}


	/*
	 * ã‚¿ãƒƒãƒ?ã‚’é–‹å§‹ã?—ã?Ÿã?¨ã??ã?®ã‚¤ãƒ™ãƒ³ãƒˆ
	 */
	public void touchesBegan(float p1x,float p1y)
	{
		if(LAppDefine.DEBUG_LOG)Log.v(TAG, "touchesBegan"+" x:"+p1x+" y:"+p1y);
		touchMgr.touchBegan(p1x,p1y);

		float x=transformDeviceToViewX( touchMgr.getX() );
		float y=transformDeviceToViewY( touchMgr.getY() );

		dragMgr.set(x, y);
	}


	public void touchesBegan(float p1x,float p1y,float p2x,float p2y)
	{
		if(LAppDefine.DEBUG_LOG)Log.v(TAG, "touchesBegan"+" x1:"+p1x+" y1:"+p1y+" x2:"+p2x+" y2:"+p2y);
		touchMgr.touchBegan(p1x,p1y,p2x,p2y);

		float x=transformDeviceToViewX( touchMgr.getX() );
		float y=transformDeviceToViewY( touchMgr.getY() );

		dragMgr.set(x, y);
	}


	/*
	 * ãƒ‰ãƒ©ãƒƒã‚°ã?—ã?Ÿã?¨ã??ã?®ã‚¤ãƒ™ãƒ³ãƒˆ
	 */
	public void touchesMoved(float p1x,float p1y)
	{
		if(LAppDefine.DEBUG_LOG)Log.v(TAG, "touchesMoved"+"x:"+p1x+" y:"+p1y);
		touchMgr.touchesMoved(p1x,p1y);
		float x=transformDeviceToViewX( touchMgr.getX() );
		float y=transformDeviceToViewY( touchMgr.getY() );

		dragMgr.set(x, y);

		final int FLICK_DISTANCE=100;// ã?“ã?®å€¤ä»¥ä¸Šãƒ•ãƒªãƒƒã‚¯ã?—ã?Ÿã‚‰ã‚¤ãƒ™ãƒ³ãƒˆç™ºç”Ÿ

		// ãƒ•ãƒªãƒƒã‚¯ã‚¤ãƒ™ãƒ³ãƒˆã?®åˆ¤å®š

		if(touchMgr.isSingleTouch() && touchMgr.isFlickAvailable() )
		{
			float flickDist=touchMgr.getFlickDistance();
			if(flickDist>FLICK_DISTANCE)
			{

				float startX=transformDeviceToViewX( touchMgr.getStartX() );
				float startY=transformDeviceToViewY( touchMgr.getStartY() );
				delegate.flickEvent(startX,startY);
				touchMgr.disableFlick();
			}
		}
	}


	public void touchesMoved(float p1x,float p1y,float p2x,float p2y)
	{
		if(LAppDefine.DEBUG_LOG)Log.v(TAG, "touchesMoved"+" x1:"+p1x+" y1:"+p1y+" x2:"+p2x+" y2:"+p2y);
		touchMgr.touchesMoved(p1x,p1y,p2x,p2y);

		// ç”»é?¢ã?®æ‹¡å¤§ç¸®å°?ã€?ç§»å‹•ã?®è¨­å®š
		float dx= touchMgr.getDeltaX() * deviceToScreen.getScaleX();
		float dy= touchMgr.getDeltaY() * deviceToScreen.getScaleY() ;
		float cx= deviceToScreen.transformX( touchMgr.getCenterX() ) * touchMgr.getScale();
		float cy= deviceToScreen.transformY( touchMgr.getCenterY() ) * touchMgr.getScale();
		float scale=touchMgr.getScale();

		if(LAppDefine.DEBUG_LOG)Log.v(TAG, "view  dx:"+dx+" dy:"+dy+" cx:"+cx+" cy:"+cy+" scale:"+scale);

		updateViewMatrix(dx,dy,cx,cy,scale,true);

		float x=transformDeviceToViewX( touchMgr.getX() );
		float y=transformDeviceToViewY( touchMgr.getY() );

		dragMgr.set(x, y);
	}


	/*
	 * ã‚¿ãƒƒãƒ?ã‚’çµ‚äº†ã?—ã?Ÿã?¨ã??ã?®ã‚¤ãƒ™ãƒ³ãƒˆ
	 * @param event
	 */
	public void touchesEnded()
	{
		if(LAppDefine.DEBUG_LOG)Log.v(TAG, "touchesEnded");
		dragMgr.set(0,0);
	}


	public L2DViewMatrix getViewMatrix()
	{
		return viewMatrix;
	}


	/*
	 * Gestureã?®è£œåŠ©ã‚¯ãƒ©ã‚¹ã€‚
	 */
	private final SimpleOnGestureListener simpleOnGestureListener = new SimpleOnGestureListener()
	{
        @Override
        public boolean onDoubleTap(MotionEvent event)
        {
        	return super.onDoubleTap(event) ;
        }

        @Override
        public boolean onDown(MotionEvent event)
        {
            super.onDown(event);
            return true ;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event)
        {
        	float x=transformDeviceToViewX( touchMgr.getX() );
    		float y=transformDeviceToViewY( touchMgr.getY() );
          	boolean ret = delegate.tapEvent(x,y);//Live2D Event
          	ret |= super.onSingleTapUp(event);
            return ret ;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event)
        {
            return super.onSingleTapUp(event) ;
        }
    };

}

