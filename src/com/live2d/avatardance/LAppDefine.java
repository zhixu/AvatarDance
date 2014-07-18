/**
 *
 *  You can modify and use this source freely
 *  only for the development of application related Live2D.
 *
 *  (c) Live2D Inc. All rights reserved.
 */
package com.live2d.avatardance;

import android.os.Environment;

/*
 * å®šæ•°
 */
public class LAppDefine
{
	// ãƒ‡ãƒ?ãƒƒã‚°ã€‚trueã?®ã?¨ã??ã?«ãƒ­ã‚°ã‚’è¡¨ç¤ºã?™ã‚‹ã€‚
	public static boolean DEBUG_LOG=true;
	public static boolean DEBUG_DRAW_HIT_AREA=false;


	//  å…¨ä½“ã?®è¨­å®š-------------------------------------------------------------------------------------------
	// ç”»é?¢
	public static final float VIEW_MAX_SCALE = 2f;
	public static final float VIEW_MIN_SCALE = 0.8f;

	public static final float VIEW_LOGICAL_LEFT = -1;
	public static final float VIEW_LOGICAL_RIGHT = 1;

	public static final float VIEW_LOGICAL_MAX_LEFT = -2;
	public static final float VIEW_LOGICAL_MAX_RIGHT = 2;
	public static final float VIEW_LOGICAL_MAX_BOTTOM = -2;
	public static final float VIEW_LOGICAL_MAX_TOP = 2;

	/*
	// ãƒ¢ãƒ‡ãƒ«ã?®å¾Œã‚?ã?«ã?‚ã‚‹èƒŒæ™¯ã?®ç”»åƒ?ãƒ•ã‚¡ã‚¤ãƒ«
	public static final String BACK_IMAGE_NAME = "image/back_class_normal.png" ;

	//  ãƒ¢ãƒ‡ãƒ«å®šç¾©----------------------------------------------------------------------------------------------------
	public static final String MODEL_HARU		= "live2d/haru/haru.model.json";
	public static final String MODEL_HARU_A		= "live2d/haru/haru_01.model.json";
	public static final String MODEL_HARU_B		= "live2d/haru/haru_02.model.json";
	public static final String MODEL_SHIZUKU	= "live2d/shizuku/shizuku.model.json";
	public static final String MODEL_WANKO 		= "live2d/wanko/wanko.model.json";
	*/
	public static final String BACK_IMAGE_NAME = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/clear.png";//"image/back_class_normal.png" ;

	//  ãƒ¢ãƒ‡ãƒ«å®šç¾©----------------------------------------------------------------------------------------------------
	public static final String MODEL_HARU		= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/miku.model.json";// "live2d/haru/haru.model.json";
	public static final String MODEL_HARU_A		= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/miku.model.json";//"live2d/haru/haru_01.model.json";
	public static final String MODEL_HARU_B		= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/miku.model.json";//"live2d/haru/haru_02.model.json";
	public static final String MODEL_SHIZUKU	= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/miku.model.json";//"live2d/shizuku/shizuku.model.json";
	public static final String MODEL_WANKO 		= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/miku.model.json";//"live2d/wanko/wanko.model.json";

	// å¤–éƒ¨å®šç¾©ãƒ•ã‚¡ã‚¤ãƒ«(json)ã?¨å?ˆã‚?ã?›ã‚‹
	static final String MOTION_GROUP_IDLE		="idle";		// ã‚¢ã‚¤ãƒ‰ãƒªãƒ³ã‚°
	static final String MOTION_GROUP_TAP_BODY	="tap_body";	// ä½“ã‚’ã‚¿ãƒƒãƒ—ã?—ã?Ÿã?¨ã??
	static final String MOTION_GROUP_FLICK_HEAD	="flick_head";	// é ­ã‚’æ’«ã?§ã?Ÿæ™‚
	static final String MOTION_GROUP_PINCH_IN	="pinch_in";	// æ‹¡å¤§ã?—ã?Ÿæ™‚
	static final String MOTION_GROUP_PINCH_OUT	="pinch_out";	// ç¸®å°?ã?—ã?Ÿæ™‚
	static final String MOTION_GROUP_SHAKE		="shake";		// ã‚·ã‚§ã‚¤ã‚¯
	static final String MOTION_GROUP_DANCE		="dance";
	
	// å¤–éƒ¨å®šç¾©ãƒ•ã‚¡ã‚¤ãƒ«(json)ã?¨å?ˆã‚?ã?›ã‚‹
	static final String HIT_AREA_HEAD		="head";
	static final String HIT_AREA_BODY		="body";
	
	
	// ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ã?®å„ªå…ˆåº¦å®šæ•°
	public static final int PRIORITY_NONE		= 0;
	public static final int PRIORITY_IDLE		= 1;
	public static final int PRIORITY_NORMAL		= 2;
	public static final int PRIORITY_FORCE		= 3;

}
