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
 * LAppRenderer�?�モデル�??画�?��?�??�?��?��?�?�OpenGL命令を集約�?��?�クラス�?��?�。
 *
 */
public class LAppRenderer implements GLSurfaceView.Renderer {

	private final String TAG = "RENDERER";
	
	private LAppLive2DManager delegate;
	private DanceActivity activity;

	private SimpleImage bg;// 背景�?��??画
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
	 * OpenGL画�?��?�作�?時�?�呼�?�れるイベント。
	 */
	@Override
	public void onSurfaceCreated(GL10 context, EGLConfig arg1) {
		// 背景�?�作�?
		
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
	 * OpenGL画�?��?�変更時�?�呼�?�れるイベント。
	 * �?期化時�?�Activity�?開時�?�呼�?�れる。
	 */
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		delegate.onSurfaceChanged(gl,width,height);//Live2D Event
		
		GLES20.glViewport(0, 0, width, height);

		// OpenGL �?期化処�?�
		gl.glViewport(0, 0, width ,height);


		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();

		L2DViewMatrix viewMatrix = delegate.getViewMatrix();
		// glOrthof( X�?�左端, X�?��?�端, Y�?�下端, Y�?�上端, Z�?�手�?, Z�?�奥);
		gl.glOrthof(
				viewMatrix.getScreenLeft(),
				viewMatrix.getScreenRight(),
				viewMatrix.getScreenBottom(),
				viewMatrix.getScreenTop(),
				0.5f, -0.5f);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);// 背景色


		OffscreenImage.createFrameBuffer(gl, width ,height, 0);
	    return ;
	}


	/*
	 * �??画イベント。
	 */
	@Override
	public void onDrawFrame(GL10 gl) {

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// �?リゴン等を�??画�?��?��?�
		delegate.update(gl);

		// OpenGL 設定
		// 画�?��?��?�変�?�行列を�?�用
		gl.glMatrixMode(GL10.GL_MODELVIEW) ;
		gl.glLoadIdentity() ;
		
		 float[] mtx = new float[16];
         GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
         surface.updateTexImage();
         surface.getTransformMatrix(mtx); 

         directVideo.draw();

		/*
		// OpenGLをLive2D用�?�設定�?��?�る
		gl.glDisable(GL10.GL_DEPTH_TEST) ;// デプステストを行�?�?��?�
		gl.glDisable(GL10.GL_CULL_FACE) ;// カリングを行�?�?��?�
		gl.glEnable(GL10.GL_BLEND);// ブレンドを行�?�
		gl.glBlendFunc(GL10.GL_ONE , GL10.GL_ONE_MINUS_SRC_ALPHA );// ブレンド方法�?�指定

		gl.glEnable( GL10.GL_TEXTURE_2D ) ;
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY) ;
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY) ;

		// テクス�?ャ�?�クランプ指定
		gl.glTexParameterx(GL10.GL_TEXTURE_2D , GL10.GL_TEXTURE_WRAP_S , GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D , GL10.GL_TEXTURE_WRAP_T , GL10.GL_CLAMP_TO_EDGE);

		gl.glColor4f( 1 , 1, 1, 1  ) ;

		// 背景�?�モデル�?��??画
		gl.glPushMatrix() ;
		{
			// 画�?��?�拡大縮�?�?移動を設定
			L2DViewMatrix viewMatrix = delegate.getViewMatrix();
			gl.glMultMatrixf(viewMatrix.getArray(), 0) ;

			//  背景�?��??画
			if(bg!=null){
				gl.glPushMatrix() ;
				{
					float SCALE_X = 0.25f ;// デ�?イス�?�回転�?�よる�?�れ幅
					float SCALE_Y = 0.1f ;
					gl.glTranslatef( -SCALE_X  * accelX , SCALE_Y * accelY , 0 ) ;// �?�れ

					bg.draw(gl);
				}
				gl.glPopMatrix() ;
			}*/
			// キャラ�?��??画
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

			// 画�?�外を黒枠�?�覆�?�場�?�。
//			ImageClip.drawClippedRect(gl
//					,viewMatrix.getMaxLeft()-0.5f, viewMatrix.getMaxRight()+0.5f	, viewMatrix.getMaxBottom()	-0.5f, viewMatrix.getMaxTop()+0.5f 	//外枠
//					,viewMatrix.getMaxLeft(), viewMatrix.getMaxRight()	, viewMatrix.getMaxBottom()	, viewMatrix.getMaxTop() 			//�?��?�枠
//					, 0xFF000000) ;
	}


	public void setAccel(float x,float y,float z)
	{
		accelX=x;
		accelY=y;
	}


	/*
	 * 背景�?�設定
	 * @param context
	 */
	private void setupBackground(GL10 context) {
		try {
			
			InputStream in = FileManager.open(LAppDefine.BACK_IMAGE_NAME);
			
			
			bg=new SimpleImage(context,in);
			// �??画範囲。画�?��?�最大表示範囲�?��?��?�?�る
			bg.setDrawRect(
					LAppDefine.VIEW_LOGICAL_MAX_LEFT,
					LAppDefine.VIEW_LOGICAL_MAX_RIGHT,
					LAppDefine.VIEW_LOGICAL_MAX_BOTTOM,
					LAppDefine.VIEW_LOGICAL_MAX_TOP);

			// 画�?を使用�?�る範囲(uv)
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
