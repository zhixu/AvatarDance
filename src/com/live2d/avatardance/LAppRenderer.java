/**
 *  You can modify and use this source freely
 *  only for the development of application related Live2D.
 *
 *  (c) Live2D Inc. All rights reserved.
 */
package com.live2d.avatardance;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import jp.live2d.framework.L2DViewMatrix;
import jp.live2d.utils.android.FileManager;
import jp.live2d.utils.android.OffscreenImage;
import jp.live2d.utils.android.SimpleImage;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;


/*
 * LAppRendererã?¯ãƒ¢ãƒ‡ãƒ«æ??ç”»ã?¨ã€?ã??ã?®ã?Ÿã‚?ã?®OpenGLå‘½ä»¤ã‚’é›†ç´„ã?—ã?Ÿã‚¯ãƒ©ã‚¹ã?§ã?™ã€‚
 *
 */
public class LAppRenderer implements GLSurfaceView.Renderer {

	private final String TAG = "RENDERER";
	
	private LAppLive2DManager delegate;
	private DanceActivity activity;

	private SimpleImage bg;// èƒŒæ™¯ã?®æ??ç”»
	private SurfaceTexture surface;
	private DirectVideo directVideo;
	private int texture;

	private float accelX=0;
	private float accelY=0;


	public LAppRenderer( LAppLive2DManager live2DMgr, DanceActivity a  ){
		this.delegate = live2DMgr ;
		this.activity = a;
	}


	/*
	 * OpenGLç”»é?¢ã?®ä½œæˆ?æ™‚ã?«å‘¼ã?°ã‚Œã‚‹ã‚¤ãƒ™ãƒ³ãƒˆã€‚
	 */
	@Override
	public void onSurfaceCreated(GL10 context, EGLConfig arg1) {
		// èƒŒæ™¯ã?®ä½œæˆ?
		
		/*context.glDisable(GL10.GL_DITHER);
		
		context.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
	            GL10.GL_FASTEST);

		context.glClearColor(0,0,0,0);
		context.glEnable(GL10.GL_CULL_FACE);
		context.glShadeModel(GL10.GL_SMOOTH);
		context.glEnable(GL10.GL_DEPTH_TEST);*/
		
		//context.glEnable(GL10.GL_BLEND);
		//context.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		//setupBackground(context);
		
		/*
		float[] mtx = new float[16];
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        surface.updateTexImage();
        surface.getTransformMatrix(mtx); */

        //directVideo.draw();
		
		texture = createTexture();
		directVideo = new DirectVideo(texture);
		
		activity.startCamera(texture);
		
		
	}


	/*
	 * OpenGLç”»é?¢ã?®å¤‰æ›´æ™‚ã?«å‘¼ã?°ã‚Œã‚‹ã‚¤ãƒ™ãƒ³ãƒˆã€‚
	 * åˆ?æœŸåŒ–æ™‚ã?¨Activityå†?é–‹æ™‚ã?«å‘¼ã?°ã‚Œã‚‹ã€‚
	 */
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		delegate.onSurfaceChanged(gl,width,height);//Live2D Event
		
		GLES20.glViewport(0, 0, width, height);

		// OpenGL åˆ?æœŸåŒ–å‡¦ç?†
		gl.glViewport(0, 0, width ,height);


		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();

