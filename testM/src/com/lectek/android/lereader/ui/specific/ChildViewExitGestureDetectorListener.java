/*
 * ========================================================
 * ClassName:ApiDemosApplication* 
 * Description:主界面
 * Copyright (C) 2012 中国电信协同通信运营支撑中心
 * ========================================================
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *===================================================================*
 * Revision History
 *
 * Modification                    Tracking
 * Date         Author          Number       Description of changes
 *____________________________________________________________________
 * 
 * 2013-1-10     chendt          #00000       create
 */
package com.lectek.android.lereader.ui.specific;


import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

/**
 * @author chendt <p>
 * 关于onSingleTapConfirmed和onSingleTapUp的一点区别： 
 * 二者的区别是：onSingleTapUp，只要手抬起就会执行，而对于onSingleTapConfirmed来说，如果双击的话，则onSingleTapConfirmed不会执行。<p>
 * 
 * onFling()是甩，这个甩的动作是在一个MotionEvent.ACTION_UP(手指抬起)发生时执行，而onScroll()，只要手指移动就会执行。
 * 他不会执行MotionEvent.ACTION_UP。
 * onFling通常用来实现翻页效果，而onScroll通常用来实现放大缩小和移动。
 */
public class ChildViewExitGestureDetectorListener extends SimpleOnGestureListener{
	private ChildViewSwitcher mSwitcher;
	
	public ChildViewExitGestureDetectorListener(ChildViewSwitcher mSwitcher){
		this.mSwitcher = mSwitcher;
	}
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
//		LogUtil.e( "onSingleTapUp--一次点击后up动作");
		return super.onSingleTapUp(e);
	}
	/**
	 * 长按，触摸屏按下后既不抬起也不移动，过一段时间后触发 
	 * */
	@Override
	public void onLongPress(MotionEvent e) {
//		LogUtil.e( "onLongPress--长按事件");
		super.onLongPress(e);
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
//		LogUtil.e( "onScroll--在屏幕上拖动事件");
		
		return super.onScroll(e1, e2, distanceX, distanceY);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
//		LogUtil.e( "onFling--滑动手势事件");
//		LogUtil.e( "velocityX="+velocityX+",velocityY="+velocityY);
		if (velocityX<-1200) {//与distanceX相反； velocityX<0代表屏幕内容向右滑动（手势向左）
			mSwitcher.flingHandle(false);
		}else if(velocityX>1200 && Math.abs(velocityY)<500){//加入y避免列表上下滑动与手势冲突
			mSwitcher.flingHandle(true);
		}
		return super.onFling(e1, e2, velocityX, velocityY);
	}
	/**
	 * down事件发生而move或者up还没发生前触发该事件；
	 * 短按，触摸屏按下后片刻后抬起，会触发这个手势，如果迅速抬起则不会 
	 * */
	@Override
	public void onShowPress(MotionEvent e) {
//		LogUtil.e( "onShowPress--(短按)down后，还未move和up");
		super.onShowPress(e);
	}

	@Override
	public boolean onDown(MotionEvent e) {
//		LogUtil.e( "onDown---一次点击Down动作");
		return super.onDown(e);
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
//		LogUtil.e( "onDoubleTap--双击事件");
		return super.onDoubleTap(e);
	}
	/**
	 * 双击间隔中还发生其他的动作。通知DoubleTap手势中的事件，包含down、up和move事件
	 * （这里指的是在双击之间发生的事件，例如在同一个地方双击会产生DoubleTap手势，而在DoubleTap手势里面还会发生down和up事件，这两个事件由该函数通知）；
	 * */
	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
//		LogUtil.e( "onDoubleTapEvent--双击之间发生的事件");
		return super.onDoubleTapEvent(e);
	}
	/**
	 * 单击事件。用来判定该次点击是SingleTap而不是DoubleTap，如果连续点击两次就是DoubleTap手势，
	 * 如果只点击一次，系统等待一段时间后没有收到第二次点击则判定该次点击为SingleTap而不是DoubleTap，然后触发SingleTapConfirmed事件。
	 * */
	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
//		LogUtil.e( "onSingleTapConfirmed--确认为单击事件时触发");
		return super.onSingleTapConfirmed(e);
	}
	
	/**
	 * @ClassName: ChildViewSwitcher
	 * @Description: 子界面左右滑动监听
	 * @author chendt
	 * @date 2013-3-26 上午12:13:30
	 *
	*/
	public static interface ChildViewSwitcher{
		
		/**
		 * true 代表向左滑动(手势向右)，false 代表是向右
		 * @param isFling
		 */
		public void flingHandle(boolean isLeft);
	}
}
