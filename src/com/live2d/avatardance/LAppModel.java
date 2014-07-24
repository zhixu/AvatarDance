/**
 *
 *  You can modify and use this source freely
 *  only for the development of application related Live2D.
 *
 *  (c) Live2D Inc. All rights reserved.
 */
package com.live2d.avatardance;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;

import jp.live2d.Live2D;
import jp.live2d.android.Live2DModelAndroid;
import jp.live2d.framework.IPlatformManager;
import jp.live2d.framework.L2DBaseModel;
import jp.live2d.framework.L2DExpressionMotion;
import jp.live2d.framework.L2DEyeBlink;
import jp.live2d.framework.L2DModelMatrix;
import jp.live2d.framework.L2DPhysics;
import jp.live2d.framework.L2DPose;
import jp.live2d.framework.L2DStandardID;
import jp.live2d.framework.Live2DFramework;
import jp.live2d.motion.AMotion;
import jp.live2d.util.UtSystem;
import jp.live2d.utils.android.BufferUtil;
import jp.live2d.utils.android.FileManager;
import jp.live2d.utils.android.LoadUtil;
import jp.live2d.utils.android.ModelSetting;
import jp.live2d.utils.android.ModelSettingJson;
import jp.live2d.utils.android.OffscreenImage;
import jp.live2d.utils.android.SoundUtil;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;


/*
 * LAppModel �?�低レベル�?�Live2Dモデル定義クラス Live2DModelAndroid をラップ�?�
 * 簡便�?�扱�?��?��?�?�ユーティリティクラス�?��?�。
 *
 *
 * 機能一覧
 *  アイドリングモーション
 *  表情
 *  音声
 *  物�?�演算�?�よるアニメーション
 *  モーション�?�無�?��?��??�?�自動�?�目パ�?
 *  パーツ切り替�?��?�よる�?ーズ�?�変更
 *  当�?�り判定
 *  呼�?��?�アニメーション
 *  ドラッグ�?�よるアニメーション
 *  デ�?イス�?�傾�??�?�よるアニメーション
 *
 */
public class LAppModel extends L2DBaseModel
{
	//  ログ用タグ
	public String 					TAG = "LAppModel ";

	//  モデル関連
	private ModelSetting 			modelSetting = null;		//  モデルファイルやモーション�?�定義
	private String 					modelHomeDir;			//  モデルデータ�?��?�るディレクトリ

	//  音声
	private MediaPlayer 			voice;					//  音声

	//  デ�?ッグ用�?�当�?�り判定表示�?��?��?�?��?ッファ
	static FloatBuffer 				debugBufferVer = null ;
	static FloatBuffer 				debugBufferColor = null ;
	
	private boolean isDance = true;

	static Object lock = new Object() ;

	public LAppModel()
	{
		super();

		if(LAppDefine.DEBUG_LOG)
		{
			debugMode=true;
//			mainMotionManager.setMotionDebugMode(true);
		}
	}


	public void release()
	{
		if(live2DModel==null)return;
		live2DModel.deleteTextures();
	}

	public void toggleDance() {
		isDance = !isDance;
	}

