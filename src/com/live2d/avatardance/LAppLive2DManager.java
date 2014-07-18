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
 *  LAppLive2DManager�?��?Live2D関連�?��?�令塔�?��?��?�モデル�?ビュー�?イベント等を管�?��?�るクラス（�?�サンプル実装）�?��?�り�?��?�。
 *
 *  外部（ゲーム等アプリケーション本体）�?�Live2D関連クラス�?��?�連�?�を�?��?�クラス�?�ラップ�?��?�独立性を高�?�?��?��?��?�。
 *
 *  ビュー（LAppView）�?�発生�?��?�イベント�?��?�?��?�クラス�?�イベント処�?�用メソッド（tapEvent()等）を呼�?�出�?��?��?�。
 *  イベント処�?�用メソッド�?��?��?イベント発生時�?�キャラクター�?��??応（特定アニメーション開始等）を記述�?��?��?�。
 *
 *  �?��?�サンプル�?��?�得�?��?��?�るイベント�?��?タップ�?ダブルタップ�?シェイク�?ドラッグ�?フリック�?加速度�?キャラ最大化・最�?化�?��?�。
 *
 */
public class LAppLive2DManager
{
	//  ログ用タグ
	static public final String 	TAG = "SampleLive2DManager";

	private LAppView 				view;						// モデル表示用View

	// モデルデータ
	private ArrayList<LAppModel>	models;


	//  ボタン�?�ら実行�?��??るサンプル機能
	private int 					modelCount		=-1;
	private boolean 				reloadFlg;					//  モデル�?読�?�込�?��?�フラグ



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
			models.get(i).release();// テクス�?ャ�?��?�を解放
		}

		models.clear();
	}
	
	public void setModelTime () {
		UtSystem.setUserTimeMSec((long) 1);
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
		if(reloadFlg)
		{
			// モデル切り替�?�ボタン�?�押�?�れ�?�時�?モデルを�?読�?�込�?��?�る
			reloadFlg=false;

			int no = modelCount % 4;

			try {
				switch (no) {
				case 0:// �?ル
					releaseModel();

					models.add(new LAppModel());
					models.get(0).load(gl, LAppDefine.MODEL_HARU);
					models.get(0).feedIn();
					break;
				case 1:// �?��?��??
					releaseModel();

					models.add(new LAppModel());
					models.get(0).load(gl, LAppDefine.MODEL_SHIZUKU);
					models.get(0).feedIn();
					break;
				case 2:// �?ん�?�
					releaseModel();

					models.add(new LAppModel());
					models.get(0).load(gl, LAppDefine.MODEL_WANKO);
					models.get(0).feedIn();
					break;
				case 3:// 複数モデル
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
				// ファイル�?�指定ミス�?�メモリ�?足�?�考�?�られる。復帰�?�中断�?�必�?
				Log.e(TAG,"Failed to load.");
				DanceActivity.exit();
			}
		}
	}


	/*
	 * noを指定�?��?�モデルを�?�得
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

		if(getModelNum()==0)
		{

			changeModel();

		}
	}


	//=========================================================
	// 	アプリケーション�?�ら�?�サンプルイベント
	//=========================================================
	/*
	 * モデル�?�切り替�?�
	 */
	public void changeModel()
	{
		reloadFlg=true;// フラグ�?��?�立�?��?�次回update時�?�切り替�?�
		modelCount++;
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
		}
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
	 * 画�?��?�最大�?��?��?��?��?��??�?�イベント
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
	 * 画�?��?�最�?�?��?��?��?��?��??�?�イベント
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
	 * シェイクイベント
	 *
	 * LAppView�?��?�シェイクイベントを感知�?��?�時�?�呼�?�れ�?
	 * シェイク時�?�モデル�?�動�??を開始�?��?��?�。
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
