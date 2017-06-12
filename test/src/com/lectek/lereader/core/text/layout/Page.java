package com.lectek.lereader.core.text.layout;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.style.BackgroundColorSpan;
import android.view.View;

import com.lectek.lereader.core.text.Constant;
import com.lectek.lereader.core.text.DrawCallback;
import com.lectek.lereader.core.text.LinkedList;
import com.lectek.lereader.core.text.PatchParent;
import com.lectek.lereader.core.text.SettingParam;
import com.lectek.lereader.core.text.StyleText;
import com.lectek.lereader.core.text.style.ImgPanelBGDrawableSpan;
import com.lectek.lereader.core.util.LogUtil;

public class Page extends AbsPatch implements PatchParent{
	private static final String TAG = Page.class.getSimpleName();
	/** 区域*/
	private LinkedList<Patch> mPatchs;
	/** 区域*/
//	private SoftReference<LinkedList<Patch>> mCachePatchs;
	/** 非浮动区域*/
	private int mNotFloatPatchBottom;
	/** 块级区域*/
	private LinkedHashMap<Integer,Panle> mPanleLines;
	/** 浮动区域*/
	private LinkedList<Patch> mFloatLines;
	/** 可用区域*/
	private LinkedList<Rect> mEnabledRect;
	/** 页内容区域*/
	private Rect mPageRect;
	
	private int mPageIndex;
	
	private TextPaint mTextPaint;

	private DrawCallback mDrawCallback;
	
	private int mLastPanleBottom;
 
	public Page(SettingParam settingParam,int pageIndex) {
		super(settingParam);
		mPageIndex = pageIndex;
		mPageRect = settingParam.getPageRect();
		mPanleLines = new LinkedHashMap<Integer,Panle>();
		initLayoutData();
		mNotFloatPatchBottom = mPageRect.top;
		mLastPanleBottom = mPageRect.top;
		setContent(-1, -1);
//		mCachePatchs = new SoftReference<LinkedList<Patch>>(null);
	}
	
	private void findPanleTop(StyleText panleStyleText,Patch patch,int top){
		if(panleStyleText != null){
			boolean isCover = panleStyleText.isCover();
			Panle panle = mPanleLines.get(panleStyleText.hashCode());
			boolean isNeedPut = panle == null;
			if(isNeedPut){
				panle = new Panle(panleStyleText.getId());
				panle.setContent(patch.getStart(), patch.getEnd());
				if(isCover){
					panle.setLeft(mSettingParam.getFullPageRect().left);
					panle.setRight(mSettingParam.getFullPageRect().right);
				}else{
					if(patch.getLayoutType() == Constant.LAYOUT_TYPE_NOTHING){
						panle.setLeft(mPageRect.left);
						panle.setRight(mPageRect.right);
					}else{
						panle.setLeft(patch.getLeft());
						panle.setRight(patch.getRight());
					}
				}
				panle.setTop(top);
				panle.setBottom(patch.mBottom);
			}else{
				panle.setContent(panle.getStart(), patch.getEnd());
				panle.setBottom(patch.mBottom);
				top = panle.getTop();
			}
			findPanleTop(panleStyleText.getParentPanleStyleText(),patch,top);
			if(isNeedPut){
				mPanleLines.put(panleStyleText.hashCode(), panle);
			}
			if(mLastPanleBottom < patch.getBottom() + 1){
				mLastPanleBottom = patch.getBottom() + 1;
			}
		}
	}
	
	public void addPatch(Patch patch,Layout layout,StyleText styleText) {
		if(mPatchs != null){
			mPatchs.add(patch);
		}
		findPanleTop(styleText.getPanleStyleText(), patch, patch.getTop());
		if(getStart() == -1){
			setContent(patch.getStart(), patch.getEnd());
		}else{
			setContent(getStart(), patch.getEnd());
		}
		if(patch.getLayoutType() == Constant.LAYOUT_TYPE_LEFT_FLOAT
				|| patch.getLayoutType() == Constant.LAYOUT_TYPE_RIGHT_FLOAT){
			mFloatLines.add(patch);
			measureEnabledRect(layout);
//			if(patch.isPanleEnd()){
//				mNotFloatPatchBottom = patch.getBottom();
//			}
		}else{
//			if(patch.isPanleEnd() && mFloatLines.size() > 0){
//				mNotFloatPatchBottom = patch.getBottom();
//				for (Patch floatLine : mFloatLines) {
//					if(floatLine.getStart() >= patch.getPanleStart() 
//							&& floatLine.getEnd() <= patch.getPanleEnd()
//							&& mNotFloatPatchBottom < floatLine.getBottom()){
//						mNotFloatPatchBottom = floatLine.getBottom();
//					}
//				}
//			}else{
				int bottom = patch.getBottom();
				if(styleText.isPanleEnd(patch.getEnd())){
					bottom += styleText.getMarginBottom();
				}
				clearUpEnabledRect(bottom);
//			}
		}
	}
	
