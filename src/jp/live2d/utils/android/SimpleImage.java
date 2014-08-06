/**
 *
 *  You can modify and use this source freely
 *  only for the development of application related Live2D.
 *
 *  (c) Live2D Inc. All rights reserved.
 */
package jp.live2d.utils.android;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.opengl.GLES11Ext;
import android.util.Log;


/*
 * èƒŒæ™¯ã�ªã�©ã�®ç”»åƒ�ã‚’è¡¨ç¤ºã�™ã‚‹ã€‚
 *
 */
public class SimpleImage {
	static FloatBuffer drawImageBufferUv = null ;
	static FloatBuffer drawImageBufferVer = null ;
	static ShortBuffer drawImageBufferIndex = null ;


	private float imageLeft;
	private float imageRight;
	private float imageTop;
	private float imageBottom;

	private float uvLeft;
	private float uvRight;
	private float uvTop;
	private float uvBottom;

	private int texture;
	
	public SimpleImage(int tex) {
		texture = tex;
		
		this.uvLeft=0;
		this.uvRight=1;
		this.uvBottom=0;
		this.uvTop=1;

		this.imageLeft=-1;
		this.imageRight=1;
		this.imageBottom=-1;
		this.imageTop=1;
	}

	public SimpleImage(GL10 gl,InputStream in) {
		Log.d("SIMPLEIMAGE", "wrong constructor called");
		try {
			texture=LoadUtil.loadTexture(gl, in, true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// åˆ�æœŸè¨­å®š
		this.uvLeft=0;
		this.uvRight=1;
		this.uvBottom=0;
		this.uvTop=1;

		this.imageLeft=-1;
		this.imageRight=1;
		this.imageBottom=-1;
		this.imageTop=1;
	}
	
	public SimpleImage(GL10 gl, Bitmap in) {
		Log.d("SIMPLEIMAGE", "wrong constructor called");
		
		try {
			texture=LoadUtil.loadTexture(gl, in, true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// åˆ�æœŸè¨­å®š
		this.uvLeft=0;
		this.uvRight=1;
		this.uvBottom=0;
		this.uvTop=1;

		this.imageLeft=-1;
		this.imageRight=1;
		this.imageBottom=-1;
		this.imageTop=1;
	}
	
	public void setTexture(GL10 gl, Bitmap in) {
		Log.d("SIMPLEIMAGE", "wrong constructor called");
		
		try {
			texture=LoadUtil.loadTexture(gl, in, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void draw(GL10 gl){
		float uv[] = { uvLeft ,uvBottom,uvRight  ,uvBottom, uvRight, uvTop, uvLeft,uvTop} ;
		float ver[] = { imageLeft , imageTop   , imageRight     , imageTop , imageRight      , imageBottom     , imageLeft , imageBottom } ;
		short index[] = {0,1,2 , 0,2,3} ;

		drawImageBufferUv = BufferUtil.setupFloatBuffer( drawImageBufferUv , uv ) ;
		drawImageBufferVer = BufferUtil.setupFloatBuffer( drawImageBufferVer , ver ) ;
		drawImageBufferIndex = BufferUtil.setupShortBuffer( drawImageBufferIndex , index ) ;

		gl.glTexCoordPointer( 2, GL10.GL_FLOAT , 0 , drawImageBufferUv ) ;
		gl.glVertexPointer( 2 , GL10.GL_FLOAT , 0 , drawImageBufferVer ) ;
		gl.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES , texture ) ;
		
		gl.glDrawElements( GL10.GL_TRIANGLES, 6 , GL10.GL_UNSIGNED_SHORT , drawImageBufferIndex ) ;
	}


	/*
	 * ãƒ†ã‚¯ã‚¹ãƒ�ãƒ£ã�®æ��ç”»å…ˆã�®åº§æ¨™ã‚’è¨­å®š(ãƒ‡ãƒ•ã‚©ãƒ«ãƒˆã�¯ 0,0,1,1 ã�«æ��ã�‹ã‚Œã‚‹)
	 *
	 * @param left
	 * @param right
	 * @param bottom
	 * @param top
	 */
	public void setDrawRect(float left, float right, float bottom, float top) {
		this.imageLeft=left;
		this.imageRight=right;
		this.imageBottom=bottom;
		this.imageTop=top;
	}


	/*
	 * ãƒ†ã‚¯ã‚¹ãƒ�ãƒ£ã�®ä½¿ç”¨ç¯„å›²ã‚’è¨­å®šï¼ˆãƒ†ã‚¯ã‚¹ãƒ�ãƒ£ã�¯0..1åº§æ¨™ï¼‰
	 * @param left
	 * @param right
	 * @param bottom
	 * @param top
	 */
	public void setUVRect(float left, float right, float bottom, float top) {
		this.uvLeft=left;
		this.uvRight=right;
		this.uvBottom=bottom;
		this.uvTop=top;
	}
}
