package com.lectek.android.widget;

import java.lang.ref.WeakReference;

import android.os.Parcelable;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.widget.ViewPagerTabHost.AbsPagerTabHostAdapter;

public abstract class BaseViewPagerTabHostAdapter extends AbsPagerTabHostAdapter {
	private static final String TAG = BaseViewPagerTabHostAdapter.class.getSimpleName();
	private WeakReference<ViewGroup> mContainer;
	private ItemLifeCycleListener mItemLifeCycleListener;
	private SparseArray<View> mItemCreatedViews = new SparseArray<View>();
	private int mOldPrimaryItemPosition = -1;

	public abstract View getItemView(ViewGroup container,int position);
	
	public int getItemIdByPosition(int position){
		return position + 1;
	}
	
	public int getItemIdByTab(String tab){
		for (int i = 0; i < getCount(); i++) {
			if(getTab(i).equals(tab)){
				return getItemIdByPosition(i);
			}
		}
		return -1;
	}
	
	public View findViewByTab(String tab){
		if(mContainer != null && mContainer.get() != null){
			return mContainer.get().findViewById(getItemIdByTab(tab));
		}
		return null;
	}
	
	public View findViewByPosition(int position){
		if(mContainer != null && mContainer.get() != null){
			return mContainer.get().findViewById(getItemIdByPosition(position));
		}
		return null;
	}
	
	@Override
	public void startUpdate(ViewGroup container) {
		LogUtil.v(TAG, "startUpdate " +mContainer);
		if(mContainer == null || mContainer.get() == null){
			mContainer = new WeakReference<ViewGroup>(container);
		}
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		LogUtil.i(TAG, "instantiateItem : position="+position);
		View contentView = getItemView(container,position);
		if(contentView.getParent() == null){
			contentView.setId(getItemIdByPosition(position));
			container.addView(contentView);
		}else if(!contentView.getParent().equals(container)){
			((ViewGroup)contentView.getParent()).removeView(contentView);
			contentView.setId(getItemIdByPosition(position));
			container.addView(contentView);
		}
		return contentView;
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		if(object == null){
			return;
		}
		if(mOldPrimaryItemPosition != position){
            LogUtil.v(TAG, "setPrimaryItem : position="+position+" object="+object);
			View view = mItemCreatedViews.get(position);
			if(view == null && mItemLifeCycleListener != null){
                View contentView = (View) object;
				if(mItemLifeCycleListener.onCreate(contentView, position)){
					mItemCreatedViews.put(position,contentView);
				}
				mOldPrimaryItemPosition = position;
			}
		}
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		LogUtil.i(TAG, "destroyItem : position="+position+" object="+object);
		if(object == null){
			return;
		}
		View contentView = (View) object;
		View view = mItemCreatedViews.get(position);
		if(view != null && mItemLifeCycleListener != null){
			mItemLifeCycleListener.onDestroy(view, position);
		}
		if(contentView.getParent() != null){
			((ViewGroup)contentView.getParent()).removeView(contentView);
		}
		mItemCreatedViews.remove(position);
	}

	@Override
	public void finishUpdate(final ViewGroup container) {
		LogUtil.v(TAG, "finishUpdate");
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {
	}

	public void setItemLifeCycleListener(ItemLifeCycleListener l){
		mItemLifeCycleListener = l;
		notifyDataSetChanged();
	}
	
	public interface ItemLifeCycleListener{
		public boolean onCreate(View view,int position);
		public void onDestroy(View view,int position);
	}
}