		L2DViewMatrix viewMatrix = delegate.getViewMatrix();
		// glOrthof( Xã?®å·¦ç«¯, Xã?®å?³ç«¯, Yã?®ä¸‹ç«¯, Yã?®ä¸Šç«¯, Zã?®æ‰‹å‰?, Zã?®å¥¥);
		gl.glOrthof(
				viewMatrix.getScreenLeft(),
				viewMatrix.getScreenRight(),
				viewMatrix.getScreenBottom(),
				viewMatrix.getScreenTop(),
				0.5f, -0.5f);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);// èƒŒæ™¯è‰²


		OffscreenImage.createFrameBuffer(gl, width ,height, 0);
	    return ;
	}


	/*
	 * æ??ç”»ã‚¤ãƒ™ãƒ³ãƒˆã€‚
	 */
	@Override
	public void onDrawFrame(GL10 gl) {

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// ãƒ?ãƒªã‚´ãƒ³ç­‰ã‚’æ??ç”»ã?—ã?¾ã?™
		delegate.update(gl);

		// OpenGL è¨­å®š
		// ç”»é?¢ã?¸ã?®å¤‰æ?›è¡Œåˆ—ã‚’é?©ç”¨
		gl.glMatrixMode(GL10.GL_MODELVIEW) ;
		gl.glLoadIdentity() ;
		
		 float[] mtx = new float[16];
         GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
         surface.updateTexImage();
         surface.getTransformMatrix(mtx); 

         directVideo.draw();

		/*
		// OpenGLã‚’Live2Dç”¨ã?®è¨­å®šã?«ã?™ã‚‹
		gl.glDisable(GL10.GL_DEPTH_TEST) ;// ãƒ‡ãƒ—ã‚¹ãƒ†ã‚¹ãƒˆã‚’è¡Œã‚?ã?ªã?„
		gl.glDisable(GL10.GL_CULL_FACE) ;// ã‚«ãƒªãƒ³ã‚°ã‚’è¡Œã‚?ã?ªã?„
		gl.glEnable(GL10.GL_BLEND);// ãƒ–ãƒ¬ãƒ³ãƒ‰ã‚’è¡Œã?†
		gl.glBlendFunc(GL10.GL_ONE , GL10.GL_ONE_MINUS_SRC_ALPHA );// ãƒ–ãƒ¬ãƒ³ãƒ‰æ–¹æ³•ã?®æŒ‡å®š

		gl.glEnable( GL10.GL_TEXTURE_2D ) ;
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY) ;
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY) ;

		// ãƒ†ã‚¯ã‚¹ãƒ?ãƒ£ã?®ã‚¯ãƒ©ãƒ³ãƒ—æŒ‡å®š
		gl.glTexParameterx(GL10.GL_TEXTURE_2D , GL10.GL_TEXTURE_WRAP_S , GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D , GL10.GL_TEXTURE_WRAP_T , GL10.GL_CLAMP_TO_EDGE);

		gl.glColor4f( 1 , 1, 1, 1  ) ;

		// èƒŒæ™¯ã?¨ãƒ¢ãƒ‡ãƒ«ã?®æ??ç”»
		gl.glPushMatrix() ;
		{
			// ç”»é?¢ã?®æ‹¡å¤§ç¸®å°?ã€?ç§»å‹•ã‚’è¨­å®š
			L2DViewMatrix viewMatrix = delegate.getViewMatrix();
			gl.glMultMatrixf(viewMatrix.getArray(), 0) ;

			//  èƒŒæ™¯ã?®æ??ç”»
			if(bg!=null){
				gl.glPushMatrix() ;
				{
					float SCALE_X = 0.25f ;// ãƒ‡ãƒ?ã‚¤ã‚¹ã?®å›žè»¢ã?«ã‚ˆã‚‹æ?ºã‚Œå¹…
					float SCALE_Y = 0.1f ;
					gl.glTranslatef( -SCALE_X  * accelX , SCALE_Y * accelY , 0 ) ;// æ?ºã‚Œ

					bg.draw(gl);
				}
				gl.glPopMatrix() ;
			}*/
			// ã‚­ãƒ£ãƒ©ã?®æ??ç”»
			for(int i=0;i<delegate.getModelNum();i++)
			{
				LAppModel model = delegate.getModel(i);
				if(model.isInitialized() && ! model.isUpdating())
				{
					model.update();
					model.draw(gl);
				}
			}
		//}
		gl.glPopMatrix() ;

			// ç”»é?¢å¤–ã‚’é»’æž ã?§è¦†ã?†å ´å?ˆã€‚
//			ImageClip.drawClippedRect(gl
//					,viewMatrix.getMaxLeft()-0.5f, viewMatrix.getMaxRight()+0.5f	, viewMatrix.getMaxBottom()	-0.5f, viewMatrix.getMaxTop()+0.5f 	//å¤–æž 
//					,viewMatrix.getMaxLeft(), viewMatrix.getMaxRight()	, viewMatrix.getMaxBottom()	, viewMatrix.getMaxTop() 			//ã?†ã?¡æž 
//					, 0xFF000000) ;
	}


	public void setAccel(float x,float y,float z)
	{
		accelX=x;
		accelY=y;
	}


	/*
	 * èƒŒæ™¯ã?®è¨­å®š
	 * @param context
	 */
	private void setupBackground(GL10 context) {
		try {
			
			InputStream in = FileManager.open(LAppDefine.BACK_IMAGE_NAME);
			
			
			bg=new SimpleImage(context,in);
			// æ??ç”»ç¯„å›²ã€‚ç”»é?¢ã?®æœ€å¤§è¡¨ç¤ºç¯„å›²ã?«å?ˆã‚?ã?›ã‚‹
			bg.setDrawRect(
					LAppDefine.VIEW_LOGICAL_MAX_LEFT,
					LAppDefine.VIEW_LOGICAL_MAX_RIGHT,
					LAppDefine.VIEW_LOGICAL_MAX_BOTTOM,
					LAppDefine.VIEW_LOGICAL_MAX_TOP);

			// ç”»åƒ?ã‚’ä½¿ç”¨ã?™ã‚‹ç¯„å›²(uv)
			bg.setUVRect(0.0f,1.0f,0.0f,1.0f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static private int createTexture()
    {
        int[] texture = new int[1];

        GLES20.glGenTextures(1,texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
             GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_LINEAR);        
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
             GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
     GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
             GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
     GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
             GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        return texture[0];
    }
	
	public void setSurface(SurfaceTexture s) {
		surface = s;
	}
	
	static public int loadShader(int type, String shaderCode)
    {
        int shader = GLES20.glCreateShader(type);

        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
}
