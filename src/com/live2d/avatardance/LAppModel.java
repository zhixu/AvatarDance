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
 * LAppModel ã?¯ä½ã¬ãã«ã?®Live2Dã¢ãã«å®ç¾©ã¯ã©ã¹ Live2DModelAndroid ãã©ããã?
 * ç°¡ä¾¿ã?«æ±ã?ã?ã?ã?®ã¦ã¼ãã£ãªãã£ã¯ã©ã¹ã?§ã?ã
 *
 *
 * æ©è½ä¸è¦§
 *  ã¢ã¤ããªã³ã°ã¢ã¼ã·ã§ã³
 *  è¡¨æ
 *  é³å£°
 *  ç©ç?æ¼ç®ã?«ããã¢ãã¡ã¼ã·ã§ã³
 *  ã¢ã¼ã·ã§ã³ã?ç¡ã?ã?¨ã??ã?«èªåã?§ç®ãã?
 *  ãã¼ãåãæ¿ã?ã?«ããã?ã¼ãºã?®å¤æ´
 *  å½ã?ãå¤å®
 *  å¼å?¸ã?®ã¢ãã¡ã¼ã·ã§ã³
 *  ãã©ãã°ã?«ããã¢ãã¡ã¼ã·ã§ã³
 *  ãã?ã¤ã¹ã?®å¾ã??ã?«ããã¢ãã¡ã¼ã·ã§ã³
 *
 */
public class LAppModel extends L2DBaseModel
{
	//  ã­ã°ç¨ã¿ã°
	public String 					TAG = "LAppModel ";

	//  ã¢ãã«é¢é£
	private ModelSetting 			modelSetting = null;		//  ã¢ãã«ãã¡ã¤ã«ãã¢ã¼ã·ã§ã³ã?®å®ç¾©
	private String 					modelHomeDir;			//  ã¢ãã«ãã¼ã¿ã?®ã?ããã£ã¬ã¯ããª

	//  é³å£°
	private MediaPlayer 			voice;					//  é³å£°

	//  ãã?ãã°ç¨ã?®å½ã?ãå¤å®è¡¨ç¤ºã?®ã?ã?ã?®ã?ããã¡
	static FloatBuffer 				debugBufferVer = null ;
	static FloatBuffer 				debugBufferColor = null ;
	
	private DanceActivity activity;
	private boolean isDance = false;
	
	private int danceNumber = 0;

	static Object lock = new Object() ;