	/*
	 * モデルを�?期化�?�る
	 * @param gl
	 * @throws Exception
	 */
	public void load(GL10 gl,String modelSettingPath) throws Exception
	{
		updating=true;
		initialized=false;

		modelHomeDir=modelSettingPath.substring(0,modelSettingPath.lastIndexOf("/") + 1);//live2d/model/xxx/
		PlatformManager pm=(PlatformManager)Live2DFramework.getPlatformManager();
		pm.setGL(gl);

		if(LAppDefine.DEBUG_LOG) Log.d(TAG, "json : "+modelSettingPath);

		try
		{
			InputStream in = new FileInputStream(modelSettingPath);//FileManager.open(modelSettingPath);
			modelSetting = new ModelSettingJson(in);
			in.close() ;
		}
		catch (IOException e)
		{
			e.printStackTrace();

			// ファイル�?�指定ミス。続行�?�?�。
			throw new Exception();
		}

		if(modelSetting.getModelName()!=null)
		{
			TAG+=modelSetting.getModelName();// ログ用
		}

		if(LAppDefine.DEBUG_LOG)Log.d(TAG, "Load model.");

		loadModelData(modelHomeDir+modelSetting.getModelFile());
		String[] texPaths=modelSetting.getTextureFiles();
		for (int i = 0; i < texPaths.length; i++) {
			loadTexture(i,modelHomeDir+texPaths[i]);
		}
		// 表情
		String[] expressionNames=modelSetting.getExpressionNames();
		String[] expressionPaths=modelSetting.getExpressionFiles();

		for (int i = 0; i < expressionPaths.length; i++) {
			loadExpression(expressionNames[i],modelHomeDir+ expressionPaths[i]);
		}

		// 物�?�演算
		loadPhysics( modelHomeDir+modelSetting.getPhysicsFile() );

		// パーツ切り替�?�
		loadPose(modelHomeDir+modelSetting.getPoseFile());

		// レイアウト
		HashMap<String, Float> layout = new HashMap<String,Float>();
		if (modelSetting.getLayout(layout) )
		{
			if (layout.get("width")!=null)modelMatrix.setWidth(layout.get("width"));
			if (layout.get("height")!=null)modelMatrix.setHeight(layout.get("height"));
			if (layout.get("x")!=null)modelMatrix.setX(layout.get("x"));
			if (layout.get("y")!=null)modelMatrix.setY(layout.get("y"));
			if (layout.get("center_x")!=null)modelMatrix.centerX(layout.get("center_x"));
			if (layout.get("center_y")!=null)modelMatrix.centerY(layout.get("center_y"));
			if (layout.get("top")!=null)modelMatrix.top(layout.get("top"));
			if (layout.get("bottom")!=null)modelMatrix.bottom(layout.get("bottom"));
			if (layout.get("left")!=null)modelMatrix.left(layout.get("left"));
			if (layout.get("right")!=null)modelMatrix.right(layout.get("right"));
		}

		// �?期パラメータ
		for(int i=0; i<modelSetting.getInitParamNum() ;i++)
		{
			String id = modelSetting.getInitParamID(i);
			float value = modelSetting.getInitParamValue(i);
			live2DModel.setParamFloat(id, value);
		}

		for(int i=0; i<modelSetting.getInitPartsVisibleNum() ;i++)
		{
			String id = modelSetting.getInitPartsVisibleID(i);
			float value = modelSetting.getInitPartsVisibleValue(i);
			live2DModel.setPartsOpacity(id, value);
		}

		// 自動目パ�?
		eyeBlink=new L2DEyeBlink();

		updating=false;// 更新状態�?�完了
		initialized=true;// �?期化完了
	}


	public void preloadMotionGroup( String name)
	{
		int len = modelSetting.getMotionNum(name);
		for (int i = 0; i < len; i++)
		{
			String fileName = modelSetting.getMotionFile(name, i);
			AMotion motion = loadMotion(fileName,modelHomeDir + fileName);
			motion.setFadeIn(modelSetting.getMotionFadeIn(name, i));
			motion.setFadeOut(modelSetting.getMotionFadeOut(name, i));
		}
	}


