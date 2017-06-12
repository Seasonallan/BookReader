package com.lectek.lereader.core.text.test;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.RecyclerListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.lectek.bookformats.R;
import com.lectek.lereader.core.bookformats.BookInfo;
import com.lectek.lereader.core.bookformats.Catalog;
import com.lectek.lereader.core.util.ContextUtil;
import com.lectek.lereader.core.util.LogUtil;

public class CatalogView extends FrameLayout{
	private static final String TAG = CatalogView.class.getSimpleName();
	private static final String TAG_CATALOG = "TAG_CATALOG";
	private static final String TAG_DIGEST = "TAG_DIGEST";
	private static final String TAG_BOOKMARK = "TAG_BOOKMARK";

	private Activity mContext;
	private TextView mBookNameTV;
	private TextView mAuthorNameTV;
	private ViewPagerTabHost mTabHost;
	private CatalogAdapter catalogAdapter;
	private ViewPagerAdapter mViewPagerAdapter;
	protected ArrayList<Catalog> mCatalogList;
	private ArrayList<String> mTags = new ArrayList<String>();
	private IActionCallBack mCallBack;
	public boolean isShowing = false;
	public boolean isDismissing = false;
	private View mGotoReaderBut;
	
	public CatalogView(Activity context, IActionCallBack actionCallBack) {
		super(context);
		mContext = context;
		mCallBack = new ActionCallBack(actionCallBack);
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.pager_tabs, this, true);
		mBookNameTV = (TextView) findViewById(R.id.catalog_book_name_tv);
		mAuthorNameTV = (TextView) findViewById(R.id.catalog_author_name_tv);
		SlideTabWidget slideTabWidget = (com.lectek.lereader.core.text.test.SlideTabWidget) findViewById(android.R.id.tabs);
		slideTabWidget.initialize(LayoutParams.FILL_PARENT,getResources().getDrawable(R.drawable.ic_reader_catalog_select_bg));
		mTabHost = (ViewPagerTabHost)findViewById(android.R.id.tabhost);
		mTabHost.setBackgroundColor(getResources().getColor(R.color.window_bg));
		mTabHost.setup(); 
		mTabHost.setOffscreenPageLimit(2);
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				fillCatalogView(tabId);
				LogUtil.i(TAG, "onTabChanged ==> tabId="+tabId);
			}
		});
		mCatalogList = new ArrayList<Catalog>();
		catalogAdapter = new CatalogAdapter();
		mGotoReaderBut = findViewById(R.id.left_suspension_but);
		mGotoReaderBut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mCallBack.showReaderContentView();
			}
		});
		mTags.add(TAG_CATALOG);
		mTags.add(TAG_DIGEST);
		mTags.add(TAG_BOOKMARK);
		mViewPagerAdapter = new ViewPagerAdapter(mTags);
		mTabHost.setAdapter(mViewPagerAdapter);
	}
	
	public void setBookInfo(String bookName,String authorName){
		mBookNameTV.setText(bookName);
		mAuthorNameTV.setText(authorName);
	}
	
	public void refreshCatalog(){
		if(catalogAdapter != null){
			catalogAdapter.notifyDataSetInvalidated();
		}
	}
	
	public void setCatalogData(ArrayList<Catalog> catalogs){
		if(catalogs == null){
			return;
		}
		mCatalogList.clear();
		mCatalogList.addAll(catalogs);
		fillCatalogView(mTabHost.getCurrentTabTag());
	}
	
	public void fillCatalogView(String tag){
		fillCatalogView(tag,mTabHost.getTabContentView());
	}
	
	public void fillCatalogView(String tag,View contentView){
		View itemContentView = contentView.findViewWithTag(mViewPagerAdapter.getItemViewTag(mTabHost.getTabIndexByTag(tag)));
		if(itemContentView == null){
			return;
		}
		final ViewHolder viewHolder = (ViewHolder) itemContentView.getTag(R.layout.reader_catalog_tab_item_lay);
		if(tag.equals(TAG_CATALOG)){
			int catalogPosition = mCatalogList.indexOf(mCallBack.getCurrentCatalog());
			if(catalogPosition > -1 && catalogPosition < mCatalogList.size()){
				viewHolder.mListView.setItemChecked(catalogPosition, true);
				viewHolder.mListView.setSelection(catalogPosition);
			}
			viewHolder.mListViewBG.setImageDrawable(null);
			catalogAdapter.notifyDataSetChanged();
		}
	}
	
	private class ViewPagerAdapter extends BaseViewPagerTabHostAdapter{
		private static final String ITEM_VIEW_TAG = "ITEM_VIEW_TAG";
		
		public ArrayList<String> mTags;
		
		public ViewPagerAdapter(ArrayList<String> tags){
			mTags = tags;
			if(mTags == null){
				mTags = new ArrayList<String>();
			}
		}
		
		@Override
		public View getIndicator(int position) {
			View indicatorView = null;
			String tag = getTab(position);
			if(tag.equals(TAG_CATALOG)){
				indicatorView = newIndicator(R.string.btn_text_catalog);
				indicatorView.setBackgroundResource(R.drawable.ic_reader_catalog_item_bg);
			}else if(tag.equals(TAG_DIGEST)){
				indicatorView = newIndicator(R.string.btn_text_bookdigest);
				indicatorView.setBackgroundResource(R.drawable.ic_reader_catalog_item_bg);
			}else if(tag.equals(TAG_BOOKMARK)){
				indicatorView = newIndicator(R.string.btn_text_bookmark);
			}
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
			String tag = getTab(position);
			View contentView = LayoutInflater.from(mContext).inflate(R.layout.reader_catalog_tab_item_lay, null);
			final ListView mListView = (ListView) contentView.findViewById(R.id.reader_catalog_lv);
			final ImageView mListViewBG = (ImageView) contentView.findViewById(R.id.reader_catalog_lv_bg);
			contentView.setTag(getItemViewTag(position));
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.mListView = mListView;
			viewHolder.mListViewBG = mListViewBG;
			viewHolder.mLoadingView = contentView.findViewById(R.id.reader_catalog_loading_lay);
			contentView.setTag(R.layout.reader_catalog_tab_item_lay, viewHolder);
			mListView.setRecyclerListener(new RecyclerListener() {
				@Override
				public void onMovedToScrapHeap(View view) {
					if(view == null){
						return;
					}
					view.destroyDrawingCache();
				}
			});
			OnItemClickListener onItemClickListener = null;
			if(tag.equals(TAG_CATALOG)){
				mListView.setAdapter(catalogAdapter);
				onItemClickListener = new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						int catalogPosition = mCatalogList.indexOf(mCallBack.getCurrentCatalog());
						if(mListView.getCheckedItemPosition() != catalogPosition){
							mListView.setItemChecked(catalogPosition, true);
							mCallBack.selectCatalog(mCatalogList.get(position));
							mCallBack.showReaderContentView();
						}else{
							mCallBack.showReaderContentView();
						}
					}
				};
			}
			mListView.setOnItemClickListener(onItemClickListener);
			return contentView;
		}
		
		public String getItemViewTag(int position){
			return ITEM_VIEW_TAG + "_" + position;
		}
	}
	
	private class ViewHolder {
		public ListView mListView;
		public ImageView mListViewBG;
		public View mLoadingView;
	}
	
	protected View newIndicator(int strResID) {
		TextView tv = (TextView) LayoutInflater.from(mContext).inflate(R.layout.tab_item_with_icon, null);
		tv.setId(android.R.id.title);
		tv.setText(strResID);
		return tv;
	};
	
	private class CatalogAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return mCatalogList.size();
		}

		@Override
		public Object getItem(int position) {
			return mCatalogList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if(convertView == null){
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.reader_catalog_item_leyue, null);
				viewHolder.titileTV = (TextView)convertView.findViewById(R.id.catalog_title_tv);
				viewHolder.titileIndexTV = (TextView)convertView.findViewById(R.id.catalog_title_index_tv);
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			Catalog catalog = (Catalog) getItem(position);
			if(catalog.getPageIndex() != null){
				viewHolder.titileIndexTV.setText(String.valueOf(catalog.getPageIndex()));
			}else{
				viewHolder.titileIndexTV.setText("");
			}
			if(catalog.getBgColor() != null){
				viewHolder.titileTV.setBackgroundColor(catalog.getBgColor());
				viewHolder.titileIndexTV.setBackgroundColor(catalog.getBgColor());
			}else{
				viewHolder.titileTV.setBackgroundDrawable(null);
				viewHolder.titileIndexTV.setBackgroundDrawable(null);
			}
			if(catalog.getTextColor() != null){
				viewHolder.titileTV.setTextColor(catalog.getTextColor());
				viewHolder.titileIndexTV.setTextColor(catalog.getTextColor());
			}else{
				viewHolder.titileTV.setTextColor(getContext().getResources().getColor(R.color.common_black_6));
				viewHolder.titileIndexTV.setTextColor(getContext().getResources().getColor(R.color.common_black_6));
			}
			float textSize = ContextUtil.DIPToPX(11);
			for (int i = 1; i < catalog.getLayer(); i++) {
				textSize *= 0.85f;
			}
			int paddingLeft = ContextUtil.DIPToPX(10) * catalog.getLayer();
			viewHolder.titileTV.setPadding(paddingLeft, convertView.getPaddingTop()
					, convertView.getPaddingRight(), convertView.getPaddingBottom());
			viewHolder.titileTV.setText(catalog.getText());
			viewHolder.titileTV.setTextSize(textSize);
			return convertView;
		}
		
		private class ViewHolder{
			TextView titileTV;
			TextView titileIndexTV;
		}
	}
	
	/**
	 * 对外部操作的回调
	 *
	 */
	private static class ActionCallBack implements IActionCallBack{
		private IActionCallBack mActionCallBack;
		
		public ActionCallBack(IActionCallBack actionCallBack){
			setActionCallBack(actionCallBack);
		}
		
		public void setActionCallBack(IActionCallBack actionCallBack){
			mActionCallBack = actionCallBack;
		}
		
		@Override
		public void reflashCurrentPageBookmark() {
			if(mActionCallBack != null){
				mActionCallBack.reflashCurrentPageBookmark();
			}
		}
		
		@Override
		public Catalog getCurrentCatalog() {
			if(mActionCallBack != null){
				return mActionCallBack.getCurrentCatalog();
			}
			return null;
		}
		
		@Override
		public void showReaderContentView() {
			if(mActionCallBack != null){
				mActionCallBack.showReaderContentView();
			}
		}

		@Override
		public void selectCatalog(Catalog catalog) {
			if(mActionCallBack != null){
				mActionCallBack.selectCatalog(catalog);
			}
		}
		
		@Override
		public boolean isTextSelectHandlEenabled() {
			if(mActionCallBack != null){
				return mActionCallBack.isTextSelectHandlEenabled();
			}
			return false;
		}
		
		@Override
		public boolean isHasNetWork() {
			if(mActionCallBack != null){
				return mActionCallBack.isHasNetWork();
			}
			return false;
		}

		@Override
		public BookInfo getBookInfo() {
			if(mActionCallBack != null){
				return mActionCallBack.getBookInfo();
			}
			return null;
		}

		@Override
		public void onEditModeChange(boolean isEdit) {
			if(mActionCallBack != null){
				mActionCallBack.onEditModeChange(isEdit);
			}
		}
	}
	
	public interface IActionCallBack{
		public void reflashCurrentPageBookmark();
		
		public Catalog getCurrentCatalog();

		public void showReaderContentView();

		public void selectCatalog(Catalog catalog);
		
		public boolean isTextSelectHandlEenabled();
		
		public boolean isHasNetWork();
		
		public BookInfo getBookInfo();
		
		public void onEditModeChange(boolean isEdit);
	}
}
