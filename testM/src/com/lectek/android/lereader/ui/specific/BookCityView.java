package com.lectek.android.lereader.ui.specific;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lectek.android.LYReader.R;
import com.lectek.android.binding.widget.BViewPagerTabHost;
import com.lectek.android.lereader.lib.utils.IProguardFilterOnlyPublic;
import com.lectek.android.lereader.ui.IPanelView;
import com.lectek.android.lereader.ui.common.BaseNetPanelView;
import com.lectek.android.widget.BaseViewPagerTabHostAdapter;
import com.lectek.android.widget.BaseViewPagerTabHostAdapter.ItemLifeCycleListener;
import com.lectek.android.widget.ViewPagerTabHost.IOnTabChangedListener;
import com.umeng.analytics.MobclickAgent;
/**
 * 书城
 * @author chendt
 */
public class BookCityView extends BaseNetPanelView implements IProguardFilterOnlyPublic{
	
	public static final String PATH_BOOKCITY_RECOMMEND = "书城推荐";
	public static final String PATH_BOOKCITY_SPECIALOFFER = "书城福利";
	public static final String PATH_BOOKCITY_SALE = "书城排行";
	public static final String PATH_BOOKCITY_CLASSIFY = "书城分类";
	public static final String PATH_BOOKCITY_SEARCH = "书城搜索";
	
	public static String current_path_string = "";
	public static final String TAG = BookCityView.class.getSimpleName();
	protected Activity this_; 
	public BookCityView(Context context) {
		super(context);
		this_ = (Activity) context;
	}
	
	@Override
	public void onCreate() {
		bindView(R.layout.book_city_tab_view, this, this);
		setTitleBarEnabled(false);
		bChildViewPagerTabHostAdapter.setItemLifeCycleListener(new ItemLifeCycleListener() {
			@Override
			public void onDestroy(View view, int position) {
				if(view instanceof IPanelView){
					((IPanelView) view).onDestroy();
				}
			}
			
			@Override
			public boolean onCreate(View view, int position) {
				if(view instanceof IPanelView){
					((IPanelView) view).onCreate();
				}
				return true;
			}
		});
		
		BViewPagerTabHost tabHostView  = (com.lectek.android.binding.widget.BViewPagerTabHost)findViewById(R.id.root_view);
		tabHostView.setOffscreenPageLimit(5);
		Drawable slideBottomLineDrawable = getResources().getDrawable(R.drawable.city_bottom_slide_top_shape);
		tabHostView.slideTabWidgetinitialize(slideBottomLineDrawable);
		tabHostView.setSlideTabWidgetShowAtTop();
		tabHostView.setIndicatorOffsetX(getResources().getDimensionPixelOffset(R.dimen.a_bottom_slide_bg_offset));
		
		tabHostView.setTabChangedListener(new IOnTabChangedListener() {
			
			@Override
			public void onTabChanged(String tabId, String oldTabID) {
				
				if(current_path_string.equals(PATH_BOOKCITY_RECOMMEND)){
					MobclickAgent.onPageEnd(PATH_BOOKCITY_RECOMMEND);
				}else if(current_path_string.equals(PATH_BOOKCITY_CLASSIFY)){
					MobclickAgent.onPageEnd(PATH_BOOKCITY_CLASSIFY);
				}else if(current_path_string.equals(PATH_BOOKCITY_SALE)){
					MobclickAgent.onPageEnd(PATH_BOOKCITY_SALE);
				}else if(current_path_string.equals(PATH_BOOKCITY_SPECIALOFFER)){
					MobclickAgent.onPageEnd(PATH_BOOKCITY_SPECIALOFFER);
				}else if(current_path_string.equals(PATH_BOOKCITY_SEARCH)){
					MobclickAgent.onPageEnd(PATH_BOOKCITY_SEARCH);
				}
				
				if(tabId.equals(TAG_BOOK_A)){
					MobclickAgent.onPageStart(PATH_BOOKCITY_RECOMMEND);
					current_path_string = PATH_BOOKCITY_RECOMMEND;
				}else if(tabId.equals(TAG_BOOK_B)){
					MobclickAgent.onPageStart(PATH_BOOKCITY_CLASSIFY);
					current_path_string = PATH_BOOKCITY_CLASSIFY;
				}else if(tabId.equals(TAG_BOOK_C)){
					MobclickAgent.onPageStart(PATH_BOOKCITY_SALE);
					current_path_string = PATH_BOOKCITY_SALE;
				}else if(tabId.equals(TAG_BOOK_D)){
					MobclickAgent.onPageStart(PATH_BOOKCITY_SPECIALOFFER);
					current_path_string = PATH_BOOKCITY_SPECIALOFFER;
				}else if(tabId.equals(TAG_BOOK_E)){
					MobclickAgent.onPageStart(PATH_BOOKCITY_SEARCH);
					current_path_string = PATH_BOOKCITY_SEARCH;
				}
			}
		});
	}
	
