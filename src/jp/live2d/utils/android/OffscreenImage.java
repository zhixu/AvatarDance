/**
 *
 *  You can modify and use this source freely
 *  only for the development of application related Live2D.
 *
 *  (c) Live2D Inc. All rights reserved.
 */
package jp.live2d.utils.android;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

import android.graphics.Bitmap;
import android.opengl.GLUtils;

/*
 * ã‚ªãƒ•ã‚¹ã‚¯ãƒªãƒ¼ãƒ³ã�®ãƒ�ãƒƒãƒ•ã‚¡ã�«æ��ç”»ã�™ã‚‹ã€‚
 *
 *
 */
public class OffscreenImage {

	private static int offscreenFrameBuffer;
	private static int offscreenTexture = -1;
	private static int defaultFrameBuffer;
	private static int viewportWidth;
	private static int viewportHeight;

	private static FloatBuffer uvBuffer ;
	private static FloatBuffer vertexBuffer ;
	private static ShortBuffer indexBuffer ;

	public static final int OFFSCREEN_SIZE = 512;// ã‚ªãƒ•ã‚¹ã‚¯ãƒªãƒ¼ãƒ³ã�«æ��ç”»ã�—ã�Ÿã�¨ã��ã�®è§£åƒ�åº¦ã€‚ç¸¦æ¨ªå�Œã�˜ã€‚

	/*
	 * ã‚ªãƒ•ã‚¹ã‚¯ãƒªãƒ¼ãƒ³ã‚’è§£é™¤ã�™ã‚‹ã€‚
	 * @param gl
	 */
	public static void setOnscreen(  GL10 gl ){
		 GL11ExtensionPack gl11ep = (GL11ExtensionPack) gl;

	    //  ã‚ªãƒ³ã‚¹ã‚¯ãƒªãƒ¼ãƒ³
	    gl11ep.glBindFramebufferOES( GL11ExtensionPack.GL_FRAMEBUFFER_OES, 	defaultFrameBuffer );
	    gl.glViewport(0, 0, viewportWidth, viewportHeight);
	}


	/*
	 * ã‚ªãƒ•ã‚¹ã‚¯ãƒªãƒ¼ãƒ³ã�«ã�™ã‚‹ã€‚
	 * @param gl
	 */
	public static void setOffscreen(  GL10 gl ){
		 GL11ExtensionPack gl11ep = (GL11ExtensionPack) gl;

	    //  ã‚ªãƒ•ã‚¹ã‚¯ãƒªãƒ¼ãƒ³
	    gl11ep.glBindFramebufferOES( GL11ExtensionPack.GL_FRAMEBUFFER_OES, 	offscreenFrameBuffer );
	    gl.glViewport(0, 0, OFFSCREEN_SIZE, OFFSCREEN_SIZE);
	}


