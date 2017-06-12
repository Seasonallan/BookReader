package com.lectek.android.lereader.ui.specific;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.lib.utils.IProguardFilterOnlyPublic;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.ui.IPanelView;
import com.lectek.android.lereader.ui.common.BaseActivity;
import com.lectek.android.lereader.widgets.SlidingView.ISlideGuestureHandler;
import com.lectek.android.widget.BaseViewPagerTabHostAdapter;
import com.lectek.android.widget.BaseViewPagerTabHostAdapter.ItemLifeCycleListener;
import com.lectek.android.widget.ViewPagerTabHost;
import com.lectek.android.widget.ViewPagerTabHost.IOnTabChangedListener;
import com.lectek.android.widget.ViewPagerTabHost.ScrollChild;
import com.umeng.analytics.MobclickAgent;

/**
 * 书城主界面
 * @author Administrator
 *
 */
@SuppressWarnings("deprecation")
public class BookCityActivityGroup extends ActivityGroup implements ISlideGuestureHandler,ScrollChild{
	public static final String TAG = BookCityActivityGroup.class.getSimpleName();
	
	public static final String Extra_Embed_Activity_ID = "Extra_Embed_Activity_ID";
	public static final String Extra_Embed_Activity_Extra = "Extra_Embed_Activity_Extra";
	public static final String Extra_Remove_Embed_Activity = "Extra_Remove_Embed_Activity";
	public static final String Extra_Embed_Activity_Hide_Title_Bar = "Extra_Remove_Embed_Activity";
	public static final String Extra_Embed_Activity_Title = "Extra_Embed_Activity_Title";
	
	public static final String PATH_BOOKCITY_RECOMMEND = "书城推荐";
	public static final String PATH_BOOKCITY_CLASSIFY = "书城分类";
	public static final String PATH_BOOKCITY_SALE = "书城榜单";
	public static final String PATH_BOOKCITY_SPECIALOFFER = "书城特价";
	public static final String PATH_BOOKCITY_SEARCH = "书城搜索";
	
	public static String current_path_string = "";
	
	/**
	 * 书城推荐界面
	 */
	public static final String TAB_RECOMMEND = "BookCityRecommend";
	/**
	 * 书城推荐界面
	 */
	private static final String TAB_SPECIAL_OFFER = "BookCitySpecialOffer";
	/**
	 * 书城排行榜界面 
	 */
	private static final String TAB_SALE = "BookCitySale";
	/**
	 * 书城分类界面
	 */
	private static final String TAB_CLASSIFY = "BookCityClassify";
	/**
	 * 书城搜索界面
	 */
	private static final String TAB_SEARCH = "BookCitySearch";
	
	
	/**
	 * 嵌入的书城的书籍详情界面
	 */
	public static final String TAB_Embed_CONTENTINFO = "BookCityContentInfoLeyue";
	
	
	/**
	 * 嵌入的书城的专题详情界面
	 */
	public static final String TAB_Embed_SUBJECT_DETAIL = "BookCitySubjectDetail";
	
	/**
	 * 嵌入的书城的推荐二级界面
	 */
	public static final String TAB_Embed_BOOK_RECOMMEND_LIST = "BookCityBookRecommendList";
	
	/**
	 * 嵌入的书城的分类二级界面
	 */
	public static final String TAB_Embed_CLASSIFY_DETAIL = "BookCityClassifyDetail";
	
	//登记要嵌入的Activity
	private static HashMap<String, Class<?>> tagMapActivity = new HashMap<String, Class<?>>();
	static {
		tagMapActivity.put(TAB_Embed_CONTENTINFO, ContentInfoActivityLeyue.class);
		tagMapActivity.put(TAB_Embed_SUBJECT_DETAIL, BookCitySubjectActivity.class);
		tagMapActivity.put(TAB_Embed_BOOK_RECOMMEND_LIST, BookCityBookListActivity.class);
		tagMapActivity.put(TAB_Embed_CLASSIFY_DETAIL, BookCityClassifyDetailActivity.class);
	}
	private String currentActivityId;
	private ViewPagerTabHost mTabHost;
	private TextView titleView;
	private TextView bookshelf_btn;
	private View goBackBtn;
	private LinkedList<ActivityIdItem> mActivityIdList = new LinkedList<ActivityIdItem>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.e(TAG, "onCreate");
		
