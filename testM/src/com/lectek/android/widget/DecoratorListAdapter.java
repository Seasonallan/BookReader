package com.lectek.android.widget;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;


public class DecoratorListAdapter extends BaseAdapter {
	private ListAdapter mAdapter;
	private MyDataSetObserver mObserver;
	public DecoratorListAdapter(ListAdapter adapter){
		mAdapter = adapter;
		mObserver = new MyDataSetObserver();
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
	public boolean isEnabled(int position) {
		return mAdapter.isEnabled(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return mAdapter.getView(position, convertView, parent);
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
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
	
	private class MyDataSetObserver extends DataSetObserver{
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
