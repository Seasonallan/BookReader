package com.lectek.android.widget;

import java.util.ArrayList;
import java.util.List;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.lib.image.ImageLoader;
import com.lectek.android.lereader.net.response.ContentInfoLeyue;
import com.lectek.android.lereader.ui.specific.ActivityChannels;
import com.lectek.android.lereader.ui.specific.ContentInfoActivityLeyue;
import com.lectek.android.lereader.utils.Constants;
import com.lectek.android.widget.ViewPagerTabHost.ViewPagerChild;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GVViewPager extends RelativeLayout implements ViewPagerChild{
	ArrayList<ContentInfoLeyue> datas = new ArrayList<ContentInfoLeyue>();
	ViewPager vp;
	LinearLayout ll;
	int pageNum = 0;
	private Context context;
	GridView gv;

	public GVViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		View v = View.inflate(context, R.layout.gvvp_layout, this);
		vp = (ViewPager) v.findViewById(R.id.vp_container);
		ll = (LinearLayout) v.findViewById(R.id.ll_points);
		this.context = context;

	}

	private void initImageViews() {
		if (ll.getChildCount() != 0) {
			ll.removeAllViews();
		}
		for (int i = 0; i < pageNum; i++) {
			ImageView iv = new ImageView(context);
			iv.setPadding(6, 0, 6, 0);
			if (i == 0) {
				iv.setImageResource(R.drawable.point_black);
			} else {
				iv.setImageResource(R.drawable.point_white);
			}
			ll.addView(iv);
		}

	}

	private void initViewPager() {
		vp.setAdapter(new MyPagerAdapter(datas));
	}

	public void setDatas(ArrayList<ContentInfoLeyue> datas, Boolean visibility) {
		if(datas != null && datas.size() > 0) {
			this.datas = datas;
			int size = datas.size();
			if (size % 3 == 0) {
				pageNum = datas.size() / 3;
			} else {
				pageNum = datas.size() / 3 + 1;
			}
			initImageViews();
			initViewPager();
			vp.setOnPageChangeListener(new OnPageChangeListener() {
	
				@Override
				public void onPageSelected(int arg0) {
					for (int i = 0; i < ll.getChildCount(); i++) {
						ImageView iv = (ImageView) ll.getChildAt(i);
						if (i == arg0) {
							iv.setImageResource(R.drawable.point_black);
	
						} else {
							iv.setImageResource(R.drawable.point_white);
	
						}
					}
				}
	
				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
					vp.requestDisallowInterceptTouchEvent(true);
				}
	
				@Override
				public void onPageScrollStateChanged(int arg0) {}
			});
		}else {
			visibility = false;
		}
		
		if (visibility) {
			setVisibility(View.VISIBLE);
		} else {
			setVisibility(View.GONE);

		}
	}
	class MyPagerAdapter extends PagerAdapter {
		List<View> views;
		int currentGV = 1;
		public static final int NUMCOLUMNS = 3;

		public MyPagerAdapter(ArrayList<ContentInfoLeyue> datas) {
			views = new ArrayList<View>();
			initViews(datas);
		}

		private void initViews(ArrayList<ContentInfoLeyue> datas) {
			for (int i = 0; i < pageNum; i++) {
				GridView gv = new GridView(getContext());
				gv.setNumColumns(NUMCOLUMNS);
				
				// 给GridView适配数据
				final ArrayList<ContentInfoLeyue> tempList = new ArrayList<ContentInfoLeyue>();
				for (int j = 0; j < 3; j++) {
					try {
						ContentInfoLeyue tempItem = datas.get((currentGV - 1)
								* 3 + j);
						tempList.add(tempItem);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				currentGV++;
				GVAdapter adapter = new GVAdapter(tempList);
				gv.setAdapter(adapter);
				views.add(gv);
				gv.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						ContentInfoLeyue info=tempList.get(position);
						String bookId = info.getBookId();
//						ContentInfoActivityLeyue.openActivity(getContext(), bookId);
						ActivityChannels.embedContentInfoLeyueActivityToBookCity(getContext(), null, bookId);
					}
				});
			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return views.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView(views.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {

			((ViewPager) container).addView(views.get(position), 0);
			return views.get(position);
		}
	}

	class GVAdapter extends BaseAdapter {
		ArrayList<ContentInfoLeyue> gvItems = new ArrayList<ContentInfoLeyue>();

		public GVAdapter(ArrayList<ContentInfoLeyue> gvItems) {
			this.gvItems = gvItems;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return gvItems.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return gvItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = View.inflate(getContext(),
					R.layout.recommended_related_book_view, null);
			ImageView iv = (ImageView) convertView
					.findViewById(R.id.book_cover_iv);
			TextView tv = (TextView) convertView
					.findViewById(R.id.book_name_tv);
			// iv.setImageURI(Uri.parse();
            if (TextUtils.isEmpty(gvItems.get(position).coverPath)){
                iv.setImageResource(R.drawable.book_default);
            }else{
                ImageLoader mImageLoader = new ImageLoader(getContext());
                mImageLoader.setImageViewBitmap(Constants.BOOKS_TEMP, Constants.BOOKS_TEMP_IMAGE, gvItems.get(position).getCoverPath(), gvItems.get(position).getBookId(), iv, R.drawable.book_default);
            }
			tv.setText(gvItems.get(position).bookName);
			return convertView;
		}

	}

	@Override
	public boolean canScroll(ViewPager viewPager, int dx, int x, int y) {
		// TODO Auto-generated method stub
		return true;
	}

}