	public LAppModel()
	{
		super();

		//activity = act;
		
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
	
	public void danceStart() {
		isDance = true;
		mainMotionManager.resetPriority();
		mainMotionManager.setReservePriority(0);
		
		int max=modelSetting.getMotionNum(LAppDefine.MOTION_GROUP_DANCE);
		danceNumber=(int)(Math.random() * max);
		
		startMotion(LAppDefine.MOTION_GROUP_DANCE, danceNumber, LAppDefine.PRIORITY_IDLE);
	}
	
	public void switchDance() {
		mainMotionManager.resetPriority();
		mainMotionManager.setReservePriority(0);
		
		int max=modelSetting.getMotionNum(LAppDefine.MOTION_GROUP_DANCE);
		if (danceNumber < max-1) {
			danceNumber++;
		} else {
			danceNumber = 0;
		}
	}

	public void danceStop() {
		isDance = false;
		mainMotionManager.resetPriority();
		mainMotionManager.setReservePriority(0);
		startRandomMotion(LAppDefine.MOTION_GROUP_IDLE, LAppDefine.PRIORITY_IDLE);
	}
	/*
	 * ã¢ãã«ãå?æåã?ã
	 * @param gl
	 * @throws Exception
	 */
	public void load(GL10 gl,String modelSettingPath, boolean isDefaultModel) throws Exception
	{
		updating=true;
		initialized=false;

		modelHomeDir=modelSettingPath.substring(0,modelSettingPath.lastIndexOf("/") + 1);//live2d/model/xxx/
		PlatformManager pm=(PlatformManager)Live2DFramework.getPlatformManager();
		pm.setGL(gl);

		if(LAppDefine.DEBUG_LOG) Log.d(TAG, "json : "+modelSettingPath);

		try
		{
			
			if (isDefaultModel) {
				
				FileManager.setAsset(true);
				InputStream in = FileManager.open_asset(modelSettingPath);
				modelSetting = new ModelSettingJson(in);
				in.close();
			} else {
				FileManager.setAsset(false);
				InputStream in = FileManager.open_background(modelSettingPath, false);
				modelSetting = new ModelSettingJson(in);
				in.close();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();

			// ãã¡ã¤ã«ã?®æå®ãã¹ãç¶è¡ä¸?å?¯ã
			throw new Exception();
		}

		if(modelSetting.getModelName()!=null)
		{
			TAG+=modelSetting.getModelName();// ã­ã°ç¨
		}

		if(LAppDefine.DEBUG_LOG)Log.d(TAG, "Load model.");

		loadModelData(modelHomeDir+modelSetting.getModelFile());
		String[] texPaths=modelSetting.getTextureFiles();
		for (int i = 0; i < texPaths.length; i++) {
			loadTexture(i,modelHomeDir+texPaths[i]);
		}
		// è¡¨æ
		String[] expressionNames=modelSetting.getExpressionNames();
		String[] expressionPaths=modelSetting.getExpressionFiles();

		for (int i = 0; i < expressionPaths.length; i++) {
			loadExpression(expressionNames[i],modelHomeDir+ expressionPaths[i]);
		}

		// ç©ç?æ¼ç®
		loadPhysics( modelHomeDir+modelSetting.getPhysicsFile() );

		// ãã¼ãåãæ¿ã?
		loadPose(modelHomeDir+modelSetting.getPoseFile());

		// ã¬ã¤ã¢ã¦ã
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

		// å?æãã©ã¡ã¼ã¿
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

		// èªåç®ãã?
		eyeBlink=new L2DEyeBlink();

		updating=false;// æ´æ°ç¶æã?®å®äº
		initialized=true;// å?æåå®äº
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
		double t = timeSec * 2 * Math.PI  ;//2Ït

		synchronized (lock)
		{
			// å¾æ©ã¢ã¼ã·ã§ã³å¤å®
			if(mainMotionManager.isFinished())
			{
				// ã¢ã¼ã·ã§ã³ã?®å?çã?ã?ªã?å ´å?ã?å¾æ©ã¢ã¼ã·ã§ã³ã?®ä¸­ã?ãã©ã³ãã ã?§å?çã?ã
				if (isDance) {
					startMotion(LAppDefine.MOTION_GROUP_DANCE, danceNumber, LAppDefine.PRIORITY_IDLE);
				} else {
					startRandomMotion(LAppDefine.MOTION_GROUP_IDLE, LAppDefine.PRIORITY_IDLE);
				}
			}

			//-----------------------------------------------------------------
			live2DModel.loadParam();// å?åã»ã¼ãã?ãã?ç¶æãã­ã¼ã

			boolean update = mainMotionManager.updateParam(live2DModel);// ã¢ã¼ã·ã§ã³ãæ´æ°
			eyeBlink.updateParam(live2DModel);
			live2DModel.saveParam();// ç¶æãä¿?å­
			//-----------------------------------------------------------------
		}

		if(expressionManager!=null)expressionManager.updateParam(live2DModel);//  è¡¨æã?§ãã©ã¡ã¼ã¿æ´æ°ï¼ç¸å¯¾å¤åï¼


		// ãã©ãã°ã?«ããå¤å
		// ãã©ãã°ã?«ããé¡ã?®å?ã??ã?®èª¿æ´
		live2DModel.addToParamFloat( L2DStandardID.PARAM_ANGLE_X, dragX *  30 , 1 );// -30ã?ã30ã?®å¤ãå ã?ã
		live2DModel.addToParamFloat( L2DStandardID.PARAM_ANGLE_Y, dragY *  30 , 1 );
		live2DModel.addToParamFloat( L2DStandardID.PARAM_ANGLE_Z, (dragX*dragY) * -30 , 1 );

		// ãã©ãã°ã?«ããä½ã?®å?ã??ã?®èª¿æ´
		live2DModel.addToParamFloat( L2DStandardID.PARAM_BODY_ANGLE_X    , dragX * 10 , 1  );// -10ã?ã10ã?®å¤ãå ã?ã

		// ãã©ãã°ã?«ããç®ã?®å?ã??ã?®èª¿æ´
		live2DModel.addToParamFloat( L2DStandardID.PARAM_EYE_BALL_X, dragX  , 1 );// -1ã?ã1ã?®å¤ãå ã?ã
		live2DModel.addToParamFloat( L2DStandardID.PARAM_EYE_BALL_Y, dragY  , 1 );

		// å¼å?¸ã?ªã?©
		live2DModel.addToParamFloat(L2DStandardID.PARAM_ANGLE_X,	(float) (15 * Math.sin( t/ 6.5345 )) , 0.5f);
		live2DModel.addToParamFloat(L2DStandardID.PARAM_ANGLE_Y,	(float) ( 8 * Math.sin( t/ 3.5345 )) , 0.5f);
		live2DModel.addToParamFloat(L2DStandardID.PARAM_ANGLE_Z,	(float) (10 * Math.sin( t/ 5.5345 )) , 0.5f);
		live2DModel.addToParamFloat(L2DStandardID.PARAM_BODY_ANGLE_X,(float) ( 4 * Math.sin( t/15.5345 )) , 0.5f);
		live2DModel.setParamFloat(L2DStandardID.PARAM_BREATH,	(float) (0.5f + 0.5f * Math.sin( t/3.2345 )),1);

		// å éåº¦ã?«ããå¤å
		live2DModel.addToParamFloat(L2DStandardID.PARAM_ANGLE_Z,	 90 * accelX  ,0.5f);

		if(physics!=null)physics.updateParam(live2DModel);// ç©ç?æ¼ç®ã?§ãã©ã¡ã¼ã¿æ´æ°

		// ãªããã·ã³ã¯ã?®è¨­å®
		if(lipSync)
		{
			live2DModel.setParamFloat(L2DStandardID.PARAM_MOUTH_OPEN_Y, lipSyncValue ,0.8f);
		}

		// ã?ã¼ãºã?®è¨­å®
		if(pose!=null)pose.updateParam(live2DModel);

		live2DModel.update();
	}


	/*
	 * ãã?ãã°ç¨å½ã?ãå¤å®ã?®è¡¨ç¤º
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
					if(x<left)left=x;	//  æå°?ã?®x
					if(x>right)right=x;	//  æå¤§ã?®x
					if(y<top)top=y;		//  æå°?ã?®y
					if(y>bottom)bottom=y;//  æå¤§ã?®y
				}

				float[] vertex={left,top,right,top,right,bottom,left,bottom,left,top};
				float r=1;
				float g=0;
				float b=0;
				float a=0.5f;
				int size=5;
				float color[] = {r,g,b,a,r,g,b,a,r,g,b,a,r,g,b,a,r,g,b,a};


				gl.glLineWidth( size );	// ãæ??ç»ãµã¤ãºãsizeã?«ã?ã
				gl.glVertexPointer( 2, GL10.GL_FLOAT, 0, BufferUtil.setupFloatBuffer( debugBufferVer,vertex));	// ãè¡¨ç¤ºåº§æ¨ã?®ã»ãã
				gl.glColorPointer( 4, GL10.GL_FLOAT, 0, BufferUtil.setupFloatBuffer( debugBufferColor,color ) );	// ãã«ã©ã¼ã?®ã»ãã
		    	gl.glDrawArrays( GL10.GL_LINE_STRIP, 0, 5 );	// ãpointNumã? ã?æ??ç»ã?ã
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
	 * ã¢ã¼ã·ã§ã³ã?®éå§ã
	 * å?çã?§ã??ãç¶æã?ã?ã§ãã¯ã?ã?¦ã?ã?§ã??ã?ªã?ãã?°ä½ãã?ã?ªã?ã
	 * å?çåºæ?¥ãå ´å?ã?¯èªåã?§ãã¡ã¤ã«ãèª­ã?¿è¾¼ãã?§å?çã
	 * é³å£°ä»ã??ã?ªãã??ããå?çã
	 * ãã§ã¼ãã¤ã³ã?ãã§ã¼ãã¢ã¦ãã?®æå ±ã?ã?ãã?°ã?ã?ã?§è¨­å®ãã?ªã?ãã?°å?æå¤ã
	 */
	public void startMotion(String name, int no,int priority)
	{
		String motionName=modelSetting.getMotionFile(name, no);

		if( motionName==null || motionName.equals(""))
		{
			if(LAppDefine.DEBUG_LOG){Log.d(TAG, "Failed to motion.");}
			return;
		}

		AMotion motion;

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

		// ãã§ã¼ãã¤ã³ã?ãã§ã¼ãã¢ã¦ãã?®è¨­å®
		motion.setFadeIn(modelSetting.getMotionFadeIn(name, no));
		motion.setFadeOut(modelSetting.getMotionFadeOut(name, no));

		if(LAppDefine.DEBUG_LOG)Log.d(TAG,"Start motion : "+motionName);

		// é³å£°ã?ç¡ã?ã¢ã¼ã·ã§ã³ã?¯å?³æå?çãéå§ã?ã?¾ã?ã
		if( modelSetting.getMotionSound(name, no) == null)
		{
			synchronized (lock)
			{
				mainMotionManager.startMotionPrio(motion,priority);
			}
		}
		// é³å£°ã?ã?ãã¢ã¼ã·ã§ã³ã?¯é³å£°ã?®ã­ã¼ããå¾ã?£ã?¦æ¬¡ã?®ãã¬ã¼ã ä»¥é?ã?«å?çãéå§ã?ã?¾ã?ã
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
	 * é³å£°ã?¨ã¢ã¼ã·ã§ã³ã?®å?æå?ç
	 * @param motion
	 * @param player
	 * @param priority åªååº¦ãä½¿ç¨ã?ã?ªã?ã?ªã0ã?§è¯ã?ã
	 */
	public void startVoiceMotion(final AMotion motion,final MediaPlayer player,final int priority)
	{
		// å?çæºåå®äºæã?®ã¤ãã³ãç»é²
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

		// å?çå®äºæã?®ã¤ãã³ãç»é²
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
	 * è¡¨æãè¨­å®ã?ã
	 * @param motion
	 */
	public void setExpression(String name)
	{
		if( ! expressions.containsKey(name))return;// ç¡å¹ã?ªæå®ã?ªãã?ªã?«ãã?ã?ªã?
		if(LAppDefine.DEBUG_LOG)Log.d(TAG,"Expression : "+name);
		AMotion motion=expressions.get(name);
		expressionManager.startMotion(motion,false);
	}


	/*
	 * è¡¨æãã©ã³ãã ã?«åãæ¿ã?ã
	 */
	public void setRandomExpression()
	{
		int no=(int)(Math.random() * expressions.size());

		String[] keys = expressions.keySet().toArray(new String[expressions.size()]);

		setExpression(keys[no]);
	}


	public void draw(GL10 gl)
	{
		((Live2DModelAndroid) live2DModel).setGL(gl);// OpenGLã?®ã³ã³ãã­ã¹ããLive2Dã¢ãã«ã?«è¨­å®

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
			// å?é?æ
			// ãªãã¹ã¯ãªã¼ã³ã?«ã¢ãã«ãæ??ç»
			OffscreenImage.setOffscreen(gl);
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			gl.glPushMatrix() ;
			{
				gl.glMultMatrixf( modelMatrix.getArray(), 0) ;
				live2DModel.draw();
			}
			gl.glPopMatrix() ;

			// å®éã?®ã¦ã£ã³ãã¦ã?«å?é?æã?§æ??ç»
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
			// éå¸¸è¡¨ç¤º
			gl.glPushMatrix() ;
			{
				gl.glMultMatrixf(modelMatrix.getArray(), 0) ;
				live2DModel.draw();
			}
			gl.glPopMatrix() ;

			if(LAppDefine.DEBUG_DRAW_HIT_AREA )
			{
				// ãã?ãã°ç¨å½ã?ãå¤å®ã?®æ??ç»
				 drawHitArea(gl);
			}
		}
	}


	/*
	 * å½ã?ãå¤å®ã?¨ã?®ç°¡æãã¹ãã
	 * æå®IDã?®é ç¹ãªã¹ãã?ãã??ãããå?«ãæå¤§ã?®ç©å½¢ãè¨ç®ã?ã?ç¹ã?ã??ã?ã?«å?«ã?¾ããã?å¤å®
	 *
	 * @param id
	 * @param testX
	 * @param testY
	 * @return
	 */
	public boolean hitTest(String id,float testX,float testY)
	{
		if(alpha<1)return false;// é?ææã?¯å½ã?ãå¤å®ã?ªã?ã
		if(modelSetting==null)return false;
		int len=modelSetting.getHitAreasNum();
		for (int i = 0; i < len; i++)
		{
			if( id.equals(modelSetting.getHitAreaName(i)) )
			{
				return hitTestSimple(modelSetting.getHitAreaID(i),testX,testY) ;
			}
		}
		return false;// å­å¨ã?ã?ªã?å ´å?ã?¯false
	}


	public void feedIn()
	{
		alpha=0;
		accAlpha=0.1f;
	}
}