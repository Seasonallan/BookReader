package com.lectek.android.lereader.widgets.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.data.ReadStyleItem;

/**
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2011-11-26
 */
public class ReadStytleItemAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	private ArrayList<ReadStyleItem> readStyleItems;
	private int oldSelect = -1;
	public ReadStytleItemAdapter(Context context, ArrayList<ReadStyleItem> readStyleItems){
		super();
		inflater = LayoutInflater.from(context);
		this.readStyleItems = readStyleItems;
	}

	@Override
	public int getCount() {
		if(readStyleItems != null){
			return readStyleItems.size();
		}
		return 0;
	}

	@Override
	public ReadStyleItem getItem(int position) {
		if(position < getCount()){
			return readStyleItems.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder;
		if(convertView == null){
			convertView = newView();
			viewHolder = new ViewHolder();
			viewHolder.contentIV = (ImageView) convertView.findViewById(R.id.content_iv);
			viewHolder.selectTV = (ImageView) convertView.findViewById(R.id.select_iv);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		ReadStyleItem item = getItem(position);
		
		viewHolder.contentIV.setImageResource(item.resId);
		if(item.isSelected){
			oldSelect = position;
            viewHolder.contentIV.setSelected(true);
			viewHolder.selectTV.setVisibility(View.INVISIBLE);
		}else{
            viewHolder.contentIV.setSelected(false);
			viewHolder.selectTV.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}
	
	private View newView(){
		return inflater.inflate(R.layout.reader_style_item, null);
	}
	public void setSeleted(int position){
		if(position != oldSelect){
			if(oldSelect != -1){
				getItem(oldSelect).isSelected = false;
			}
			getItem(position).isSelected = true;
			oldSelect = position;
			notifyDataSetChanged();
		}
	}
	private class ViewHolder {
		
		public ImageView contentIV;
		public ImageView selectTV;
		
	}

}
