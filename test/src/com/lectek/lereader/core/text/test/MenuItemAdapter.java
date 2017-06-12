package com.lectek.lereader.core.text.test;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.lectek.bookformats.R;


/**
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2011-11-2
 */
public class MenuItemAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	private ArrayList<MenuItem> menuItems;
	
	public MenuItemAdapter(Context context, ArrayList<MenuItem> menuItems){
		super();
		inflater = LayoutInflater.from(context);
		this.menuItems = menuItems;
	}

	@Override
	public int getCount() {
		if(menuItems != null){
			return menuItems.size();
		}
		return 0;
	}

	@Override
	public MenuItem getItem(int position) {
		if(position < getCount()){
			return menuItems.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if(convertView == null){
			convertView = newView();
			viewHolder = new ViewHolder();
			viewHolder.imageView = (CheckedTextView) convertView.findViewById(R.id.menu_icon);
			viewHolder.textView = (TextView) convertView.findViewById(R.id.menu_name);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		MenuItem item = getItem(position);
		viewHolder.imageView.setBackgroundResource(item.iconResId);
		viewHolder.textView.setText(item.name);
		return convertView;
	}
	
	private View newView(){
		return inflater.inflate(R.layout.menu_item_leyue, null);
	}
	
	private class ViewHolder {
		
		public CheckedTextView imageView;
		public TextView textView;
		
	}

}
