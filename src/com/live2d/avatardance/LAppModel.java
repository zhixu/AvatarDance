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
 * LAppModel ã?¯ä½Žãƒ¬ãƒ™ãƒ«ã?®Live2Dãƒ¢ãƒ‡ãƒ«å®šç¾©ã‚¯ãƒ©ã‚¹ Live2DModelAndroid ã‚’ãƒ©ãƒƒãƒ—ã?—
 * ç°¡ä¾¿ã?«æ‰±ã?†ã?Ÿã‚?ã?®ãƒ¦ãƒ¼ãƒ†ã‚£ãƒªãƒ†ã‚£ã‚¯ãƒ©ã‚¹ã?§ã?™ã€‚
 *
 *
 * æ©Ÿèƒ½ä¸€è¦§
 *  ã‚¢ã‚¤ãƒ‰ãƒªãƒ³ã‚°ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³
 *  è¡¨æƒ…
 *  éŸ³å£°
 *  ç‰©ç?†æ¼”ç®—ã?«ã‚ˆã‚‹ã‚¢ãƒ‹ãƒ¡ãƒ¼ã‚·ãƒ§ãƒ³
 *  ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ã?Œç„¡ã?„ã?¨ã??ã?«è‡ªå‹•ã?§ç›®ãƒ‘ãƒ?
 *  ãƒ‘ãƒ¼ãƒ„åˆ‡ã‚Šæ›¿ã?ˆã?«ã‚ˆã‚‹ãƒ?ãƒ¼ã‚ºã?®å¤‰æ›´
 *  å½“ã?Ÿã‚Šåˆ¤å®š
 *  å‘¼å?¸ã?®ã‚¢ãƒ‹ãƒ¡ãƒ¼ã‚·ãƒ§ãƒ³
 *  ãƒ‰ãƒ©ãƒƒã‚°ã?«ã‚ˆã‚‹ã‚¢ãƒ‹ãƒ¡ãƒ¼ã‚·ãƒ§ãƒ³
 *  ãƒ‡ãƒ?ã‚¤ã‚¹ã?®å‚¾ã??ã?«ã‚ˆã‚‹ã‚¢ãƒ‹ãƒ¡ãƒ¼ã‚·ãƒ§ãƒ³
 *
 */
public class LAppModel extends L2DBaseModel
{
	//  ãƒ­ã‚°ç”¨ã‚¿ã‚°
	public String 					TAG = "LAppModel ";

	//  ãƒ¢ãƒ‡ãƒ«é–¢é€£
	private ModelSetting 			modelSetting = null;		//  ãƒ¢ãƒ‡ãƒ«ãƒ•ã‚¡ã‚¤ãƒ«ã‚„ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ã?®å®šç¾©
	private String 					modelHomeDir;			//  ãƒ¢ãƒ‡ãƒ«ãƒ‡ãƒ¼ã‚¿ã?®ã?‚ã‚‹ãƒ‡ã‚£ãƒ¬ã‚¯ãƒˆãƒª

	//  éŸ³å£°
	private MediaPlayer 			voice;					//  éŸ³å£°

	//  ãƒ‡ãƒ?ãƒƒã‚°ç”¨ã?®å½“ã?Ÿã‚Šåˆ¤å®šè¡¨ç¤ºã?®ã?Ÿã‚?ã?®ãƒ?ãƒƒãƒ•ã‚¡
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
	 * ãƒ¢ãƒ‡ãƒ«ã‚’åˆ?æœŸåŒ–ã?™ã‚‹
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

