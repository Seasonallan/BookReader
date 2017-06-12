package com.lectek.lereader.core.text.test;

import java.util.HashMap;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class CheckedGridView extends GridView {
	public static final int CHOICE_MODE_NONE = 0;
	public static final int CHOICE_MODE_SINGLE = 1;
	public static final int CHOICE_MODE_MULTIPLE = 2;

	private int mChoiceMode;
	private SparseBooleanArray mCheckStates;
	private HashMap<Long, Boolean> mCheckedIdStates;
	private boolean mInLayout = false;
	private OnItemCheckedStateChangeListener mOnItemCheckedStateChangeListener;

	public CheckedGridView(Context context) {
		super(context);
		init();
	}

	public CheckedGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CheckedGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		mInLayout = true;
		super.onLayout(changed, l, t, r, b);
		mInLayout = false;
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		if(adapter != null){
			adapter = new InteriorAdapter(adapter);
			if (mChoiceMode != CHOICE_MODE_NONE && adapter.hasStableIds()
					&& mCheckedIdStates == null) {
				mCheckedIdStates = new HashMap<Long, Boolean>();
			}
		}
		super.setAdapter(adapter);
		if (mCheckStates != null) {
			mCheckStates.clear();
		}
		if (mCheckedIdStates != null) {
			mCheckedIdStates.clear();
		}
	}

	public void setChoiceMode(int choiceMode) {
		mChoiceMode = choiceMode;
		if (mChoiceMode != CHOICE_MODE_NONE) {
			if (mCheckStates == null) {
				mCheckStates = new SparseBooleanArray();
			}
			if (mCheckedIdStates == null && getAdapter() != null
					&& getAdapter().hasStableIds()) {
				mCheckedIdStates = new HashMap<Long, Boolean>();
			}
		}
	}

	@Override
	public boolean performItemClick(View view, int position, long id) {
		boolean handled = false;
		if (mChoiceMode != ListView.CHOICE_MODE_NONE) {
			handled = true;
			boolean newValue = !mCheckStates.get(position, false);
			if(!dispatchPreItemCheckedStateChange(position,newValue)){
				if (mChoiceMode == ListView.CHOICE_MODE_MULTIPLE) {
					mCheckStates.put(position, newValue);
					if (mCheckedIdStates != null && getAdapter().hasStableIds()) {
						if (newValue) {
							mCheckedIdStates.put(getAdapter().getItemId(position),
									Boolean.TRUE);
						} else {
							mCheckedIdStates.remove(getAdapter()
									.getItemId(position));
						}
					}
				} else {
					if (newValue) {
						mCheckStates.clear();
						mCheckStates.put(position, true);
						if (mCheckedIdStates != null && getAdapter().hasStableIds()) {
							mCheckedIdStates.clear();
							mCheckedIdStates.put(getAdapter().getItemId(position),
									Boolean.TRUE);
						}
					}else{
						mCheckStates.clear();
						if (mCheckedIdStates != null && getAdapter().hasStableIds()) {
							mCheckedIdStates.clear();
						}
					}
				}
				invalidateViews();
				dispatchItemCheckedStateChange(position, newValue);
			}
		}

		handled |= super.performItemClick(view, position, id);
		return handled;
	}

	/**
	 * Sets the checked state of the specified position. The is only valid if
	 * the choice mode has been set to {@link #CHOICE_MODE_SINGLE} or
	 * {@link #CHOICE_MODE_MULTIPLE}.
	 * 
	 * @param position
	 *            The item whose checked state is to be checked
	 * @param value
	 *            The new checked state for the item
	 */
	public void setItemChecked(int position, boolean value) {
		if (mChoiceMode == CHOICE_MODE_NONE) {
			return;
		}
		boolean isChange = mCheckStates.get(position,false) != value;
		if(!isChange || dispatchPreItemCheckedStateChange(position,value)){
			return;
		}
		if (mChoiceMode == CHOICE_MODE_MULTIPLE) {
			mCheckStates.put(position, value);
			if (mCheckedIdStates != null && getAdapter().hasStableIds()) {
				if (value) {
					mCheckedIdStates.put(getAdapter().getItemId(position),
							Boolean.TRUE);
				} else {
					mCheckedIdStates.remove(getAdapter().getItemId(position));
				}
			}
		} else {
			boolean updateIds = mCheckedIdStates != null
					&& getAdapter().hasStableIds();
			// Clear all values if we're checking something, or unchecking the
			// currently
			// selected item
			mCheckStates.clear();
			if (updateIds) {
				mCheckedIdStates.clear();
			}
			// this may end up selecting the value we just cleared but this way
			// we ensure length of mCheckStates is 1, a fact
			// getCheckedItemPosition relies on
			if (value) {
				mCheckStates.put(position, true);
				if (updateIds) {
					mCheckedIdStates.put(getAdapter().getItemId(position),
							Boolean.TRUE);
				}
			}
		}
		// Do not generate a data change while we are in the layout phase
		if (!mInLayout) {
			invalidateViews();
		}
		dispatchItemCheckedStateChange(position, value);
	}

	private void dispatchItemCheckedStateChange(int position,boolean isChecked){
		if(mOnItemCheckedStateChangeListener != null){
			mOnItemCheckedStateChangeListener.onItemCheckedStateChange(this,position, isChecked);
		}
	}
	
	private boolean dispatchPreItemCheckedStateChange(int position,boolean isChecked){
		if(mOnItemCheckedStateChangeListener != null){
			return mOnItemCheckedStateChangeListener.onPreItemCheckedStateChange(this,position, isChecked);
		}
		return false;
	}
	
	public void setOnItemCheckedStateChangeListener(OnItemCheckedStateChangeListener listener){
		mOnItemCheckedStateChangeListener = listener;
	}
	public boolean isItemChecked(int position) {
		if (mChoiceMode != CHOICE_MODE_NONE && mCheckStates != null) {
			return mCheckStates.get(position);
		}
		return false;
	}

	public int getCheckedItemPosition() {
		if (mChoiceMode == CHOICE_MODE_SINGLE && mCheckStates != null
				&& mCheckStates.size() == 1) {
			return mCheckStates.keyAt(0);
		}

		return INVALID_POSITION;
	}

	/**
	 * Clear any choices previously set
	 */
	public void clearChoices() {
		boolean isChange = false;
		if (mCheckStates != null) {
			isChange = mCheckStates.size() != 0;
			mCheckStates.clear();
		}
		if (mCheckedIdStates != null) {
			isChange = isChange || mCheckedIdStates.size() != 0;
			mCheckedIdStates.clear();
		}
		if (!mInLayout && isChange) {
			invalidateViews();
		}
	}

	public SparseBooleanArray getCheckedItemPositions() {
		if (mChoiceMode != CHOICE_MODE_NONE) {
			return mCheckStates;
		}
		return null;
	}

	public class InteriorAdapter extends BaseAdapter {
		private ListAdapter mAdapter;
		private MyDataSetObserver mObserver;

		public InteriorAdapter(ListAdapter adapter) {
			mAdapter = adapter;
			mObserver = new MyDataSetObserver();
		}

		@Override
		public boolean hasStableIds() {
			return mAdapter.hasStableIds();
		}

		@Override
		public boolean areAllItemsEnabled() {
			return mAdapter.areAllItemsEnabled();
		}

		@Override
		public boolean isEnabled(int position) {
			return mAdapter.isEnabled(position);
		}

		@Override
		public int getItemViewType(int position) {
			return mAdapter.getItemViewType(position);
		}

		@Override
		public int getViewTypeCount() {
			return mAdapter.getViewTypeCount();
		}

		@Override
		public boolean isEmpty() {
			return mAdapter.isEmpty();
		}

		@Override
		public int getCount() {
			return mAdapter.getCount();
		}

		@Override
		public Object getItem(int position) {
			return mAdapter.getItem(position);
		}

		@Override
		public long getItemId(int position) {
			return mAdapter.getItemId(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = mAdapter.getView(position, convertView, parent);
			if (mChoiceMode != CHOICE_MODE_NONE && mCheckStates != null) {
				if (convertView instanceof Checkable) {
					((Checkable) convertView).setChecked(mCheckStates
							.get(position));
				}
			}
			return convertView;
		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
			super.registerDataSetObserver(observer);
			mAdapter.registerDataSetObserver(mObserver);
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			super.unregisterDataSetObserver(observer);
			mAdapter.unregisterDataSetObserver(mObserver);
		}

		private class MyDataSetObserver extends DataSetObserver {
			@Override
			public void onChanged() {
				super.onChanged();
				notifyDataSetChanged();
			}

			@Override
			public void onInvalidated() {
				super.onInvalidated();
				notifyDataSetInvalidated();
			}
		}
	}
	
	public interface OnItemCheckedStateChangeListener{
		/**
		 * 选中状态改变前的回调
		 * @param parent
		 * @param position
		 * @param isChecked
		 * @return 返回true 则阻止选中事件发生
		 */
		public boolean onPreItemCheckedStateChange(AdapterView<?> parent,int position,boolean isChecked);
		/**
		 * 选中状态发生后的回调
		 * @param parent
		 * @param position
		 * @param isChecked
		 */
		public void onItemCheckedStateChange(AdapterView<?> parent,int position,boolean isChecked);
	}
}
