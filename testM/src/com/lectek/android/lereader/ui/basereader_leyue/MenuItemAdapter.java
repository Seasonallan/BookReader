package com.lectek.android.lereader.ui.basereader_leyue;

import java.util.ArrayList;

import com.lectek.android.LYReader.R;
import com.lectek.lereader.core.pdf.PdfReaderView;

import android.content.Context;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;


/**
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2011-11-2
 */
public class MenuItemAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	private ArrayList<MenuItem> menuItems;
	private Context mContext;
	
	public MenuItemAdapter(Context context, ArrayList<MenuItem> menuItems){
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
			viewHolder.imageView = (CheckedTextView) convertView.findViewById(R.id.menu_icon);
			viewHolder.textView = (TextView) convertView.findViewById(R.id.menu_name);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		MenuItem item = getItem(position);
		viewHolder.imageView.setBackgroundResource(item.iconResId);
        if (TextUtils.isEmpty(item.name)){
            viewHolder.textView.setVisibility(View.GONE);
        }else{
            viewHolder.textView.setVisibility(View.VISIBLE);
            viewHolder.textView.setText(item.name);
        }
		convertView.setId(position);
		return convertView;
	}
	
	private View newView(){
		return inflater.inflate(R.layout.menu_item_leyue, null);
	}
	/**
	 * 更新cartoon布局类型图标
	 * @param parent
	 * @param layoutType
	 */
	public void updateCartoonLayoutIcon(ViewGroup parent, int layoutType) {
		if(parent == null) {
			return;
		}
		for(int i = 0; i < parent.getChildCount(); i++) {
			int childId = parent.getChildAt(i).getId();
			if (childId >= this.getCount() || childId < 0) {
				continue;
			}
			MenuItem item = getItem(childId);
			if(item == null) {
				return;
			}
			if(MenuItem.MENU_ITEM_ID_FONT == item.id) {
				if(layoutType == Configuration.ORIENTATION_LANDSCAPE) {
					item.iconResId = R.drawable.menu_icon_fit_screen;
					item.name = mContext.getString(R.string.cartoon_menu_portrait);
				} else {
					item.iconResId = R.drawable.menu_icon_fit_width;
					item.name = mContext.getString(R.string.cartoon_menu_landscape);
				}
			}
		}
		notifyDataSetChanged();
	}
    /**
     * 更新白天黑夜类型图标
     * @param parent
     * @param layoutType
     */
    public void updateDayNightLayoutIcon(ViewGroup parent, int layoutType) {
        if(parent == null) {
            return;
        }
        for(int i = 0; i < parent.getChildCount(); i++) {
            int childId = parent.getChildAt(i).getId();
            if (childId >= this.getCount() || childId < 0) {
                continue;
            }
            MenuItem item = getItem(childId);
            if(item == null) {
                return;
            }
            if(MenuItem.MENU_ITEM_ID_N_NIGHT_DAY == item.id) {
                item.iconResId = layoutType;
            }
        }
        notifyDataSetChanged();
    }
	/**
	 * 更新pdf布局类型图标
	 * @param parent
	 * @param layoutType
	 */
	public void updatePdfLayoutIcon(ViewGroup parent, int layoutType) {
		if(parent == null) {
			return;
		}
		for(int i = 0; i < parent.getChildCount(); i++) {
			int childId = parent.getChildAt(i).getId();
			if (childId >= this.getCount() || childId < 0) {
				continue;
			}
			MenuItem item = getItem(childId);
			if(item == null) {
				return;
			}
			if(MenuItem.MENU_ITEM_ID_PDF_LAYOUT_TYPE == item.id) {
				if(layoutType == PdfReaderView.LAYOUT_TYPE_FITSCREEN) {
					item.iconResId = R.drawable.menu_icon_fit_width;
					item.name = mContext.getString(R.string.reader_menu_item_fit_width);
				} else if (layoutType == PdfReaderView.LAYOUT_TYPE_FITWIDTH) {
					item.iconResId = R.drawable.menu_icon_fit_screen;
					item.name = mContext.getString(R.string.reader_menu_item_fit_screen);
				}
			}
		}
		notifyDataSetChanged();
	}
	
	private class ViewHolder {
		
		public CheckedTextView imageView;
		public TextView textView;
		
	}

}