			// ãƒ•ã‚¡ã‚¤ãƒ«ã?®æŒ‡å®šãƒŸã‚¹ã€‚ç¶šè¡Œä¸?å?¯ã€‚
			throw new Exception();
		}

		if(modelSetting.getModelName()!=null)
		{
			TAG+=modelSetting.getModelName();// ãƒ­ã‚°ç”¨
		}

		if(LAppDefine.DEBUG_LOG)Log.d(TAG, "Load model.");

		loadModelData(modelHomeDir+modelSetting.getModelFile());
		String[] texPaths=modelSetting.getTextureFiles();
		for (int i = 0; i < texPaths.length; i++) {
			loadTexture(i,modelHomeDir+texPaths[i]);
		}
		// è¡¨æƒ…
		String[] expressionNames=modelSetting.getExpressionNames();
		String[] expressionPaths=modelSetting.getExpressionFiles();

		for (int i = 0; i < expressionPaths.length; i++) {
			loadExpression(expressionNames[i],modelHomeDir+ expressionPaths[i]);
		}

		// ç‰©ç?†æ¼”ç®—
		loadPhysics( modelHomeDir+modelSetting.getPhysicsFile() );

		// ãƒ‘ãƒ¼ãƒ„åˆ‡ã‚Šæ›¿ã?ˆ
		loadPose(modelHomeDir+modelSetting.getPoseFile());

		// ãƒ¬ã‚¤ã‚¢ã‚¦ãƒˆ
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

		// åˆ?æœŸãƒ‘ãƒ©ãƒ¡ãƒ¼ã‚¿
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

		// è‡ªå‹•ç›®ãƒ‘ãƒ?
		eyeBlink=new L2DEyeBlink();

		updating=false;// æ›´æ–°çŠ¶æ…‹ã?®å®Œäº†
		initialized=true;// åˆ?æœŸåŒ–å®Œäº†
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
		double t = timeSec * 2 * Math.PI  ;//2Ï€t

		synchronized (lock)
		{
			// å¾…æ©Ÿãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³åˆ¤å®š
			if(mainMotionManager.isFinished())
			{
				// ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ã?®å†?ç”Ÿã?Œã?ªã?„å ´å?ˆã€?å¾…æ©Ÿãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ã?®ä¸­ã?‹ã‚‰ãƒ©ãƒ³ãƒ€ãƒ ã?§å†?ç”Ÿã?™ã‚‹
				if (isDance) {
					startRandomMotion(LAppDefine.MOTION_GROUP_DANCE, LAppDefine.PRIORITY_IDLE);
				} else {
					startRandomMotion(LAppDefine.MOTION_GROUP_IDLE, LAppDefine.PRIORITY_IDLE);
				}
			}

			//-----------------------------------------------------------------
			live2DModel.loadParam();// å‰?å›žã‚»ãƒ¼ãƒ–ã?•ã‚Œã?ŸçŠ¶æ…‹ã‚’ãƒ­ãƒ¼ãƒ‰

			boolean update = mainMotionManager.updateParam(live2DModel);// ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ã‚’æ›´æ–°
			eyeBlink.updateParam(live2DModel);
			live2DModel.saveParam();// çŠ¶æ…‹ã‚’ä¿?å­˜
			//-----------------------------------------------------------------
		}

		if(expressionManager!=null)expressionManager.updateParam(live2DModel);//  è¡¨æƒ…ã?§ãƒ‘ãƒ©ãƒ¡ãƒ¼ã‚¿æ›´æ–°ï¼ˆç›¸å¯¾å¤‰åŒ–ï¼‰


		// ãƒ‰ãƒ©ãƒƒã‚°ã?«ã‚ˆã‚‹å¤‰åŒ–
		// ãƒ‰ãƒ©ãƒƒã‚°ã?«ã‚ˆã‚‹é¡”ã?®å?‘ã??ã?®èª¿æ•´
		live2DModel.addToParamFloat( L2DStandardID.PARAM_ANGLE_X, dragX *  30 , 1 );// -30ã?‹ã‚‰30ã?®å€¤ã‚’åŠ ã?ˆã‚‹
		live2DModel.addToParamFloat( L2DStandardID.PARAM_ANGLE_Y, dragY *  30 , 1 );
		live2DModel.addToParamFloat( L2DStandardID.PARAM_ANGLE_Z, (dragX*dragY) * -30 , 1 );

		// ãƒ‰ãƒ©ãƒƒã‚°ã?«ã‚ˆã‚‹ä½“ã?®å?‘ã??ã?®èª¿æ•´
		live2DModel.addToParamFloat( L2DStandardID.PARAM_BODY_ANGLE_X    , dragX * 10 , 1  );// -10ã?‹ã‚‰10ã?®å€¤ã‚’åŠ ã?ˆã‚‹

		// ãƒ‰ãƒ©ãƒƒã‚°ã?«ã‚ˆã‚‹ç›®ã?®å?‘ã??ã?®èª¿æ•´
		live2DModel.addToParamFloat( L2DStandardID.PARAM_EYE_BALL_X, dragX  , 1 );// -1ã?‹ã‚‰1ã?®å€¤ã‚’åŠ ã?ˆã‚‹
		live2DModel.addToParamFloat( L2DStandardID.PARAM_EYE_BALL_Y, dragY  , 1 );

		// å‘¼å?¸ã?ªã?©
		live2DModel.addToParamFloat(L2DStandardID.PARAM_ANGLE_X,	(float) (15 * Math.sin( t/ 6.5345 )) , 0.5f);
		live2DModel.addToParamFloat(L2DStandardID.PARAM_ANGLE_Y,	(float) ( 8 * Math.sin( t/ 3.5345 )) , 0.5f);
		live2DModel.addToParamFloat(L2DStandardID.PARAM_ANGLE_Z,	(float) (10 * Math.sin( t/ 5.5345 )) , 0.5f);
		live2DModel.addToParamFloat(L2DStandardID.PARAM_BODY_ANGLE_X,(float) ( 4 * Math.sin( t/15.5345 )) , 0.5f);
		live2DModel.setParamFloat(L2DStandardID.PARAM_BREATH,	(float) (0.5f + 0.5f * Math.sin( t/3.2345 )),1);

		// åŠ é€Ÿåº¦ã?«ã‚ˆã‚‹å¤‰åŒ–
		live2DModel.addToParamFloat(L2DStandardID.PARAM_ANGLE_Z,	 90 * accelX  ,0.5f);

		if(physics!=null)physics.updateParam(live2DModel);// ç‰©ç?†æ¼”ç®—ã?§ãƒ‘ãƒ©ãƒ¡ãƒ¼ã‚¿æ›´æ–°

		// ãƒªãƒƒãƒ—ã‚·ãƒ³ã‚¯ã?®è¨­å®š
		if(lipSync)
		{
			live2DModel.setParamFloat(L2DStandardID.PARAM_MOUTH_OPEN_Y, lipSyncValue ,0.8f);
		}

		// ãƒ?ãƒ¼ã‚ºã?®è¨­å®š
		if(pose!=null)pose.updateParam(live2DModel);

		live2DModel.update();
	}


	/*
	 * ãƒ‡ãƒ?ãƒƒã‚°ç”¨å½“ã?Ÿã‚Šåˆ¤å®šã?®è¡¨ç¤º
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
					if(x<left)left=x;	//  æœ€å°?ã?®x
					if(x>right)right=x;	//  æœ€å¤§ã?®x
					if(y<top)top=y;		//  æœ€å°?ã?®y
					if(y>bottom)bottom=y;//  æœ€å¤§ã?®y
				}

				float[] vertex={left,top,right,top,right,bottom,left,bottom,left,top};
				float r=1;
				float g=0;
				float b=0;
				float a=0.5f;
				int size=5;
				float color[] = {r,g,b,a,r,g,b,a,r,g,b,a,r,g,b,a,r,g,b,a};


				gl.glLineWidth( size );	// ã€€æ??ç”»ã‚µã‚¤ã‚ºã‚’sizeã?«ã?™ã‚‹
				gl.glVertexPointer( 2, GL10.GL_FLOAT, 0, BufferUtil.setupFloatBuffer( debugBufferVer,vertex));	// ã€€è¡¨ç¤ºåº§æ¨™ã?®ã‚»ãƒƒãƒˆ
				gl.glColorPointer( 4, GL10.GL_FLOAT, 0, BufferUtil.setupFloatBuffer( debugBufferColor,color ) );	// ã€€ã‚«ãƒ©ãƒ¼ã?®ã‚»ãƒƒãƒˆ
		    	gl.glDrawArrays( GL10.GL_LINE_STRIP, 0, 5 );	// ã€€pointNumã? ã?‘æ??ç”»ã?™ã‚‹
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
	 * ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ã?®é–‹å§‹ã€‚
	 * å†?ç”Ÿã?§ã??ã‚‹çŠ¶æ…‹ã?‹ãƒ?ã‚§ãƒƒã‚¯ã?—ã?¦ã€?ã?§ã??ã?ªã?‘ã‚Œã?°ä½•ã‚‚ã?—ã?ªã?„ã€‚
	 * å†?ç”Ÿå‡ºæ?¥ã‚‹å ´å?ˆã?¯è‡ªå‹•ã?§ãƒ•ã‚¡ã‚¤ãƒ«ã‚’èª­ã?¿è¾¼ã‚“ã?§å†?ç”Ÿã€‚
	 * éŸ³å£°ä»˜ã??ã?ªã‚‰ã??ã‚Œã‚‚å†?ç”Ÿã€‚
	 * ãƒ•ã‚§ãƒ¼ãƒ‰ã‚¤ãƒ³ã€?ãƒ•ã‚§ãƒ¼ãƒ‰ã‚¢ã‚¦ãƒˆã?®æƒ…å ±ã?Œã?‚ã‚Œã?°ã?“ã?“ã?§è¨­å®šã€‚ã?ªã?‘ã‚Œã?°åˆ?æœŸå€¤ã€‚
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

		// æ–°ã?—ã?„ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ã?®priorityã?¨ã€?å†?ç”Ÿä¸­ã?®ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ã€?äºˆç´„æ¸ˆã?¿ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ã?®priorityã?¨æ¯”è¼ƒã?—ã?¦
		// äºˆç´„å?¯èƒ½ã?§ã?‚ã‚Œã?°ï¼ˆå„ªå…ˆåº¦ã?Œé«˜ã?‘ã‚Œã?°ï¼‰å†?ç”Ÿã‚’äºˆç´„ã?—ã?¾ã?™ã€‚
		//
		// äºˆç´„ã?—ã?Ÿæ–°ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ã?¯ã€?ã?“ã?®ãƒ•ãƒ¬ãƒ¼ãƒ ã?§å?³æ™‚å†?ç”Ÿã?•ã‚Œã‚‹ã?‹ã€?ã‚‚ã?—ã??ã?¯éŸ³å£°ã?®ãƒ­ãƒ¼ãƒ‰ç­‰ã?Œå¿…è¦?ã?ªå ´å?ˆã?¯
		// ä»¥é™?ã?®ãƒ•ãƒ¬ãƒ¼ãƒ ã?§å†?ç”Ÿé–‹å§‹ã?•ã‚Œã?¾ã?™ã€‚
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

		// ãƒ•ã‚§ãƒ¼ãƒ‰ã‚¤ãƒ³ã€?ãƒ•ã‚§ãƒ¼ãƒ‰ã‚¢ã‚¦ãƒˆã?®è¨­å®š
		motion.setFadeIn(modelSetting.getMotionFadeIn(name, no));
		motion.setFadeOut(modelSetting.getMotionFadeOut(name, no));

		if(LAppDefine.DEBUG_LOG)Log.d(TAG,"Start motion : "+motionName);

		// éŸ³å£°ã?Œç„¡ã?„ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ã?¯å?³æ™‚å†?ç”Ÿã‚’é–‹å§‹ã?—ã?¾ã?™ã€‚
		if( modelSetting.getMotionSound(name, no) == null)
		{
			synchronized (lock)
			{
				mainMotionManager.startMotionPrio(motion,priority);
			}
		}
		// éŸ³å£°ã?Œã?‚ã‚‹ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ã?¯éŸ³å£°ã?®ãƒ­ãƒ¼ãƒ‰ã‚’å¾…ã?£ã?¦æ¬¡ã?®ãƒ•ãƒ¬ãƒ¼ãƒ ä»¥é™?ã?«å†?ç”Ÿã‚’é–‹å§‹ã?—ã?¾ã?™ã€‚
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
	 * éŸ³å£°ã?¨ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ã?®å?Œæ™‚å†?ç”Ÿ
	 * @param motion
	 * @param player
	 * @param priority å„ªå…ˆåº¦ã€‚ä½¿ç”¨ã?—ã?ªã?„ã?ªã‚‰0ã?§è‰¯ã?„ã€‚
	 */
	public void startVoiceMotion(final AMotion motion,final MediaPlayer player,final int priority)
	{
		// å†?ç”Ÿæº–å‚™å®Œäº†æ™‚ã?®ã‚¤ãƒ™ãƒ³ãƒˆç™»éŒ²
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

		// å†?ç”Ÿå®Œäº†æ™‚ã?®ã‚¤ãƒ™ãƒ³ãƒˆç™»éŒ²
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
	 * è¡¨æƒ…ã‚’è¨­å®šã?™ã‚‹
	 * @param motion
	 */
	public void setExpression(String name)
	{
		if( ! expressions.containsKey(name))return;// ç„¡åŠ¹ã?ªæŒ‡å®šã?ªã‚‰ã?ªã?«ã‚‚ã?—ã?ªã?„
		if(LAppDefine.DEBUG_LOG)Log.d(TAG,"Expression : "+name);
		AMotion motion=expressions.get(name);
		expressionManager.startMotion(motion,false);
	}


	/*
	 * è¡¨æƒ…ã‚’ãƒ©ãƒ³ãƒ€ãƒ ã?«åˆ‡ã‚Šæ›¿ã?ˆã‚‹
	 */
	public void setRandomExpression()
	{
		int no=(int)(Math.random() * expressions.size());

		String[] keys = expressions.keySet().toArray(new String[expressions.size()]);

		setExpression(keys[no]);
	}


	public void draw(GL10 gl)
	{
		((Live2DModelAndroid) live2DModel).setGL(gl);// OpenGLã?®ã‚³ãƒ³ãƒ†ã‚­ã‚¹ãƒˆã‚’Live2Dãƒ¢ãƒ‡ãƒ«ã?«è¨­å®š

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
			// å?Šé€?æ˜Ž
			// ã‚ªãƒ•ã‚¹ã‚¯ãƒªãƒ¼ãƒ³ã?«ãƒ¢ãƒ‡ãƒ«ã‚’æ??ç”»
			OffscreenImage.setOffscreen(gl);
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			gl.glPushMatrix() ;
			{
				gl.glMultMatrixf( modelMatrix.getArray(), 0) ;
				live2DModel.draw();
			}
			gl.glPopMatrix() ;

			// å®Ÿéš›ã?®ã‚¦ã‚£ãƒ³ãƒ‰ã‚¦ã?«å?Šé€?æ˜Žã?§æ??ç”»
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
			// é€šå¸¸è¡¨ç¤º
			gl.glPushMatrix() ;
			{
				gl.glMultMatrixf(modelMatrix.getArray(), 0) ;
				live2DModel.draw();
			}
			gl.glPopMatrix() ;

			if(LAppDefine.DEBUG_DRAW_HIT_AREA )
			{
				// ãƒ‡ãƒ?ãƒƒã‚°ç”¨å½“ã?Ÿã‚Šåˆ¤å®šã?®æ??ç”»
				 drawHitArea(gl);
			}
		}
	}


	/*
	 * å½“ã?Ÿã‚Šåˆ¤å®šã?¨ã?®ç°¡æ˜“ãƒ†ã‚¹ãƒˆã€‚
	 * æŒ‡å®šIDã?®é ‚ç‚¹ãƒªã‚¹ãƒˆã?‹ã‚‰ã??ã‚Œã‚‰ã‚’å?«ã‚€æœ€å¤§ã?®çŸ©å½¢ã‚’è¨ˆç®—ã?—ã€?ç‚¹ã?Œã??ã?“ã?«å?«ã?¾ã‚Œã‚‹ã?‹åˆ¤å®š
	 *
	 * @param id
	 * @param testX
	 * @param testY
	 * @return
	 */
	public boolean hitTest(String id,float testX,float testY)
	{
		if(alpha<1)return false;// é€?æ˜Žæ™‚ã?¯å½“ã?Ÿã‚Šåˆ¤å®šã?ªã?—ã€‚
		if(modelSetting==null)return false;
		int len=modelSetting.getHitAreasNum();
		for (int i = 0; i < len; i++)
		{
			if( id.equals(modelSetting.getHitAreaName(i)) )
			{
				return hitTestSimple(modelSetting.getHitAreaID(i),testX,testY) ;
			}
		}
		return false;// å­˜åœ¨ã?—ã?ªã?„å ´å?ˆã?¯false
	}


	public void feedIn()
	{
		alpha=0;
		accAlpha=0.1f;
	}
}