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
 * LAppView�?��?GLSurfaceViewを継承�?�るOpenGL�?�View�?�サンプル
 *
 * 画�?��?��?期化�?ビュー�?�関連�?�るイベント処�?�（タッ�?関連）�?��?�を行�?��?��?�。
 * Live2Dを�?�むOpenGL�?��??画処�?��?��?LAppRender�?�移譲�?��?��?�。
 *
 */
public class LAppView extends GLSurfaceView {
	//  ログ用タグ
	static public final String 		TAG = "LAppView";

	// �??画を行�?�View
	private LAppRenderer 				renderer ;

	// Live2D関連�?�イベント�?�管�?�。コンストラクタ�?��?�照を�?��?��?�る。
	private LAppLive2DManager 			delegate;
	private L2DMatrix44 				deviceToScreen;
	private L2DViewMatrix 				viewMatrix;// 画�?��?�拡大縮�?�?移動用�?�行列
	private AccelHelper 				accelHelper;// 加速度センサ�?�制御
	private TouchManager 				touchMgr;// ピン�?�?��?�
	private L2DTargetPoint 				dragMgr;// ドラッグ�?�よるアニメーション�?�管�?�

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


		// デ�?イス座標�?�らスクリーン座標�?�変�?��?�る�?��?�?�
		deviceToScreen=new L2DMatrix44();

		// 画�?��?�表示�?�拡大縮�?や移動�?�変�?�を行�?�行列
		viewMatrix=new L2DViewMatrix();

		// 表示範囲�?�設定
		viewMatrix.setMaxScale( LAppDefine.VIEW_MAX_SCALE );// �?界拡大率
		viewMatrix.setMinScale( LAppDefine.VIEW_MIN_SCALE );// �?界縮�?率


		// 表示�?��??る最大範囲
		viewMatrix.setMaxScreenRect(
				LAppDefine.VIEW_LOGICAL_MAX_LEFT,
				LAppDefine.VIEW_LOGICAL_MAX_RIGHT,
				LAppDefine.VIEW_LOGICAL_MAX_BOTTOM,
				LAppDefine.VIEW_LOGICAL_MAX_TOP
				);

		// タッ�?関係�?�イベント管�?�
		touchMgr=new TouchManager();

		dragMgr  = new L2DTargetPoint();
		
		
	}


	public void startAccel(Activity activity)
	{
		// 加速度関係�?�イベント
		accelHelper = new AccelHelper(activity) ;
	}


	/*
	 * タッ�?イベント。
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

			// タッ�?数を�?�得
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
				// タッ�?数 3以上
			}

	        break;
	    case MotionEvent.ACTION_UP:
	    	touchesEnded();
	        break;
	    case MotionEvent.ACTION_MOVE:
	    	// タッ�?数を�?�得
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
				// タッ�?数 3以上
			}
	        break;
	    case MotionEvent.ACTION_CANCEL:
	        break;
	    }
        ret |= gestureDetector.onTouchEvent(event) ;

        return ret ;
	}


	/*
	 * Activity�?��?開�?�れ�?�時�?�イベント
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
	 * Activity�?��?ーズ�?�れ�?�時�?�イベント
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

		viewMatrix.setScreenRect(left,right,bottom,top);// デ�?イス�?�対応�?�る画�?��?�範囲。 X�?�左端, X�?��?�端, Y�?�下端, Y�?�上端

		float screenW=Math.abs(left-right);
		deviceToScreen.identity() ;
		deviceToScreen.multTranslate(-width/2.0f,height/2.0f );
		deviceToScreen.multScale( screenW/width , screenW/width );
	}


	public void update()
	{
		dragMgr.update();// ドラッグ用パラメータ�?�更新
		delegate.setDrag(dragMgr.getX(), dragMgr.getY());

		accelHelper.update();

		if( accelHelper.getShake() > 1.5f )
		{
			if(LAppDefine.DEBUG_LOG)Log.d(TAG, "shake event");
			// シェイクモーションを起動�?�る
			delegate.shakeEvent() ;
			accelHelper.resetShake() ;
		}

		delegate.setAccel(accelHelper.getAccelX(), accelHelper.getAccelY(), accelHelper.getAccelZ());
		renderer.setAccel(accelHelper.getAccelX(), accelHelper.getAccelY(), accelHelper.getAccelZ());
	}


	/*
	 * 画�?�表示�?�行列を更新。
	 *
	 * @param dx 移動幅
	 * @param dy 移動幅
	 * @param cx 拡大�?�中心
	 * @param cy 拡大�?�中心
	 * @param scale 拡大率
	 */
	public void updateViewMatrix(float dx, float dy, float cx, float cy,float scale,boolean enableEvent)
	{
		boolean isMaxScale=viewMatrix.isMaxScale();
		boolean isMinScale=viewMatrix.isMinScale();

		// 拡大縮�?
		viewMatrix.adjustScale(cx, cy, scale);

		// 移動
		viewMatrix.adjustTranslate(dx, dy) ;

		if(enableEvent)
		{
			// 画�?��?�最大�?��?��?��?��?��??�?�イベント
			if( ! isMaxScale)
			{
				if(viewMatrix.isMaxScale())
				{
					delegate.maxScaleEvent();
				}
			}
			// 画�?��?�最�?�?��?��?��?��?��??�?�イベント
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
		float screenX = deviceToScreen.transformX( deviceX );// 論�?�座標変�?��?��?�座標を�?�得。
		return  viewMatrix.invertTransformX(screenX);// 拡大�?縮�?�?移動後�?�値。
	}


	private float transformDeviceToViewY(float deviceY)
	{
		float screenY = deviceToScreen.transformY( deviceY );// 論�?�座標変�?��?��?�座標を�?�得。
		return  viewMatrix.invertTransformY(screenY);// 拡大�?縮�?�?移動後�?�値。
	}


	/*
	 * タッ�?を開始�?��?��?��??�?�イベント
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
	 * ドラッグ�?��?��?��??�?�イベント
	 */
	public void touchesMoved(float p1x,float p1y)
	{
		if(LAppDefine.DEBUG_LOG)Log.v(TAG, "touchesMoved"+"x:"+p1x+" y:"+p1y);
		touchMgr.touchesMoved(p1x,p1y);
		float x=transformDeviceToViewX( touchMgr.getX() );
		float y=transformDeviceToViewY( touchMgr.getY() );

		dragMgr.set(x, y);

		final int FLICK_DISTANCE=100;// �?��?�値以上フリック�?��?�らイベント発生

		// フリックイベント�?�判定

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

		// 画�?��?�拡大縮�?�?移動�?�設定
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
	 * タッ�?を終了�?��?��?��??�?�イベント
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
	 * Gesture�?�補助クラス。
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