	public void update()
	{
		if(live2DModel == null)
		{
			if(LAppDefine.DEBUG_LOG)Log.d(TAG, "Failed to update.");
			return;
		}

		long timeMSec = UtSystem.getUserTimeMSec() - startTimeMSec  ;
		double timeSec = timeMSec / 1000.0 ;
		double t = timeSec * 2 * Math.PI  ;//2πt

		synchronized (lock)
		{
			// 待機モーション判定
			if(mainMotionManager.isFinished())
			{
				// モーション�?��?生�?��?��?�場�?��?待機モーション�?�中�?�らランダム�?��?生�?�る
				if (isDance) {
					startRandomMotion(LAppDefine.MOTION_GROUP_DANCE, LAppDefine.PRIORITY_IDLE);
				} else {
					startRandomMotion(LAppDefine.MOTION_GROUP_IDLE, LAppDefine.PRIORITY_IDLE);
				}
			}

			//-----------------------------------------------------------------
			live2DModel.loadParam();// �?回セーブ�?�れ�?�状態をロード

			boolean update = mainMotionManager.updateParam(live2DModel);// モーションを更新
			eyeBlink.updateParam(live2DModel);
			live2DModel.saveParam();// 状態を�?存
			//-----------------------------------------------------------------
		}

		if(expressionManager!=null)expressionManager.updateParam(live2DModel);//  表情�?�パラメータ更新（相対変化）


		// ドラッグ�?�よる変化
		// ドラッグ�?�よる顔�?��?��??�?�調整
		live2DModel.addToParamFloat( L2DStandardID.PARAM_ANGLE_X, dragX *  30 , 1 );// -30�?�ら30�?�値を加�?�る
		live2DModel.addToParamFloat( L2DStandardID.PARAM_ANGLE_Y, dragY *  30 , 1 );
		live2DModel.addToParamFloat( L2DStandardID.PARAM_ANGLE_Z, (dragX*dragY) * -30 , 1 );

		// ドラッグ�?�よる体�?��?��??�?�調整
		live2DModel.addToParamFloat( L2DStandardID.PARAM_BODY_ANGLE_X    , dragX * 10 , 1  );// -10�?�ら10�?�値を加�?�る

		// ドラッグ�?�よる目�?��?��??�?�調整
		live2DModel.addToParamFloat( L2DStandardID.PARAM_EYE_BALL_X, dragX  , 1 );// -1�?�ら1�?�値を加�?�る
		live2DModel.addToParamFloat( L2DStandardID.PARAM_EYE_BALL_Y, dragY  , 1 );

		// 呼�?��?��?�
		live2DModel.addToParamFloat(L2DStandardID.PARAM_ANGLE_X,	(float) (15 * Math.sin( t/ 6.5345 )) , 0.5f);
		live2DModel.addToParamFloat(L2DStandardID.PARAM_ANGLE_Y,	(float) ( 8 * Math.sin( t/ 3.5345 )) , 0.5f);
		live2DModel.addToParamFloat(L2DStandardID.PARAM_ANGLE_Z,	(float) (10 * Math.sin( t/ 5.5345 )) , 0.5f);
		live2DModel.addToParamFloat(L2DStandardID.PARAM_BODY_ANGLE_X,(float) ( 4 * Math.sin( t/15.5345 )) , 0.5f);
		live2DModel.setParamFloat(L2DStandardID.PARAM_BREATH,	(float) (0.5f + 0.5f * Math.sin( t/3.2345 )),1);

		// 加速度�?�よる変化
		live2DModel.addToParamFloat(L2DStandardID.PARAM_ANGLE_Z,	 90 * accelX  ,0.5f);

		if(physics!=null)physics.updateParam(live2DModel);// 物�?�演算�?�パラメータ更新

		// リップシンク�?�設定
		if(lipSync)
		{
			live2DModel.setParamFloat(L2DStandardID.PARAM_MOUTH_OPEN_Y, lipSyncValue ,0.8f);
		}

		// �?ーズ�?�設定
		if(pose!=null)pose.updateParam(live2DModel);

		live2DModel.update();
	}


