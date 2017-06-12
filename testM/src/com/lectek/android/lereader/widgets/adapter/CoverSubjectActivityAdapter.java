package com.lectek.android.lereader.widgets.adapter;

import java.util.ArrayList;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.lib.image.ImageLoader;
import com.lectek.android.lereader.net.response.SubjectResultInfo;
import com.lectek.android.lereader.utils.Constants;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2011-5-10
 */
public class CoverSubjectActivityAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	private ArrayList<SubjectResultInfo> specialSubjectInfoList;
	private Context mContext;
	
	public CoverSubjectActivityAdapter(Context context, ArrayList<SubjectResultInfo> specialSubjectInfoList){
		inflater = LayoutInflater.from(context);
		this.mContext = context;
		this.specialSubjectInfoList = specialSubjectInfoList;
	}

	@Override
	public int getCount() {
		if(specialSubjectInfoList != null){
			return specialSubjectInfoList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.cover_gallery_item, null);
			convertView.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.FILL_PARENT));//mContext.getResources().getDimensionPixelSize(R.dimen.index_page_subject_activity_rg_height)
		}
		ImageView iv = (ImageView)convertView.findViewById(R.id.cover_image_iv);
		TextView tv = (TextView)convertView.findViewById(R.id.cover_des_tv);
//		ImageView iv = (ImageView) convertView;
		SubjectResultInfo info = specialSubjectInfoList.get(position);
		tv.setText(info.getSubjectIntro());
		ImageLoader imageLoader = new ImageLoader(mContext);
//		imageLoader.setImageViewBitmap(Constants.BOOKS_TEMP, Constants.BOOKS_TEMP_IMAGE,info.getContent(),item.getContent(),viewHolder.leftImgIv,R.drawable.book_default);
		imageLoader.setImageViewBitmap(Constants.BOOKS_TEMP, Constants.BOOKS_TEMP_IMAGE, info.getSubjectPic(), info.getSubjectPic(), iv, convertView, R.drawable.test_subject_1);
		return convertView;
	}

}
