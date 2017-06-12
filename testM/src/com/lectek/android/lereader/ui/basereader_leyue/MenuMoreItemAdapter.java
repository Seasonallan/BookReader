package com.lectek.android.lereader.ui.basereader_leyue;

import android.content.Context;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.lectek.android.LYReader.R;
import com.lectek.lereader.core.pdf.PdfReaderView;

import java.util.ArrayList;


/**
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2011-11-2
 */
public class MenuMoreItemAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private ArrayList<MenuItem> menuItems;
	private Context mContext;

	public MenuMoreItemAdapter(Context context, ArrayList<MenuItem> menuItems){
		super();
		mContext = context;
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
			viewHolder.textView = (TextView) convertView.findViewById(R.id.menu_name);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		MenuItem item = getItem(position);
        viewHolder.textView.setText(item.name);
		convertView.setId(position);
		return convertView;
	}
	
	private View newView(){
		return inflater.inflate(R.layout.menu_item_more, null);
	}

	private class ViewHolder {

		public TextView textView;
		
	}

}