	private static final String TAG_BOOK_A = "TAG_BOOK_A";
	private static final String TAG_BOOK_B = "TAG_BOOK_B";
	private static final String TAG_BOOK_C = "TAG_BOOK_C";
	private static final String TAG_BOOK_D = "TAG_BOOK_D";
	private static final String TAG_BOOK_E = "TAG_BOOK_E";
	
	public final ViewPagerAdapter bChildViewPagerTabHostAdapter = new ViewPagerAdapter();
	
	
	private class ViewPagerAdapter extends BaseViewPagerTabHostAdapter implements IProguardFilterOnlyPublic{
		public ArrayList<String> mTags;
		public ViewPagerAdapter(){
			mTags = new ArrayList<String>();
			mTags.add(TAG_BOOK_A);
			mTags.add(TAG_BOOK_B);
			mTags.add(TAG_BOOK_C);
			mTags.add(TAG_BOOK_D);
			mTags.add(TAG_BOOK_E);
		}
		
		@Override
		public View getIndicator(int position) {
			LinearLayout indicatorView = (LinearLayout) LayoutInflater.
							from(this_).inflate(R.layout.bookcity_bottom_item, null);
			TextView textView = (TextView) indicatorView.findViewById(R.id.textview);
			ImageView imgView = (ImageView) indicatorView.findViewById(R.id.divider_line);
			ImageView img = (ImageView) indicatorView.findViewById(R.id.img);
			if(TAG_BOOK_A.equals(getTab(position))){
				textView.setText(this_.getString(R.string.tab_recommended));
				img.setBackgroundDrawable(this_.getResources().getDrawable(R.drawable.icon_recommend_bg));
			}else if(TAG_BOOK_D.equals(getTab(position))){
				textView.setText(this_.getString(R.string.tab_special_offer));
				img.setBackgroundDrawable(this_.getResources().getDrawable(R.drawable.icon_special_sale_bg));
//				imgView.setVisibility(View.GONE);
			}else if(TAG_BOOK_C.equals(getTab(position))){
				textView.setText(this_.getString(R.string.tab_sale));
				img.setBackgroundDrawable(this_.getResources().getDrawable(R.drawable.icon_ranks_bg));
			}else if(TAG_BOOK_B.equals(getTab(position))){
				textView.setText(this_.getString(R.string.tab_classify));
				img.setBackgroundDrawable(this_.getResources().getDrawable(R.drawable.icon_classify_bg));
			}else if(TAG_BOOK_E.equals(getTab(position))){
				textView.setText(this_.getString(R.string.tab_search));
				img.setBackgroundDrawable(this_.getResources().getDrawable(R.drawable.icon_special_sale_bg));
//				textView.setText("测试");
//				imgView.setVisibility(View.GONE);
			}
			DisplayMetrics dm = new DisplayMetrics();
			this_.getWindowManager().getDefaultDisplay().getMetrics(dm);
			indicatorView.setLayoutParams(new LayoutParams(dm.widthPixels/5,LayoutParams.MATCH_PARENT));
			return indicatorView;
		}

		@Override
		public String getTab(int position) {
			return mTags.get(position);
		}

		@Override
		public int getCount() {
			return mTags.size();
		}

		@Override
		public View getItemView(ViewGroup container, int position) {
			if(TAG_BOOK_A.equals(getTab(position))){
//				container = new BookCityRecommendView(this_);
			}else if(TAG_BOOK_B.equals(getTab(position))){
				container = new BookCityClassifyView(this_);
			}else if(TAG_BOOK_C.equals(getTab(position))){
//				container = new BookCitySaleView(this_);
			}else if(TAG_BOOK_D.equals(getTab(position))){
				container = new BookCitySpecialOfferView(this_);
			}else if(TAG_BOOK_E.equals(getTab(position))){
//				container = new BookCityRecommendView(this_);
			}
			return container;
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
	}
}