	private void clearUpEnabledRect(int bottom){
		mNotFloatPatchBottom = bottom;
		int i = 0,j = 0;
		for (Rect enabledRect : mEnabledRect) {
			if(enabledRect.top < mNotFloatPatchBottom){
				enabledRect.set(enabledRect.left, mNotFloatPatchBottom , enabledRect.right, enabledRect.bottom);
			}
			for (j = i + 1;j < mEnabledRect.size();j++) {
				Rect enabledRect1 = mEnabledRect.get(j);
				if(enabledRect1.top < mNotFloatPatchBottom){
					enabledRect1.set(enabledRect1.left, mNotFloatPatchBottom , enabledRect1.right, enabledRect1.bottom);
				}
				if(enabledRect.contains(enabledRect1)){
					enabledRect1.top = enabledRect1.bottom;
				}else if(enabledRect1.contains(enabledRect)){
					enabledRect.top = enabledRect.bottom;
				}
			}
			i++;
		}
	}
	/**
	 * @return the mPageIndex
	 */
	public int getPageIndex() {
		return mPageIndex;
	}

	/**
	 * 计算当前页可用区域
	 */
	private final void measureEnabledRect(Layout layout){
		layout.recycleRects(mEnabledRect);
		// 页的内容宽度
		int pageWidth = mPageRect.width();
		// 页的内容起始点X坐标
		int pageStartX = mPageRect.left;
		// 页的内容最大X坐标
		int maxX = mPageRect.right;
		// 页的内容最大Y坐标
		int maxY = mPageRect.bottom;
		//扫描起始点,找到最后一个非浮动区域的底部作为扫描起始点
		int scanStartY = mNotFloatPatchBottom + 1;
		//如果已经到了页的底部还位置找到可以使用的空间，就返回空表示空间不够
		for(;scanStartY < maxY;){
			Rect finalRect = layout.createRect();
			finalRect.set(pageStartX, scanStartY , maxX, maxY);
			int leftMinBottom = finalRect.bottom;
			int rightMinBottom = finalRect.bottom;
			for(Patch floatPatch : mFloatLines){
				if(scanStartY >= floatPatch.mTop && scanStartY < floatPatch.mBottom){
					if(floatPatch.getLayoutType() == Constant.LAYOUT_TYPE_LEFT_FLOAT){
						if(finalRect.left < floatPatch.mRight){
							finalRect.left = floatPatch.mRight;
							if(leftMinBottom > floatPatch.mBottom){
								leftMinBottom = floatPatch.mBottom;
							}
						}
					}else{
						if(finalRect.right > floatPatch.mLeft){
							finalRect.right = floatPatch.mLeft;
							if(rightMinBottom > floatPatch.mBottom){
								rightMinBottom = floatPatch.mBottom;
							}
						}
					}
				}
			}
			mEnabledRect.add(finalRect);
			//如果可用空间的宽度不用则跳到浮动区域底部位置重新开始扫描
			if(finalRect.width() < pageWidth){
				scanStartY = ( leftMinBottom < rightMinBottom ? leftMinBottom : rightMinBottom ) + 1;
			}else{
				break;
			}
		}
	}
	/**
	 * 获取可用区域
	 * @param offsetY 偏移量，找到最后一个非浮动区域的底部作为扫描起始点后会累积这个偏移量作为新的扫描起始点
	 * @param patchRect 待添加内容的区域大小
	 * @return
	 */
	public final Rect getEnabledRect(int width,int height,Rect rectContainer,Patch patch){
		boolean isStartPanle = patch.isPanleStart();
		if(patch.getLayoutType() != Constant.LAYOUT_TYPE_RIGHT_FLOAT
				&& patch.getLayoutType() != Constant.LAYOUT_TYPE_LEFT_FLOAT){
			isStartPanle = isStartPanle && patch.getEnd() == patch.getStart();
		}
		if(mPageRect.width() < width){
			width = mPageRect.width();
		}
		if(mPageRect.height() < height){
			height = mPageRect.height();
		}
		if(isStartPanle){
			if(mNotFloatPatchBottom < mLastPanleBottom){
				clearUpEnabledRect(mLastPanleBottom);
			}
		}
		for(Rect enabledRect : mEnabledRect){
			int enabledRectW = enabledRect.width();
			int enabledRectH = enabledRect.height();
			if(enabledRectW >= width && enabledRectH >= height){
				rectContainer.set(enabledRect.left,
						enabledRect.top,
						enabledRect.right,
						enabledRect.bottom);
				return rectContainer;
			}
		}
		return null;
	}
	
	final void initLayoutData(){
		mEnabledRect = new LinkedList<Rect>();
		mEnabledRect.add(new Rect(mPageRect));
		mFloatLines = new LinkedList<Patch>();
		mPatchs = new LinkedList<Patch>();
	}
	
	final void releaseLayoutData(Layout layout){
		layout.recycleRects(mEnabledRect);
		mFloatLines = null;
		mEnabledRect = null;
		mNotFloatPatchBottom = mPageRect.top;
		mLastPanleBottom = mPageRect.top;
	}
	
