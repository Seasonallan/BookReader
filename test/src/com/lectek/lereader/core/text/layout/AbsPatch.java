package com.lectek.lereader.core.text.layout;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.SpanWatcher;
import android.text.Spannable;
import android.text.TextPaint;
import android.view.View;

import com.lectek.lereader.core.text.PatchParent;
import com.lectek.lereader.core.text.SettingParam;
import com.lectek.lereader.core.text.StyleText;

public abstract class AbsPatch implements SpanWatcher{
	/** 数据源*/
	protected StyleText mStyleText;
	/** 父框架*/
	protected PatchParent mParent;
	/** 设置参数*/
	protected SettingParam mSettingParam;
	/** 内容起始点从（代表该下标所对应的字符）*/
	protected int mStart;
	/** 内容结束点（代表该下标所对应的字符）*/
	protected int mEnd;
	
	AbsPatch(SettingParam settingParam){
		mSettingParam = settingParam;
	}
	/**
	 * 绑定区域
	 * @param parent
	 */
	public void bindPatchParent(PatchParent parent,StyleText styleText) {
		mStyleText = styleText;
		mStyleText.getDataSource().setSpan(false,this, getStart(), getEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		mParent = parent;
	}
	/**
	 * 解除绑定
	 */
	public void unBindPatchParent() {
		mParent = null;
		if(mStyleText != null){
			mStyleText.getDataSource().removeSpan(this);
			mStyleText = null;
		}
	}
	
	public boolean isBind(){
		return mParent != null && mStyleText != null;
	}
	/**
	 * 获取内容对应的字符开始下标
	 * @return the mStart （代表该下标所对应的字符）
	 */
	public final int getStart() {
		return mStart;
	}
	/**
	 * 获取内容对应的字符结束下标
	 * @return the mEnd （代表该下标所对应的字符）
	 */
	public final int getEnd() {
		return mEnd;
	}
	/**
	 * 设置内容对应的字符下标
	 * @param start （代表该下标所对应的字符）
	 * @param end （代表该下标所对应的字符）
	 */
	public void setContent(int start,int end){
		this.mStart = start;
		if(start <= end){
			this.mEnd = end;
		}
	}
	
	@Override
	public final void onSpanAdded(Spannable text, Object what, int start, int end) {
		if(mParent != null){
			mParent.invalidate(this);
		}
	}
	
	@Override
	public final void onSpanRemoved(Spannable text, Object what, int start, int end) {
		if(mParent != null){
			mParent.invalidate(this);
		}
	}
	
	@Override
	public final void onSpanChanged(Spannable text, Object what, int ostart,
			int oend, int nstart, int nend) {
		if(mParent != null){
			mParent.invalidate(this);
		}
	}
	
	protected TextPaint getSourcePaint() {
		return mSettingParam.getSourcePaint();
	}
	/**
	 * 绘制内容
	 * @param canvas
	 */
	public abstract void draw(Canvas canvas);
	/**
	 * 是的全屏
	 * @return
	 */
	public abstract boolean isFullScreen();
	/**
	 * 处理触屏事件
	 * @return
	 */
	public abstract boolean dispatchClick(View v,int x,int y);
	/**
	 * 内容是否全部准备好
	 * @return
	 */
	public abstract boolean isFinish();
	
	public abstract Rect getFullScreenContentRect();
	
	public abstract int findIndexByLocation(int x, int y, boolean isAccurate);

	public abstract Rect findRectByPosition(int position);
}
