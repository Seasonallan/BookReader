package com.lectek.android.lereader.ui.basereader_leyue.anim;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.view.MotionEvent;
/**
 * 翻页动画控制者
 * @author lyw
 */
public abstract class PageAnimController{
	public static final int ANIM_TYPE_PAGE_TURNING = 0;
	public static final int ANIM_TYPE_TRANSLATION = 1;
	public static final int ANIM_TYPE_AUTO = 2;
	protected Context mContext;
	
	public static PageAnimController create(Context context,int type){
		PageAnimController pageAnimController = null;
		if(ANIM_TYPE_PAGE_TURNING == type){
			pageAnimController = new PageTurningAnimController(context);
		}else if(ANIM_TYPE_TRANSLATION == type){
			pageAnimController = new PageTurningAnimController(context);
			//pageAnimController = new HorTranslationAnimController(context); 
		}else if(ANIM_TYPE_AUTO == type){
			pageAnimController = new PageTurningAnimController(context);
			//pageAnimController = new AutoAnimController(context); 
		}
		return pageAnimController;
	}
	
	PageAnimController(Context context) {
		mContext = context;
	}
	
	public Resources getResources(){
		return mContext.getResources();
	}
	/**
	 * 派遣触屏事件
	 * @param event
	 */
	public abstract void dispatchTouchEvent(MotionEvent event,PageCarver pageCarver);
	/**
	 * 派遣绘制事件
	 * @param event
	 */
	public abstract boolean dispatchDrawPage(Canvas canvas,PageCarver pageCarver);
	/**
	 * 播放动画
	 * @param isNext TODO
	 * @param index 目标页的偏移量
	 */
	public abstract void startAnim(int fromIndex,int toIndex,boolean isNext, PageCarver pageCarver);
	/**
	 * 终止动画
	 */
	public abstract void stopAnim(PageCarver pageCarver);
	/**
	 * 动画是否已经停止,如果返回true就不会再调用dispatchDrawPage方法了
	 * @return
	 */
	public abstract boolean isAnimStop();
	/**
	 * 页绘制者
	 * @author lyw
	 */
	public static interface PageCarver{
		/**
		 * 绘制页内容
		 * @param index
		 */
		public void drawPage(Canvas canvas,int index);
		/**
		 * 请求翻到上一页
		 * @return
		 */
		public Integer requestPrePage();
		/**
		 * 请求翻到下一页
		 * @return
		 */
		public Integer requestNextPage();
		/**
		 * 刷新界面
		 */
		public void requestInvalidate();
		/**
		 * 获取当前页
		 */
		public int getCurrentPageIndex();
		/**
		 * 获取内容区宽度
		 * @return
		 */
		public int getContentWidth();
		/**
		 * 获取内容区高度
		 * @return
		 */
		public int getContentHeight();
		/**
		 * 获取屏幕宽度
		 * @return
		 */
		public int getScreenWidth();
		/**
		 * 获取屏幕高度
		 * @return
		 */
		public int getScreenHeight();
		/**
		 * 获取页背景颜色值
		 * @return
		 */
		public int getPageBackgroundColor();
		/**
		 * 开始动画的回调
		 * @param isCancel 是否是取消动画
		 */
		public void onStartAnim(boolean isCancel);
		/**
		 * 结束动画的回调
		 * @param isCancel 是否是取消动画
		 */
		public void onStopAnim(boolean isCancel);
	}
}