		setContentView(R.layout.book_city_tab_layout);
		goBackBtn = findViewById(R.id.go_back_btn);
		goBackBtn.setOnClickListener(backBtnClickListener);
		
		titleView = (TextView) findViewById(R.id.title_text);
		
		bookshelf_btn = (TextView) goBackBtn.findViewById(R.id.bookshelf_tip);
        SlideActivityGroup.PageController.getmInstance().setmListener(new SlideActivityGroup.IGotoFuliPage() {
            @Override
            public void onGoto(int page) {
                if (mTabHost != null){
                    mTabHost.setCurrentTab(page);
                }
            }
        });
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.e(TAG, "onDestroy");
	}

    @Override
	protected void onStart() {
		super.onStart();
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {

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
				
				mTabHost = (ViewPagerTabHost)findViewById(R.id.root_view);
				mTabHost.setIsShowAnimation(false);
				mTabHost.setup();
				mTabHost.setOffscreenPageLimit(0);
				Drawable slideBottomLineDrawable = getResources().getDrawable(R.color.transparent);
				mTabHost.slideTabWidgetinitialize(slideBottomLineDrawable);
				mTabHost.setSlideTabWidgetShowAtTop();
				mTabHost.setIndicatorOffsetX(getResources().getDimensionPixelOffset(R.dimen.a_bottom_slide_bg_offset));
				mTabHost.setAdapter(bChildViewPagerTabHostAdapter);
				mTabHost.setTabChangedListener(new IOnTabChangedListener() {
					
					@Override
					public void onTabChanged(String tabId, String oldID) {
						setTitleText(tabId);
						
						if(oldID != null && getLocalActivityManager().getActivity(oldID) != null) {
							if(getLocalActivityManager().getActivity(oldID) instanceof BookCitySaleActivity) {
								((BookCitySaleActivity)getLocalActivityManager().getActivity(oldID)).onPause();
							}else if(getLocalActivityManager().getActivity(oldID) instanceof BookCitySearchActivity) {
								((BookCitySearchActivity)getLocalActivityManager().getActivity(oldID)).onPause();
							} else if(getLocalActivityManager().getActivity(oldID) instanceof BookCityBaseActivity){
								((BookCityBaseActivity)getLocalActivityManager().getActivity(oldID)).onPause();
							}
						}
						
                        setCurrentTag(tabId);
                        
                        if(getLocalActivityManager().getActivity(tabId) != null) {
                        	if(getLocalActivityManager().getActivity(tabId) instanceof BookCitySaleActivity) {
								((BookCitySaleActivity)getLocalActivityManager().getActivity(tabId)).onResume();
							}else if(getLocalActivityManager().getActivity(tabId) instanceof BookCitySearchActivity) {
								((BookCitySearchActivity)getLocalActivityManager().getActivity(tabId)).onResume();
							} else if(getLocalActivityManager().getActivity(tabId) instanceof BookCityBaseActivity){
								((BookCityBaseActivity)getLocalActivityManager().getActivity(tabId)).onResume();
							}
                        }
//						removeEmbedActivity(getCurrentTag());
						removeAllEmbedActivity();
						
						LogUtil.e(">>>>>>>>>>>>>>>>>>>>>>>>>setOnTabChangedListener");
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
						
						if(tabId.equals(TAB_RECOMMEND)){
							MobclickAgent.onPageStart(PATH_BOOKCITY_RECOMMEND);
							current_path_string = PATH_BOOKCITY_RECOMMEND;
						}else if(tabId.equals(TAB_SPECIAL_OFFER)){
							MobclickAgent.onPageStart(PATH_BOOKCITY_SPECIALOFFER);
							current_path_string = PATH_BOOKCITY_SPECIALOFFER;
						}else if(tabId.equals(TAB_SALE)){
							MobclickAgent.onPageStart(PATH_BOOKCITY_SALE);
							current_path_string = PATH_BOOKCITY_SALE;
						}else if(tabId.equals(TAB_CLASSIFY)){
							MobclickAgent.onPageStart(PATH_BOOKCITY_CLASSIFY);
							current_path_string = PATH_BOOKCITY_CLASSIFY;
						}else if(tabId.equals(TAB_SEARCH)){
							MobclickAgent.onPageStart(PATH_BOOKCITY_SEARCH);
							current_path_string = PATH_BOOKCITY_SEARCH;
						}
					}
				});
				
			}
		});
	};
		
		private OnClickListener backBtnClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(!removeEmbedActivity(getCurrentTag())) {
					Intent intent = new Intent(v.getContext(), SlideActivityGroup.class);
					intent.putExtra(SlideActivityGroup.Extra_Switch_UI, SlideActivityGroup.BOOK_SHELF);
					v.getContext().startActivity(intent);
				}
			}
		};
		
		private void setTitleText(String tabId){
			
			if(tabId.equals(TAB_RECOMMEND)){
				titleView.setText(R.string.bookcity_title_recommend);
			}else if(tabId.equals(TAB_SPECIAL_OFFER)){
				titleView.setText(R.string.bookcity_title_fuli);
			}else if(tabId.equals(TAB_SALE)){
				titleView.setText(R.string.bookcity_title_paihang);
			}else if(tabId.equals(TAB_CLASSIFY)){
				titleView.setText(R.string.bookcity_title_classify);
			}else if (tabId.equals(TAB_SEARCH)){
				titleView.setText(R.string.main_booksearch_btn);
			}			
		}
		
		public final ViewPagerAdapter bChildViewPagerTabHostAdapter = new ViewPagerAdapter();
		
		
		private class ViewPagerAdapter extends BaseViewPagerTabHostAdapter implements IProguardFilterOnlyPublic{
			public ArrayList<String> mTags;
			public ViewPagerAdapter(){
				mTags = new ArrayList<String>();
				mTags.add(TAB_RECOMMEND);
				mTags.add(TAB_SPECIAL_OFFER);
				mTags.add(TAB_SALE);
				mTags.add(TAB_CLASSIFY);
				mTags.add(TAB_SEARCH);
			}
			
			@Override
			public View getIndicator(int position) {
				LinearLayout indicatorView = (LinearLayout) LayoutInflater.
								from(getBaseContext()).inflate(R.layout.bookcity_bottom_item, null);
				TextView textView = (TextView) indicatorView.findViewById(R.id.textview);
				ImageView imgView = (ImageView) indicatorView.findViewById(R.id.divider_line);
				imgView.setVisibility(View.GONE);
				ImageView img = (ImageView) indicatorView.findViewById(R.id.img);
				if(TAB_RECOMMEND.equals(getTab(position))){
					textView.setText(getString(R.string.tab_recommended));
					img.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_recommend_bg));
				}else if(TAB_SPECIAL_OFFER.equals(getTab(position))){
					textView.setText(getString(R.string.tab_special_offer));
					img.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_special_sale_bg));
				}else if(TAB_SALE.equals(getTab(position))){
					textView.setText(getString(R.string.tab_sale));
					img.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_ranks_bg));
				}else if(TAB_CLASSIFY.equals(getTab(position))){
					textView.setText(getString(R.string.tab_classify));
					img.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_classify_bg));
				}else if(TAB_SEARCH.equals(getTab(position))){
					textView.setText(getString(R.string.tab_search));
					img.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_search_tag_bg));
				}

				DisplayMetrics dm = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(dm);
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
				if(TAB_RECOMMEND.equals(getTab(position))){
					if(getLocalActivityManager().getActivity(TAB_RECOMMEND) == null) {
						container = (ViewGroup)getView(BookCityRecommendActivity.class, TAB_RECOMMEND);
					}else {
						container = (ViewGroup)getLocalActivityManager().getActivity(TAB_RECOMMEND).getWindow().getDecorView();
					}
					
				}else if(TAB_SPECIAL_OFFER.equals(getTab(position))){
					if(getLocalActivityManager().getActivity(TAB_SPECIAL_OFFER) == null) {
						container = (ViewGroup)getView(BookCitySpecialOfferActivity.class, TAB_SPECIAL_OFFER);
					}else {
						container = (ViewGroup)getLocalActivityManager().getActivity(TAB_SPECIAL_OFFER).getWindow().getDecorView();;
					}
				}else if(TAB_SALE.equals(getTab(position))){
					if(getLocalActivityManager().getActivity(TAB_SALE) == null) {
						container = (ViewGroup)getView(BookCitySaleActivity.class, TAB_SALE);
					}else {
						container = (ViewGroup)getLocalActivityManager().getActivity(TAB_SALE).getWindow().getDecorView();;
					}
				}else if(TAB_CLASSIFY.equals(getTab(position))){
					if(getLocalActivityManager().getActivity(TAB_CLASSIFY) == null) {
						container = (ViewGroup)getView(BookCityClassifyActivity.class, TAB_CLASSIFY);
					}else {
						container = (ViewGroup)getLocalActivityManager().getActivity(TAB_CLASSIFY).getWindow().getDecorView();;
					}
					
				}else if(TAB_SEARCH.equals(getTab(position))){
					if(getLocalActivityManager().getActivity(TAB_SEARCH) == null) {
						container = (ViewGroup)getView(BookCitySearchActivity.class, TAB_SEARCH); 
					}else {
						container = (ViewGroup)getLocalActivityManager().getActivity(TAB_SEARCH).getWindow().getDecorView();;
					}
				}
				return container;
			}
		}
	private View getView(Class<?> tClass, String id){
		Intent intent = new Intent(this, tClass);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		Window subActivity = getLocalActivityManager().startActivity(id, intent);
		return subActivity.getDecorView();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		if(intent.hasExtra(Extra_Embed_Activity_ID)) {
			String extraId = intent.getStringExtra(Extra_Embed_Activity_ID);
			if(intent.getBooleanExtra(Extra_Remove_Embed_Activity, false)) {
				removeEmbedActivity(extraId);
			}else if(tagMapActivity.get(extraId) != null){
				Intent newIntent = new Intent(this, tagMapActivity.get(extraId));
				if(intent.hasExtra(Extra_Embed_Activity_Extra)) {
					newIntent.putExtras(intent.getBundleExtra(Extra_Embed_Activity_Extra));
				}
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				
				View decorView = getLocalActivityManager().startActivity(extraId, newIntent).getDecorView();
				
				if(decorView != null) {
					
					//activityId进栈
					if(!(getTopActivityId() != null && getTopActivityId().activityId.equals(extraId))){
						ActivityIdItem item = new ActivityIdItem();
						item.activityId = extraId;
						item.activitytitle = intent.getStringExtra(Extra_Embed_Activity_Title);
						pushActivityIdList(item);
					}
					
					setCurrentTag(extraId);
					bookshelf_btn.setVisibility(View.INVISIBLE);
					if(mTabHost.getTabContentView().equals(decorView.getParent())){
						decorView.bringToFront();
					}else {
						titleView.setText(intent.getStringExtra(Extra_Embed_Activity_Title));
						mTabHost.getTabContentView().addView(decorView);
					}
				}
			}
			
			return;
		}
		
		super.onNewIntent(intent);
	}

	
	private String getCurrentTag() {
		return currentActivityId;
	}
	
	private void setCurrentTag(String tag) {
		currentActivityId = tag;
	}
	
	private void removeAllEmbedActivity(){
		while(getTopActivityId() != null){
			String tabId = popActivityIdList().activityId;
			if(!TextUtils.isEmpty(tabId)){
				removeEmbedActivity(tabId);
			}
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getLocalActivityManager().dispatchResume();
	}
	
	
	@Override
	public void onBackPressed() {
		goBackBtn.performClick();
	}

	private boolean removeEmbedActivity(String tabID) {
//		setTitleText(mTabHost.getCurrentTabTag());
		if(getTopActivityId() != null && getTopActivityId().activityId != null && getTopActivityId().activityId.equals(tabID)){
			popActivityIdList();			
		}
		if(tagMapActivity.containsKey(tabID) && getLocalActivityManager().getActivity(tabID) != null) {
//			View view = getLocalActivityManager().getActivity(tabID).getWindow().getDecorView();
			mTabHost.getTabContentView().removeView(getLocalActivityManager().getActivity(tabID).getWindow().getDecorView());
			getLocalActivityManager().destroyActivity(tabID, true);

			if(Build.VERSION.SDK_INT < 11) {
				removeEmbedActivitySpecial(tabID);
			}

			if(getTopActivityId() == null){
				setTitleText(mTabHost.getCurrentTabTag());
                setCurrentTag(mTabHost.getCurrentTabTag());
				bookshelf_btn.setVisibility(View.VISIBLE);
			}else{
				View view = null;
				if(getLocalActivityManager().getActivity(getTopActivityId().activityId) != null
						&& getLocalActivityManager().getActivity(getTopActivityId().activityId).getWindow() != null){
					view = getLocalActivityManager().getActivity(getTopActivityId().activityId).getWindow().getDecorView();
				}
				if(view != null){
					setCurrentTag(getTopActivityId().activityId);
					bookshelf_btn.setVisibility(View.INVISIBLE);
					titleView.setText(getTopActivityId().activitytitle);
					if(mTabHost.getTabContentView().equals(view.getParent())){
						view.bringToFront();
					}else {
						mTabHost.getTabContentView().addView(view);
					}
				}
			}
			return true;
		}
		
		return false;
	}
	
	private boolean removeEmbedActivitySpecial(String tabID) {
		try {  
			final Field mActivitiesField = getLocalActivityManager().getClass().getDeclaredField("mActivities");  
			if(mActivitiesField != null){  
				mActivitiesField.setAccessible(true);  
				@SuppressWarnings("unchecked")  
				final Map<String, Object> mActivities = (Map<String, Object>)mActivitiesField.get(getLocalActivityManager());  
				if(mActivities != null){  
					mActivities.remove(tabID);  
				}  
			}  
			return true;
		} catch (Exception e) {  
			e.printStackTrace();  
		}
		return false;
	}
	
	private void pushActivityIdList(ActivityIdItem currentitem){
		mActivityIdList.addFirst(currentitem);
	}
	
	private ActivityIdItem getTopActivityId(){
		if(mActivityIdList.size() > 0){
			return mActivityIdList.getFirst();
		}
		return null;
	}
	
	private ActivityIdItem popActivityIdList(){
		if(mActivityIdList.size() > 0){
			return mActivityIdList.removeFirst();
		}
		return null;
	}
	
//	private void clearActivityIdList(){
//		mActivityIdList.clear();
//	}
//	
	
	@Override
	public boolean consumeHorizontalSlide(float x, float y, float deltaX) {
        Activity activity = getLocalActivityManager().getActivity(getCurrentTag());
		if(activity instanceof ISlideGuestureHandler) {
			return ((ISlideGuestureHandler)activity).consumeHorizontalSlide(x, y, deltaX);
		}
		return false;
	}

	@Override
	public boolean isChildScroll(MotionEvent ev) {
		if(getLocalActivityManager().getActivity(getCurrentTag()) instanceof ScrollChild) {
			return ((ScrollChild)getLocalActivityManager().getActivity(getCurrentTag())).isChildScroll(ev);
		}
		return false;
	}
	
	public class ActivityIdItem{
		String activityId = "";
		String activitytitle = "";
	}
}
