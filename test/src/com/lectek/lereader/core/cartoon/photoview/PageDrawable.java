package com.lectek.lereader.core.cartoon.photoview;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;

import com.lectek.lereader.core.text.PageManager;
import com.lectek.lereader.core.text.SettingParam;
import com.lectek.lereader.core.util.LogUtil;

/** 
 *  
 * @author laijp
 * @date 2014-3-10
 * @email 451360508@qq.com
 */
public class PageDrawable  extends Drawable{
	PageManager mPageManager;
	int[] pageIndex;
	SettingParam mSettingParam;
	
	public PageDrawable(PageManager pageManager, int[] pageIndex){
		this.mPageManager = pageManager;
		this.pageIndex = pageIndex;
		this.mSettingParam = mPageManager.getSettingParam(); 
	}
	
	@Override
	public void draw(Canvas canvas) { 
		if(pageIndex != null){
			mPageManager.requestDrawPage(canvas, pageIndex [0], pageIndex[1], pageIndex [0], pageIndex[1]);	
		}		
	} 

	@Override
	public int getIntrinsicHeight() {
		if(mSettingParam != null){
			return mSettingParam.getFullPageRect().height();
		}
		return super.getIntrinsicHeight();
	}
 
	@Override
	public void invalidateSelf() { 
		LogUtil.i("invalidateSelf");
		super.invalidateSelf();
	}

	@Override
	public int getIntrinsicWidth() {
		if(mSettingParam != null){
			return mSettingParam.getFullPageRect().width();
		}
		return super.getIntrinsicWidth(); 
	}

	@Override
	public int getOpacity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setAlpha(int alpha) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		// TODO Auto-generated method stub
		
	} 
	
} 