	/*
	 * ã‚ªãƒ•ã‚¹ã‚¯ãƒªãƒ¼ãƒ³ã�®ãƒ�ãƒƒãƒ•ã‚¡ã‚’ä½œæˆ�ã�™ã‚‹ã€‚
	 * @param gl
	 * @param width
	 * @param height
	 * @param fbo ãƒ‡ãƒ•ã‚©ãƒ«ãƒˆã�®ãƒ•ãƒ¬ãƒ¼ãƒ ãƒ�ãƒƒãƒ•ã‚¡
	 */
	public static void createFrameBuffer( GL10 gl, int width, int height ,int fbo)
	{
		viewportWidth=width;
		viewportHeight=height;
		defaultFrameBuffer=fbo;

		 //  ãƒ†ã‚¯ã‚¹ãƒ�ãƒ£ç”¨ãƒ¡ãƒ¢ãƒªã�®ç¢ºä¿�
		if( offscreenTexture > 0 )
		{
			releaseFrameBuffer(gl);
		}
        int[] buffers = new int[1];//  ãƒ†ã‚¯ã‚¹ãƒ�ãƒ£ç®¡ç�†IDã�®é…�åˆ—

        gl.glGenTextures(1, buffers, 0);//  1æžšåˆ†ã�®ãƒ¡ãƒ¢ãƒªã‚’ç¢ºä¿�
        gl.glBindTexture(GL10.GL_TEXTURE_2D, buffers[0]);//  1æžšç›®ã‚’ä½¿ã�†æŒ‡å®š

        offscreenTexture = buffers[0];

        Bitmap bitmap = Bitmap.createBitmap(OFFSCREEN_SIZE, OFFSCREEN_SIZE, Bitmap.Config.ARGB_8888);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);//  ç¸®å°�æ™‚ã�®ãƒ•ã‚£ãƒ«ã‚¿
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);//  æ‹¡å¤§æ™‚ã�®ãƒ•ã‚£ãƒ«ã‚¿

		GL11ExtensionPack gl11ep = (GL11ExtensionPack) gl;

	    int[] framebuffers = new int[1];

	    gl11ep.glGenFramebuffersOES( 1, framebuffers, 0 );

	    gl11ep.glBindFramebufferOES( GL11ExtensionPack.GL_FRAMEBUFFER_OES, framebuffers[0] );

		gl11ep.glFramebufferTexture2DOES( GL11ExtensionPack.GL_FRAMEBUFFER_OES, GL11ExtensionPack.GL_COLOR_ATTACHMENT0_OES, GL10.GL_TEXTURE_2D, offscreenTexture, 0 );

		int status = gl11ep.glCheckFramebufferStatusOES( GL11ExtensionPack.GL_FRAMEBUFFER_OES );
	    if( status != GL11ExtensionPack.GL_FRAMEBUFFER_COMPLETE_OES )
	    {
			throw new RuntimeException("Framebuffer is not complete: " +
			Integer.toHexString(status));
		}

	    offscreenFrameBuffer= framebuffers[0];

	    gl11ep.glBindFramebufferOES( GL11ExtensionPack.GL_FRAMEBUFFER_OES, 	defaultFrameBuffer );

		uvBuffer=BufferUtil.createFloatBuffer( 4*2 );
		uvBuffer.put(0);
		uvBuffer.put(0);

		uvBuffer.put(1);
		uvBuffer.put(0);

		uvBuffer.put(0);
		uvBuffer.put(1);

		uvBuffer.put(1);
		uvBuffer.put(1);
		uvBuffer.position(0);

		vertexBuffer=BufferUtil.createFloatBuffer( 4*2 );
		vertexBuffer.put(-1);
		vertexBuffer.put((float)-height/width);

		vertexBuffer.put(1);
		vertexBuffer.put((float)-height/width);

		vertexBuffer.put(-1);
		vertexBuffer.put((float)height/width);

		vertexBuffer.put(1);
		vertexBuffer.put((float)height/width);
		vertexBuffer.position(0);

		indexBuffer=ShortBuffer.allocate(3*2);
		indexBuffer.put((short) 0);
		indexBuffer.put((short) 1);
		indexBuffer.put((short) 2);

		indexBuffer.put((short) 2);
		indexBuffer.put((short) 1);
		indexBuffer.put((short) 3);
		indexBuffer.position(0);
	}


	public static void releaseFrameBuffer(GL10 gl)
	{
		int textures[] = { offscreenTexture };
		gl.glDeleteTextures(1, textures ,0);
		offscreenTexture=-1;
	}


	/*
	 * ã‚ªãƒ•ã‚¹ã‚¯ãƒªãƒ¼ãƒ³ã�®ãƒ�ãƒƒãƒ•ã‚¡ã‚’æ��ç”»ã�™ã‚‹ã€‚
	 * @param gl
	 * @param opacity
	 */
	public static void drawDisplay(GL10 gl  , float opacity  ){

		gl.glEnable( GL10.GL_TEXTURE_2D ) ;
		gl.glEnable( GL10.GL_BLEND);

		gl.glBlendFunc(GL10.GL_ONE , GL10.GL_ONE_MINUS_SRC_ALPHA );//

		gl.glColor4f( opacity , opacity, opacity, opacity  ) ;

		gl.glBindTexture(GL10.GL_TEXTURE_2D , offscreenTexture ) ;

		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY) ;
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY) ;


		gl.glTexCoordPointer( 2, GL10.GL_FLOAT , 0 , uvBuffer ) ;


		gl.glVertexPointer( 2 , GL10.GL_FLOAT , 0 , vertexBuffer ) ;

		gl.glDrawElements( GL10.GL_TRIANGLES, 6 , GL10.GL_UNSIGNED_SHORT , indexBuffer ) ;

	}
}
