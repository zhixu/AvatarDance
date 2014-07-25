/**
 *
 *  You can modify and use this source freely
 *  only for the development of application related Live2D.
 *
 *  (c) Live2D Inc. All rights reserved.
 */
package jp.live2d.framework;

import jp.live2d.ALive2DModel ;
import jp.live2d.motion.AMotion ;
import jp.live2d.motion.MotionQueueManager ;

/*
 * L2DMotionManagerã�¯ã€�å„ªå…ˆåº¦(priority)ã‚’æŒ‡å®šã�—ã�¦ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ã�®å†�ç”Ÿã‚’ç®¡ç�†ã�™ã‚‹ã�Ÿã‚�ã�®ã‚¯ãƒ©ã‚¹ã�§ã�™ã€‚
 *
 * ä¸»ã�«è¦ªã‚¯ãƒ©ã‚¹ã�®MotionQueueManagerï¼ˆæ¨™æº–ã�®Live2Dãƒ©ã‚¤ãƒ–ãƒ©ãƒªï¼‰ã�«ä¸�è¶³ã�™ã‚‹ä»¥ä¸‹ã�®æ©Ÿèƒ½ã‚’è£œã�„ã�¾ã�™ã€‚
 *
 * ï¼‘ï¼Žå‰²ã‚Šè¾¼ã�¿ã�®åˆ¶å¾¡
 *
 * è¦ªã‚¯ãƒ©ã‚¹MotionQueueManagerã�§ã�¯ã€�startMotionã‚’å‘¼ã�³å‡ºã�™ã�¨æ–°ã�—ã�„ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ã�Œå‰²ã‚Šè¾¼ã�¿ã�§
 * ã‚¹ã‚¿ãƒ¼ãƒˆã�—æ—¢å­˜ã�®ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ã�¯çµ‚äº†ã�—ã�¾ã�™ã€‚ï¼ˆå‰�å¾Œã�®ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ã�¯æ»‘ã‚‰ã�‹ã�«ç¹‹ã�Œã‚Šã�¾ã�™ï¼‰
 *
 * L2DMotionManagerã�§ã�¯ã‚»ãƒªãƒ•ç­‰ã�®å‰²ã‚Šè¾¼ã�¾ã‚Œã�Ÿã��ã�ªã�„ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ã�®å ´å�ˆã�«ã€�å‰²ã‚Šè¾¼ã�¿ã‚’é˜²ã��ä»•çµ„ã�¿ã‚’
 * æ��ä¾›ã�—ã�¾ã�™ã€‚priority ã�Œå�Œã�˜å ´å�ˆã�¯ã€�å‰²ã‚Šè¾¼ã�¿ã�Œç™ºç”Ÿã�›ã�šã�«æ–°ã�—ã�„ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ã‚’ç„¡è¦–ã�—ã�¾ã�™ã€‚
 *
 *
 * ï¼’ï¼ŽéŸ³å£°ã�®ãƒ­ãƒ¼ãƒ‰ã�¨ã�®é€£æ�º
 *
 * ã‚¿ãƒƒãƒ—ã�ªã�©ã�®ã‚¤ãƒ™ãƒ³ãƒˆã�Œç™ºç”Ÿã�—ã�Ÿéš›ã�«ã€�éŸ³å£°ã�®ãƒ­ãƒ¼ãƒ‰ã�Œå®Œäº†ã�—ã�¦ã�Šã‚‰ã�šãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ã‚’å�³æ™‚é–‹å§‹ã�™ã‚‹ã�¨
 * ã‚ºãƒ¬ã�¦ã�—ã�¾ã�†å ´å�ˆã�Œã�‚ã‚Šã�¾ã�™ã€‚ã��ã�®ã‚ˆã�†ã�ªã‚±ãƒ¼ã‚¹ã�®ã�Ÿã‚�ã�«ã€�æ¬¡ãƒ•ãƒ¬ãƒ¼ãƒ ä»¥é™�ã�§å†�ç”Ÿã�™ã‚‹ã�“ã�¨ã‚’äºˆç´„ã�™ã‚‹
 * ä»•çµ„ã�¿ã‚’æ��ä¾›ã�—ã�¾ã�™ã€‚
 *
 *
 *
 */
public class L2DMotionManager extends MotionQueueManager{

