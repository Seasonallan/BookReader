/*
 * ========================================================
 * ClassName:MusicView.java* 
 * Description:
 * Copyright (C) 
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
 * 2013-12-12     chendt          #00000       create
 */
package com.lectek.android.lereader.ui.basereader_leyue.expand_audio;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.lib.utils.DimensionsUtil;
import com.lectek.android.lereader.utils.CommonUtil;

public class MusicView extends ImageView {
	public static final String TAG = MusicView.class.getSimpleName();
	private Context mContext;
	private Bitmap cursorBitmap;
	private float cursorLeftX;
	private int screen_W,screen_H;
	private MusicInfo musicInfo;
	private boolean isShowCursor;
	public TouchMusicScoreCallBack musicViewCallBack;
	public MusicView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		cursorBitmap = CommonUtil.getBitmapFromResources(mContext, R.drawable.cursor);
	}

	public MusicView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		cursorBitmap = CommonUtil.getBitmapFromResources(mContext, R.drawable.cursor);
	}

	public MusicView(Context context) {
		super(context);
		mContext = context;
		cursorBitmap = CommonUtil.getBitmapFromResources(mContext, R.drawable.cursor);
	}
	
	boolean forLastInvalidate = false;
	boolean forFirstInvalidate = true;
	
	/**
	 * 初始化，刷新 view的数据。
	 * @param musicInfo
	 * @param isRefresh
	 */
	public void refreshView(MusicInfo musicInfo,boolean isInit) {
		this.musicInfo = musicInfo;
//		LogUtil.e("--position = "+musicInfo.curPosition+"--index = "+musicInfo.playIndex);
		if (musicInfo.isInView()) {
//			LogUtil.e("--isInView--");
			if (!isInit) {
				//滚动到该view不在listview中时，会出现内容被复用，导致调用滚动，但top值不是播放的view的
				if (forFirstInvalidate) {
					musicViewCallBack.scrollPartIndex(getTop());//需要判断当前view是否在listview显示的内容中，不在则滚动到未显示处
					forFirstInvalidate = false;
				}
			}
			isShowCursor = true;	
			forLastInvalidate = true;
			setCursorLeftX(musicInfo.getLeftBoundWidth()+musicInfo.getSale()*musicInfo.getValidZone());
		}else {
			isShowCursor = false;
			if (forLastInvalidate) {//跳过当前图片，刷新，绘制透明。
				forLastInvalidate = false;
				forFirstInvalidate = true;
				invalidate();
			}
		}
	}
	
	/** 可见屏幕宽高度 **/
	public void setScreen_WH(int screen_W,int screen_H) {
		this.screen_W = screen_W;
		this.screen_H = screen_H;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//这个值应该x3了,具体到时候再来计算。
		if(getBackground()!=null){
			int width = getBackground().getIntrinsicWidth();
			int height = getBackground().getIntrinsicHeight();
//			Log.e(Tag, "width = "+width+"---height---"+height);
			if (width >0 && height>0) {
				//处理认为宽>高
				if (screen_W>0 && screen_H >0) {
					setMeasuredDimension(screen_W, (screen_W*height)/screen_H);
					return;
				}else {
					super.onMeasure(width, height);
					return;
				}
			}
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (isShowCursor) {
			//获取游标图片
//			Log.d(Tag, "--drawBitmap--cursorLeftX = "+cursorLeftX);
			canvas.drawBitmap(cursorBitmap, cursorLeftX, 0, null);
			//FIXME:横屏位置不对。还有没有保存当前播放状态。
		}else {
			cleanView(canvas);
		}
	}
	
	/**清除游标*/
	private void cleanView(Canvas canvas) {
		Paint p = new Paint();
		p.setColor(mContext.getResources().getColor(R.color.transparent));
		canvas.drawPoint(0, 0, p);
	}
	
	public void setCursorLeftX(float cursorLeftX) {
		this.cursorLeftX = cursorLeftX;
		invalidate();
	}
	public enum STATE{
		ACTION_POINTER_DOWN,
		ACTION_MOVE,
		ACTION_DOWN,
		NONE
	}
	private STATE state = STATE.NONE;
	private float curTime;
	private float lastX,lastY;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		/** 处理单点、多点触摸 **/
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			Log.e(TAG, "Down");
			if (event.getRawX()>musicInfo.getLeftBoundWidth()&&event.getRawX()<musicInfo.getRightBoundWidth()) {
				curTime = (event.getRawX()-musicInfo.getLeftBoundWidth())/musicInfo.getValidZone()*musicInfo.getValidProgress()+musicInfo.getLeftProgress();
//				Log.e(Tag, "---event.getRawX()--"+event.getRawX()+",---event.getRawY()--"+event.getRawY()
//						+",curTime = "+curTime+"---event.getX()--"+event.getX());
				state = STATE.ACTION_DOWN;
			}else {
				state = STATE.NONE;
			}
			break;
		// 多点触摸
		case MotionEvent.ACTION_POINTER_DOWN:
			state = STATE.ACTION_POINTER_DOWN;
			break;

		case MotionEvent.ACTION_MOVE:
			Log.e(TAG, "---lastx="+lastX+",lastY="+lastY+",rawX="+event.getRawX()+",event.getRawY()="+event.getRawY());
			switch (state) {
			case ACTION_DOWN:
				if ((lastX!=0 && lastY!=0)&&(Math.abs((event.getRawX() - lastX)) > DimensionsUtil.dip2px(20, mContext) ||
						Math.abs((event.getRawY() - lastY)) > DimensionsUtil.dip2px(20, mContext))) {
					state = STATE.ACTION_MOVE;
				}else {
					state = STATE.ACTION_DOWN;
				}
				break;

			default:
				break;
			}
			lastX = event.getRawX();
			lastY = event.getRawY();
			break;
		case MotionEvent.ACTION_UP:
			Log.e(TAG, "---state="+state);
			lastX = 0;
			lastY = 0;
			switch (state) {
			case ACTION_DOWN:
				isShowCursor = true;	
				musicInfo.curPosition = curTime;
				setCursorLeftX(musicInfo.getLeftBoundWidth()+musicInfo.getSale()*musicInfo.getValidZone());
				musicViewCallBack.touchCallBack(curTime);
				break;
			default:
				break;
			}
			break;
		}
		return true;
	}
	
	public static interface TouchMusicScoreCallBack{
		/**点击乐谱调整播放进度*/
		public void touchCallBack(float changeTime);
		/**滚动到第几项的y坐标*/
		public void scrollPartIndex(int index);
	}
	
	public void setMusicViewCallBack(TouchMusicScoreCallBack musicViewCallBack) {
		this.musicViewCallBack = musicViewCallBack;
	}

	public MusicInfo getMusicInfo() {
		return musicInfo;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof MusicView) {
			MusicView view = (MusicView)o;
			if(musicInfo == null){
				return false;
			}else {
				if (view.musicInfo.getImg().equals(musicInfo.getImg())) {
					return true;
				}else {
					return false;
				}
			}
		}else {
			return super.equals(o);
		}
	}
	
	@Override
	public int hashCode() {
		 int result = 17;  //任意素数  
		 result = 31*result +musicInfo.getImg().hashCode(); 
		 result = 31*result +musicInfo.getTone().hashCode();  
		 return result; 
	}
}