	/*
	 * デ�?ッグ用当�?�り判定�?�表示
	 * @param gl
	 */
	private void drawHitArea(GL10 gl) {
		gl.glDisable( GL10.GL_TEXTURE_2D ) ;
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY) ;
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		gl.glPushMatrix() ;
		{
			gl.glMultMatrixf(modelMatrix.getArray(), 0) ;
			int len = modelSetting.getHitAreasNum();
			for (int i=0;i<len;i++)
			{
				String drawID=modelSetting.getHitAreaID(i);
				int drawIndex=live2DModel.getDrawDataIndex(drawID);
				if(drawIndex<0)continue;
				float[] points=live2DModel.getTransformedPoints(drawIndex);
				float left=live2DModel.getCanvasWidth();
				float right=0;
				float top=live2DModel.getCanvasHeight();
				float bottom=0;

				for (int j = 0; j < points.length; j=j+2)
				{
					float x = points[j];
					float y = points[j+1];
					if(x<left)left=x;	//  最�?�?�x
					if(x>right)right=x;	//  最大�?�x
					if(y<top)top=y;		//  最�?�?�y
					if(y>bottom)bottom=y;//  最大�?�y
				}

				float[] vertex={left,top,right,top,right,bottom,left,bottom,left,top};
				float r=1;
				float g=0;
				float b=0;
				float a=0.5f;
				int size=5;
				float color[] = {r,g,b,a,r,g,b,a,r,g,b,a,r,g,b,a,r,g,b,a};


				gl.glLineWidth( size );	// 　�??画サイズをsize�?��?�る
				gl.glVertexPointer( 2, GL10.GL_FLOAT, 0, BufferUtil.setupFloatBuffer( debugBufferVer,vertex));	// 　表示座標�?�セット
				gl.glColorPointer( 4, GL10.GL_FLOAT, 0, BufferUtil.setupFloatBuffer( debugBufferColor,color ) );	// 　カラー�?�セット
		    	gl.glDrawArrays( GL10.GL_LINE_STRIP, 0, 5 );	// 　pointNum�?��?��??画�?�る
			}
		}
		gl.glPopMatrix() ;
		gl.glEnable( GL10.GL_TEXTURE_2D ) ;
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY) ;
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
	}


	public void startRandomMotion(String name ,int priority)
	{
		int max=modelSetting.getMotionNum(name);
		int no=(int)(Math.random() * max);
		
		startMotion(name,no,priority);
	}


	/*
	 * モーション�?�開始。
	 * �?生�?��??る状態�?��?ェック�?��?��?�?��??�?��?�れ�?�何も�?��?��?�。
	 * �?生出�?�る場�?��?�自動�?�ファイルを読�?�込ん�?��?生。
	 * 音声付�??�?�ら�??れも�?生。
	 * フェードイン�?フェードアウト�?�情報�?��?�れ�?��?��?��?�設定。�?��?�れ�?��?期値。
	 */
	public void startMotion(String name, int no,int priority)
	{
		String motionName=modelSetting.getMotionFile(name, no);

		if( motionName==null || motionName.equals(""))
		{
			if(LAppDefine.DEBUG_LOG){Log.d(TAG, "Failed to motion.");}
			return;//
		}

		AMotion motion;

		// 新�?��?�モーション�?�priority�?��?�?生中�?�モーション�?予約済�?�モーション�?�priority�?�比較�?��?�
		// 予約�?�能�?��?�れ�?�（優先度�?�高�?�れ�?�）�?生を予約�?��?��?�。
		//
		// 予約�?��?�新モーション�?��?�?��?�フレーム�?��?�時�?生�?�れる�?��?も�?��??�?�音声�?�ロード等�?�必�?�?�場�?��?�
		// 以�?�?�フレーム�?��?生開始�?�れ�?��?�。
		synchronized (lock)
		{
			if (priority == LAppDefine.PRIORITY_FORCE) {
				mainMotionManager.setReservePriority(priority);
			}
			else if( ! mainMotionManager.reserveMotion(priority))
			{
				if(LAppDefine.DEBUG_LOG){Log.d(TAG, "Failed to motion.");}
				return ;
			}

			String motionPath=modelHomeDir + motionName;
			motion = loadMotion(null,motionPath);

			if(motion==null)
			{
				Log.w(TAG, "Failed to load motion.");
				mainMotionManager.setReservePriority(0);
				return;
			}
		}

		// フェードイン�?フェードアウト�?�設定
		motion.setFadeIn(modelSetting.getMotionFadeIn(name, no));
		motion.setFadeOut(modelSetting.getMotionFadeOut(name, no));

		if(LAppDefine.DEBUG_LOG)Log.d(TAG,"Start motion : "+motionName);

		// 音声�?�無�?�モーション�?��?�時�?生を開始�?��?��?�。
		if( modelSetting.getMotionSound(name, no) == null)
		{
			synchronized (lock)
			{
				mainMotionManager.startMotionPrio(motion,priority);
			}
		}
		// 音声�?��?�るモーション�?�音声�?�ロードを待�?��?�次�?�フレーム以�?�?��?生を開始�?��?��?�。
		else
		{
			String soundName=modelSetting.getMotionSound(name, no);
			String soundPath=modelHomeDir + soundName;
			MediaPlayer player=LoadUtil.loadAssetsSound( soundPath);

			if(LAppDefine.DEBUG_LOG)Log.d(TAG,"sound : "+soundName);
			startVoiceMotion( motion,player,priority);
		}
	}


	/*
	 * 音声�?�モーション�?��?�時�?生
	 * @param motion
	 * @param player
	 * @param priority 優先度。使用�?��?��?��?�ら0�?�良�?�。
	 */
	public void startVoiceMotion(final AMotion motion,final MediaPlayer player,final int priority)
	{
		// �?生準備完了時�?�イベント登録
		player.setOnPreparedListener(new OnPreparedListener()
		{
			public void onPrepared(MediaPlayer mp)
			{
				SoundUtil.release(voice);
				synchronized (lock)
				{
					mainMotionManager.startMotionPrio(motion,priority);
				}
				voice=player;
				voice.start();
			}
		});

		// �?生完了時�?�イベント登録
		player.setOnCompletionListener( new MediaPlayer.OnCompletionListener()
		{
			public void onCompletion(MediaPlayer mp)
			{
				SoundUtil.release(player);
			}
		});

		try
		{
			player.prepare();
		}
		catch (IllegalStateException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}


	/*
	 * 表情を設定�?�る
	 * @param motion
	 */
	public void setExpression(String name)
	{
		if( ! expressions.containsKey(name))return;// 無効�?�指定�?�ら�?��?�も�?��?��?�
		if(LAppDefine.DEBUG_LOG)Log.d(TAG,"Expression : "+name);
		AMotion motion=expressions.get(name);
		expressionManager.startMotion(motion,false);
	}


	/*
	 * 表情をランダム�?�切り替�?�る
	 */
	public void setRandomExpression()
	{
		int no=(int)(Math.random() * expressions.size());

		String[] keys = expressions.keySet().toArray(new String[expressions.size()]);

		setExpression(keys[no]);
	}


	public void draw(GL10 gl)
	{
		((Live2DModelAndroid) live2DModel).setGL(gl);// OpenGL�?�コンテキストをLive2Dモデル�?�設定

		alpha+=accAlpha;

		if (alpha<0)
		{
			alpha=0;
			accAlpha=0;
		}
		else if (alpha>1)
		{
			alpha=1;
			accAlpha=0;
		}

		if(alpha<0.001)return;

		if (alpha<0.999)
		{
			// �?��?明
			// オフスクリーン�?�モデルを�??画
			OffscreenImage.setOffscreen(gl);
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			gl.glPushMatrix() ;
			{
				gl.glMultMatrixf( modelMatrix.getArray(), 0) ;
				live2DModel.draw();
			}
			gl.glPopMatrix() ;

			// 実際�?�ウィンドウ�?��?��?明�?��??画
			OffscreenImage.setOnscreen(gl);
			gl.glPushMatrix() ;
			{
				gl.glLoadIdentity();
				OffscreenImage.drawDisplay(gl,alpha);
			}
			gl.glPopMatrix() ;
		}
		else
		{
			// 通常表示
			gl.glPushMatrix() ;
			{
				gl.glMultMatrixf(modelMatrix.getArray(), 0) ;
				live2DModel.draw();
			}
			gl.glPopMatrix() ;

			if(LAppDefine.DEBUG_DRAW_HIT_AREA )
			{
				// デ�?ッグ用当�?�り判定�?��??画
				 drawHitArea(gl);
			}
		}
	}


	/*
	 * 当�?�り判定�?��?�簡易テスト。
	 * 指定ID�?�頂点リスト�?�ら�??れらを�?�む最大�?�矩形を計算�?��?点�?��??�?��?��?��?�れる�?�判定
	 *
	 * @param id
	 * @param testX
	 * @param testY
	 * @return
	 */
	public boolean hitTest(String id,float testX,float testY)
	{
		if(alpha<1)return false;// �?明時�?�当�?�り判定�?��?�。
		if(modelSetting==null)return false;
		int len=modelSetting.getHitAreasNum();
		for (int i = 0; i < len; i++)
		{
			if( id.equals(modelSetting.getHitAreaName(i)) )
			{
				return hitTestSimple(modelSetting.getHitAreaID(i),testX,testY) ;
			}
		}
		return false;// 存在�?��?��?�場�?��?�false
	}


	public void feedIn()
	{
		alpha=0;
		accAlpha=0.1f;
	}
}