	//  ãƒ¡ã‚¤ãƒ³ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ã�®å„ªå…ˆåº¦
	//  æ¨™æº–è¨­å®š 0:å†�ç”Ÿã�—ã�¦ã�ªã�„ 1:ã‚¢ã‚¤ãƒ‰ãƒªãƒ³ã‚°(å‰²ã‚Šè¾¼ã‚“ã�§è‰¯ã�„) 2:é€šå¸¸(åŸºæœ¬å‰²ã‚Šè¾¼ã�¿ã�ªã�—) 3:å¼·åˆ¶ã�§é–‹å§‹
	private int currentPriority;//  ç�¾åœ¨å†�ç”Ÿä¸­ã�®ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ã�®å„ªå…ˆåº¦
	private int reservePriority;//  å†�ç”Ÿäºˆå®šã�®ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ã�®å„ªå…ˆåº¦ã€‚å†�ç”Ÿä¸­ã�¯0ã�«ã�ªã‚‹ã€‚ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ãƒ•ã‚¡ã‚¤ãƒ«ã‚’åˆ¥ã‚¹ãƒ¬ãƒƒãƒ‰ã�§èª­ã�¿è¾¼ã‚€ã�¨ã��ã�®æ©Ÿèƒ½ã€‚


	/*
	 * å†�ç”Ÿä¸­ã�®ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ã�®å„ªå…ˆåº¦
	 * @return
	 */
	public int getCurrentPriority()
	{
		return currentPriority;
	}


	/*
	 * äºˆç´„ä¸­ã�®ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ã�®å„ªå…ˆåº¦
	 * @return
	 */
	public int getReservePriority()
	{
		return reservePriority;
	}


	/*
	 * æ¬¡ã�«å†�ç”Ÿã�—ã�Ÿã�„ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ã�®priorityã‚’æ¸¡ã�—ã�¦ã€�å†�ç”Ÿäºˆç´„ã�§ã��ã‚‹çŠ¶æ³�ã�‹åˆ¤æ–­ã�™ã‚‹
	 *
	 *
	 * @param priority
	 * @return
	 */
	public boolean reserveMotion(int priority)
	{
		if( reservePriority >= priority)
		{
			return false;// å†�ç”Ÿäºˆç´„ã�Œã�‚ã‚‹(åˆ¥ã‚¹ãƒ¬ãƒƒãƒ‰ã�§æº–å‚™ã�—ã�¦ã�„ã‚‹)
		}
		if( currentPriority >= priority ){
			return false;// å†�ç”Ÿä¸­ã�®ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ã�Œã�‚ã‚‹
		}
		reservePriority=priority;// ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³å†�ç”Ÿã�Œé�žå�ŒæœŸã�®å ´å�ˆã�¯å„ªå…ˆåº¦ã‚’å…ˆã�«è¨­å®šã�—ã�¦äºˆç´„ã�—ã�¦ã�Šã��
		return true;
	}
	
	public void resetPriority() {
		currentPriority = 0;
	}


	/*
	 * ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ã‚’äºˆç´„ã�™ã‚‹
	 * @param val
	 */
	public void setReservePriority(int val)
	{
		reservePriority = val;
	}


	@Override
	public boolean updateParam(ALive2DModel model)
	{
		boolean updated=super.updateParam(model);
		if(isFinished()){
			currentPriority=0;// å†�ç”Ÿä¸­ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ã�®å„ªå…ˆåº¦ã‚’è§£é™¤
		}
		return updated;
	}


	public int startMotionPrio(AMotion motion,int priority)
	{
		if(priority==reservePriority)
		{
			reservePriority=0;// äºˆç´„ã‚’è§£é™¤
		}
		currentPriority=priority;// å†�ç”Ÿä¸­ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ã�®å„ªå…ˆåº¦ã‚’è¨­å®š
		return super.startMotion(motion, false);//  ç¬¬äºŒå¼•æ•°ã�¯ãƒ¢ãƒ¼ã‚·ãƒ§ãƒ³ãƒ‡ãƒ¼ã‚¿ã‚’è‡ªå‹•ã�§å‰Šé™¤ã�™ã‚‹ã�‹ã�©ã�†ã�‹ã€‚Javaã�§ã�¯é–¢ä¿‚ã�ªã�—
	}
}