	void clearUpCoverLayout(StyleText styleText){
		if(!styleText.isCover() || mPatchs == null || mPatchs.isEmpty()){
			return;
		}
		int contentTop = mPatchs.get(0).getTop();
		int contentBottom = mPatchs.get(mPatchs.size() - 1).mBottom;
		int h = mPageRect.bottom - contentBottom + contentTop;
		if(h > 0){
			h /= 2;
			int newTop = h;
			newTop = contentTop - newTop;
			for (Patch patch : mPatchs) {
				patch.setLocation(patch.getLeft(), patch.getTop() - newTop);
			}
			for (Panle panle : mPanleLines.values()) {
				panle.setTop(panle.getTop() - newTop);
				panle.setBottom(panle.getBottom() - newTop);
			}
		}
	}
	
	public void advanceDraw(Canvas canvas) {
		if(isBind() && mPatchs != null){
			drawBG(canvas);
			for (Patch patch : mPatchs) {
				patch.draw(canvas);
			}
		}
	}
	
	private void drawBG(Canvas canvas){
		BackgroundColorSpan bgColorSpan = mStyleText.getRootParent().getBGColor();
		Rect fullPageRect = mSettingParam.getFullPageRect();
		if(bgColorSpan != null){
			mTextPaint.setColor(bgColorSpan.getBackgroundColor());
			canvas.drawRect(fullPageRect, mTextPaint);
		}
		ImgPanelBGDrawableSpan bgDrawable = mStyleText.getRootParent().getBGDrawable();
		if(bgDrawable != null){
			bgDrawable.drawBG(canvas, mStyleText.getDataSource(), mStart, mEnd,
					fullPageRect.left, fullPageRect.top, fullPageRect.right, fullPageRect.bottom, 0, mTextPaint);
		}
		for (Entry<Integer, Panle> entry : mPanleLines.entrySet()) {
			Panle panle = entry.getValue();
			panle.drawBG(canvas, mStyleText, mTextPaint);
		}
	}
	
	@Override
	public void draw(Canvas canvas) {
		if(isBind() && mPatchs != null){
			boolean isFullScreen = isFullScreen();
			mDrawCallback.onPreDrawContent(canvas,isFullScreen);
			drawBG(canvas);
			for (Patch patch : mPatchs) {
				patch.draw(canvas);
			}
			mDrawCallback.onPostDrawContent(canvas,isFullScreen);
		}else{
			LogUtil.e(TAG, "draw unBind");
		}
	}
	
	@Override
	public void invalidate(AbsPatch patch) {
		if(mParent != null){
			mParent.invalidate(this);
		}
	}
	
	@Override
	@Deprecated
	public void bindPatchParent(PatchParent parent,StyleText styleText) {
		
	}
	
	public void bindPatchParent(PatchParent parent,StyleText styleText,DrawCallback callback) {
		super.bindPatchParent(parent, styleText);
		mDrawCallback = callback;
		mTextPaint = new TextPaint();
//		if(mPatchs == null){
//			LinkedList<Patch> patchs = mCachePatchs.get();
//			mCachePatchs.clear();
//			if(patchs != null){
//				mPatchs = patchs;
//			}else{
//				mPatchs = new LinkedList<Patch>();
//			}
//		}
//		if(mPatchs.isEmpty()){
//			mPanleLines.clear();
//			new Layout(mSettingParam).layoutPage(mStyleText, this);
//		}
		for (Patch patch : mPatchs) {
			patch.bindPatchParent(this, styleText);
		}
	}
	
	@Override
	public void unBindPatchParent() {
		mDrawCallback = null;
		mTextPaint = null;
		if(mPatchs != null){
			for (Patch patch : mPatchs) {
				patch.unBindPatchParent();
			}
//			mCachePatchs = new SoftReference<LinkedList<Patch>>(mPatchs);
//			mPatchs = null;
		}
		super.unBindPatchParent();
	}

	@Override
	public boolean isFullScreen() {
		if(mPatchs != null){
			for (Patch patch : mPatchs) {
				if(patch.isFullScreen()){
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean dispatchClick(View v, int x, int y) {
		if(mPatchs != null){
			for (Patch patch : mPatchs) {
				if(patch.getTop() <= y
						&& patch.getBottom() >= y){
					if(patch.dispatchClick(v, x, y)){
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean isFinish() {
		if(mPatchs != null){
			for (Patch patch : mPatchs) {
				if(!patch.isFinish()){
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public Rect getFullScreenContentRect(){
		if(mPatchs != null && !mPatchs.isEmpty()){
			Patch patch = mPatchs.get(0);
			return patch.getFullScreenContentRect();
		}
		return null;
	}

	@Override
	public int findIndexByLocation(int x, int y, boolean isAccurate) {
		if(mPatchs != null){
			for (Patch patch : mPatchs) {
				if(patch.getTop() <= y
						&& patch.getBottom() >= y){
					return patch.findIndexByLocation(x, y ,isAccurate);
				}
			}
		}
		return -1;
	}

	@Override
	public Rect findRectByPosition(int position) {
		if(mPatchs != null){
			if(getStart() <= position && position <= getEnd()){
				for (Patch patch : mPatchs) {
					if(patch.getStart() <= position && position <= patch.getEnd()){
						return patch.findRectByPosition(position);
					}
				}
			}
		}
		return null;
	}
}
