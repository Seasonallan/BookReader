package com.lectek.lereader.core.text.test;


import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TabHost;

public class ViewPagerTabHost extends BaseTabHost{
    private ViewPager mViewPager;
    private FrameLayout mTempContentLayout;
    private FrameLayout mContentLayout;
	private OnPageChangeListener mOnPageChangeListener;
	private SlideTabWidget mSlideTabWidget;
	private InteriorPagerAdapter mInteriorPagerAdapter;
	private OnTabChangeListener mOnTabChangeListener;
	
	public ViewPagerTabHost(Context context) {
		super(context);
	}

	public ViewPagerTabHost(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onInit() {
		super.onInit();
		mInteriorPagerAdapter = new InteriorPagerAdapter(this);
		mViewPager = new InteriorViewPager(getContext()){
			@Override
			public boolean onInterceptTouchEvent(MotionEvent arg0) {
				return super.onInterceptTouchEvent(arg0);
			}
		};
		mViewPager.setId(mViewPager.hashCode());
		mViewPager.setAdapter(mInteriorPagerAdapter);
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				dispatchPageSelected(position);
			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				dispatchPageScrolled(position, positionOffset, positionOffsetPixels);
			}
			
			@Override
			public void onPageScrollStateChanged(int state) {
				dispatchPageScrollStateChanged(state);
			}
		});
        setOffscreenPageLimit(2);
        mTempContentLayout = new FrameLayout(getContext());
        mTempContentLayout.setLayoutParams(new LayoutParams(0, 0));
        mTempContentLayout.setOnHierarchyChangeListener(new OnHierarchyChangeListener() {
			@Override
			public void onChildViewRemoved(View parent, View child) {
			}
			
			@Override
			public void onChildViewAdded(View parent, View child) {
				mTempContentLayout.removeView(child);
			}
		});
	}
	
	public OnPageChangeListener getOnPageChangeListener(){
		return mOnPageChangeListener;
	}
	
	public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener){
		mOnPageChangeListener = onPageChangeListener;
	}
	
	public void setOffscreenPageLimit(int limit){
		mViewPager.setOffscreenPageLimit(limit);
	}
	
	public int getOffscreenPageLimit(){
		return mViewPager.getOffscreenPageLimit();
	}
	
	public void setAdapter(AbsPagerTabHostAdapter adapter){
		if(super.getTabContentView() == null){
			setup();
		}
		mInteriorPagerAdapter.setAdapter(adapter);
	}
	
	public AbsPagerTabHostAdapter getAdapter(){
		if(mInteriorPagerAdapter != null){
			return mInteriorPagerAdapter.getAdapter();
		}
		return null;
	}
	
	public ViewPager getViewPager(){
		return mViewPager;
	}
	
	@Override
	public FrameLayout getTabContentView() {
		return mContentLayout;
	}
	 
	@Override
	public void setup() {
		View view = findViewById(android.R.id.tabs);
		if(view instanceof SlideTabWidget){
			mSlideTabWidget = (SlideTabWidget) view;
		}
		mContentLayout = (FrameLayout) findViewById(android.R.id.tabcontent);
		if(!mTempContentLayout.equals(mContentLayout)){
			mContentLayout.removeAllViews();
			mContentLayout.addView(mViewPager,new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			mContentLayout.setId(this.hashCode());
			mTempContentLayout.setId(android.R.id.tabcontent);
			this.addView(mTempContentLayout);
		}
		super.setup();
	}

	public int getTabIndexByTag(String tag){
		int i = -1;
        for (i = 0; i < mInteriorPagerAdapter.getCount(); i++) {
            if (mInteriorPagerAdapter.getTab(i).equals(tag)) {
                break;
            }
        }
        return i;
	}
	
	@Override
	protected void dispatchTabChanged(String tabId) {
		super.dispatchTabChanged(tabId);
		 int position = getCurrentTab();
		 mViewPager.setCurrentItem(position, true);
	}
	
	@Override
	public void setOnTabChangedListener(OnTabChangeListener l) {
		mOnTabChangeListener = l;
	}
	
	protected void dispatchPageScrolled(int position, float positionOffset, int positionOffsetPixels){
		if(mOnPageChangeListener != null){
			mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
		}
		if(mSlideTabWidget != null){
			mSlideTabWidget.dispatchPageScrolled(position, positionOffset, positionOffsetPixels);
		}
	}
	
	public void dispatchPageSelected(int position){
		if(mOnPageChangeListener != null){
			mOnPageChangeListener.onPageSelected(position);
		}
		super.setCurrentTab(position);
	}
	
	protected void dispatchPageScrollStateChanged(int state){
		if(mOnPageChangeListener != null){
			mOnPageChangeListener.onPageScrollStateChanged(state);
		}
	}
	
	private class InteriorPagerAdapter extends AbsPagerTabHostAdapter{
		private AbsPagerTabHostAdapter mAdapter;
		private DataSetObserver mObserver = null;
		private TabHost mTabHost;
		private int mOldPosition = -1;
		public InteriorPagerAdapter(TabHost tabHost){
			mTabHost = tabHost;
			mObserver = new DataSetObserver(){
				@Override
				public void onChanged() {
					InteriorPagerAdapter.this.notifyDataSetChanged();
					setupTabWidget(mAdapter);
				}
			};
		}
		
		private void setupTabWidget(AbsPagerTabHostAdapter adapter){
			if(mTabHost == null){
				return;
			}
			mTabHost.clearAllTabs();
			if(adapter == null){
				return;
			}
			int size = adapter.getCount();
			for(int i = 0 ; i < size ;i++){
				mTabHost.addTab(mTabHost.newTabSpec(adapter.getTab(i)).setIndicator(adapter.getIndicator(i)).setContent(new TabContentFactory() {	
					@Override
					public View createTabContent(String tag) {
						return new FrameLayout(getContext());
					}
				}));
			}
		}
		
		public AbsPagerTabHostAdapter getAdapter(){
			return mAdapter;
		}
		
		public void setAdapter(AbsPagerTabHostAdapter adapter){
			if(mAdapter != null){
				mAdapter.unregisterObserver(mObserver);
			}
			mAdapter = adapter;
			if(mAdapter != null){
				mAdapter.registerObserver(mObserver);
				mAdapter.notifyDataSetChanged();
			}else{
				setupTabWidget(mAdapter);
				this.notifyDataSetChanged();
			}
		}
		
		@Override
		public int getCount() {
			if(mAdapter != null){
				return mAdapter.getCount();
			}
			return 0;
		}

		@Override
		public View getIndicator(int position) {
			if(mAdapter != null){
				return mAdapter.getIndicator(position);
			}
			return null;
		}

		@Override
		public String getTab(int position) {
			if(mAdapter != null){
				return mAdapter.getTab(position);
			}
			return null;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			if(mAdapter != null){
				mAdapter.destroyItem(container, position, object);
				return;
			}
			super.destroyItem(container, position, object);
		}

		@Override
		public void finishUpdate(ViewGroup container) {
			if(mAdapter != null){
				mAdapter.finishUpdate(container);
				return;
			}
			super.finishUpdate(container);
		}

		@Override
		public int getItemPosition(Object object) {
			if(mAdapter != null){
				return mAdapter.getItemPosition(object);
			}
			return super.getItemPosition(object);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			if(mAdapter != null){
				return mAdapter.getPageTitle(position);
			}
			return super.getPageTitle(position);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			if(mAdapter != null){
				return mAdapter.instantiateItem(container, position);
			}
			return super.instantiateItem(container, position);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			if(mAdapter != null){
				return mAdapter.isViewFromObject(arg0, arg1);
			}
			return false;
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
			if(mAdapter != null){
				mAdapter.restoreState(state, loader);
				return;
			}
			super.restoreState(state, loader);
		}

		@Override
		public Parcelable saveState() {
			if(mAdapter != null){
				return mAdapter.saveState();
			}
			return super.saveState();
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position,
				Object object) {
			if(mAdapter != null){
				mAdapter.setPrimaryItem(container, position, object);
				if(mOnTabChangeListener != null && position != mOldPosition){
					mOnTabChangeListener.onTabChanged(mAdapter.getTab(position));
				}
				mOldPosition = position;
				return;
			}
			super.setPrimaryItem(container, position, object);
		}
		
		@Override
		public void startUpdate(ViewGroup container) {
			if(mAdapter != null){
				mAdapter.startUpdate(container);
				return;
			}
			super.startUpdate(container);
		}
	}
	
	public static abstract class AbsPagerTabHostAdapter extends PagerAdapter {
		private DataSetObservable mObservable = new DataSetObservable();
		
	    void registerObserver(DataSetObserver observer) {
	        mObservable.registerObserver(observer);
	    }
	    
	    void unregisterObserver(DataSetObserver observer) {
	        mObservable.unregisterObserver(observer);
	    }
	    
		@Override
	    public void notifyDataSetChanged() {
	        mObservable.notifyChanged();
	        super.notifyDataSetChanged();
	    }
	    
		public abstract View getIndicator(int position);
		public abstract String getTab(int position);
	}
	
	public void setIndicatorPaddingWidth(int padding){
		if(mSlideTabWidget != null){
			mSlideTabWidget.setIndicatorPaddingWidth(padding);
		}
	}
	
	public void setIndicatorOffsetY(int offsetY){
		if(mSlideTabWidget != null){
			mSlideTabWidget.setIndicatorOffsetY(offsetY);
		}
	}
	
	public void setIndicatorOffsetX(int offsetX){
		if(mSlideTabWidget != null){
			mSlideTabWidget.setIndicatorOffsetX(offsetX);
		}
	}
	
	public void setSlideTabWidgetShowAtTop(){
		if(mSlideTabWidget != null){
			mSlideTabWidget.setShowAtTop(true);
		}
	}
	
	public void slideTabWidgetinitialize(Drawable indicatorDrawable){
		if(mSlideTabWidget != null){
			mSlideTabWidget.initialize(indicatorDrawable);
		}
	}
	
	public void slideTabWidgetinitialize(int indicatorHeight,Drawable indicatorDrawable){
		if(mSlideTabWidget != null){
			mSlideTabWidget.initialize(indicatorHeight, indicatorDrawable);
		}
	}
	
	private class InteriorViewPager extends ViewPager{
		public InteriorViewPager(Context context) {
			super(context);
		}

		@Override
		protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
			int childCount = getChildCount();
	        final int destX = (getWidth() + getPageMargin()) * getCurrentItem();
			for (int i = 0; i < childCount; i++) {
                final View child = getChildAt(i);
                if(child.getLeft() >= destX && child.getLeft() < destX + getWidth()){
                	final int scrollX = v.getScrollX();
    	            final int scrollY = v.getScrollY();
                   if(canScrollChild(child, true, dx, x + scrollX - child.getLeft(),
                           y + scrollY - child.getTop())){
                	   return true;
                   }
                }
            }
			return checkV;
		}
		
		protected boolean canScrollChild(View v, boolean checkV, int dx, int x, int y) {
			boolean canScroll = false;
			if(v instanceof ViewPagerChild && ((ViewPagerChild) v).canScroll(this, dx, x, y)){
				return true;
			}
	        if (v instanceof ViewGroup) {
	        	if(v instanceof InteriorViewPager){
	        		InteriorViewPager viewPager = (InteriorViewPager) v;
	        		PagerAdapter adapter = viewPager.getAdapter();
	        		int childCount = viewPager.getChildCount();
	        		if(adapter == null || adapter.getCount() <= 1 || childCount <= 1){
	        			canScroll = !checkV;
	        		}
	        		int count = adapter.getCount();
	        		final int widthWithMargin = viewPager.getWidth() + viewPager.getPageMargin();
                    final int lastItemIndex = count - 1;
        			final float leftBound = Math.max(0, (viewPager.getCurrentItem() - 1) * widthWithMargin);
                    final float rightBound = Math.min(viewPager.getCurrentItem() + 1, lastItemIndex) * widthWithMargin;
	        		if(dx > 0){//向右
	        			if(viewPager.getCurrentItem() != 0 || viewPager.getScrollX() < leftBound){
	        				canScroll = checkV;
	        			}
	        		}else{
	        			if(viewPager.getCurrentItem() != count - 1 || viewPager.getScrollX() > rightBound){
	        				canScroll = checkV;
	        			}
	        		}
	        	}
	            final ViewGroup group = (ViewGroup) v;
	            final int scrollX = v.getScrollX();
	            final int scrollY = v.getScrollY();
	            final int count = group.getChildCount();
	            for (int i = count - 1; i >= 0; i--) {
	                final View child = group.getChildAt(i);
	                if (canScrollChild(child, true, dx, x + scrollX - child.getLeft(),
	                                y + scrollY - child.getTop()) || canScroll) {
	                    return true;
	                }
	            }
	        }
	        return false;
		}
	}
	
	public interface ViewPagerChild{
		public boolean canScroll(ViewPager viewPager,int dx, int x, int y);
	}
}
