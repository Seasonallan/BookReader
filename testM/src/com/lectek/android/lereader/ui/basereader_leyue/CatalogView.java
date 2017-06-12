package com.lectek.android.lereader.ui.basereader_leyue;

import java.util.ArrayList;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView.RecyclerListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lectek.android.LYReader.R;
import com.lectek.android.LYReader.dialog.LeYueDialog;
import com.lectek.android.lereader.lib.utils.DimensionsUtil;
import com.lectek.android.lereader.storage.dbase.digest.BookDigests;
import com.lectek.android.lereader.storage.dbase.mark.BookMark;
import com.lectek.android.lereader.ui.basereader_leyue.bookmarks.BookMarkDatas;
import com.lectek.android.lereader.ui.basereader_leyue.bookmarks.BookmarkItemAdapter;
import com.lectek.android.lereader.ui.basereader_leyue.digests.AbsTextSelectHandler;
import com.lectek.android.lereader.ui.basereader_leyue.digests.BookDigestsItemAdapter;
import com.lectek.android.lereader.ui.basereader_leyue.view.IReaderView;
import com.lectek.android.lereader.ui.common.BasePanelView;
import com.lectek.android.lereader.utils.ToastUtil;
import com.lectek.android.widget.BaseViewPagerTabHostAdapter;
import com.lectek.android.widget.ViewPagerTabHost;
import com.lectek.android.widget.ViewPagerTabHost.IOnTabChangedListener;
import com.lectek.lereader.core.bookformats.Catalog;

/**
 * 目录视图
 * @author ljp 2014年4月22日修正
 */
public class CatalogView extends BasePanelView implements ReadSetting.SettingListener {
	private static final String TAG = CatalogView.class.getSimpleName();
	private static final String TAG_CATALOG = "TAG_CATALOG";
	private static final String TAG_BOOKMARK = "TAG_BOOKMARK";
	private static final String TAG_BOOKDIGEST = "TAG_BOOKDIGEST";

	private Activity mContext;
	private ViewPagerTabHost mTabHost;
	private CatalogAdapter catalogAdapter;
	private ViewPagerAdapter mViewPagerAdapter;
	private BookmarkItemAdapter mBookmarkItemAdapter;
    private BookDigestsItemAdapter bookDigestsItemAdapter;

	protected ArrayList<Catalog> mCatalogList;
	private ArrayList<String> mTags = new ArrayList<String>();
	private boolean isShowing = false;
    private boolean isDismissing = false;
	private View mGotoReaderBut;
    public boolean mIsOrder = false;
	private int feeIndex = -1;
    private IReaderView mReaderView;
    private View[] mTabTextView;
    private int mLastTheme;
    private View mContainer;
    /**
     * 设置目录收费起始点
     * @param index
     */
    public void setFeeIndex(int index, boolean isOrder){
        this.feeIndex = index;
        this.mIsOrder = isOrder;
        if(catalogAdapter != null){
            catalogAdapter.notifyDataSetChanged();
        }
    }
    /**
     * 获取目录收费起始点
     */
    public int getFeeIndex(){
        return this.feeIndex;
    }

	public CatalogView(Activity context, IReaderView readerView, int feeIndex, boolean isOrder) {
		super(context);
        this.mContext = context;
        this.mReaderView = readerView;
		this.feeIndex = feeIndex;
        this.mIsOrder = isOrder;
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.pager_tabs, this, true);

        mContainer = findViewById(R.id.container);
      /*  SlideTabWidget slideTabWidget = (SlideTabWidget)findViewById(android.R.id.tabs);
        Drawable slideBottomLineDrawable = getResources().getDrawable(R.drawable.city_top_slide_bottom_shape);
        slideTabWidget.initialize(slideBottomLineDrawable);
        slideTabWidget.setIndicatorOffsetY(getResources().getDimensionPixelOffset(R.dimen.a_top_slide_bg_height));*/
        
		mTabHost = (ViewPagerTabHost)findViewById(android.R.id.tabhost);
		mTabHost.setBackgroundColor(getResources().getColor(R.color.window_bg));
		mTabHost.setup();
		mTabHost.setOffscreenPageLimit(2);
		mTabHost.setTabChangedListener(new IOnTabChangedListener() {
			
			@Override
			public void onTabChanged(String newTabID, String oldTabID) {
				int position = mTabHost.getTabIndexByTag(newTabID);
				for (int i =0 ;i < mTabTextView.length; i++){
                  mTabTextView[i].setSelected(position == i);
				}
				
				fillCatalogView(newTabID);
			}
		});

		mCatalogList = new ArrayList<Catalog>();
		catalogAdapter = new CatalogAdapter();
		mGotoReaderBut = findViewById(R.id.left_suspension_but);
		mGotoReaderBut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                dismissCatalogView();
			}
		});
        if(mReaderView.getTextSelectHandler() != null){//epub书籍显示书签笔记
            mTags.add(TAG_CATALOG);
            mTags.add(TAG_BOOKMARK);
            mTags.add(TAG_BOOKDIGEST);
        }else{
            mTags.add(TAG_CATALOG);
        }
        mTabTextView = new View[mTags.size()];
		mViewPagerAdapter = new ViewPagerAdapter(mTags);
		mTabHost.setAdapter(mViewPagerAdapter);
        mLastTheme = ReadSetting.getInstance(context).getThemeType();
        ReadSetting.getInstance(context).addDataListeners(this);
        changeStyle(mLastTheme == ReadSetting.THEME_TYPE_NIGHT);
	}

    private PopupWindow.OnDismissListener mListener;
    /**
     * 设置目录隐藏监听器
     */
    public void setOnDismissListener(PopupWindow.OnDismissListener listener){
        this.mListener = listener;
    }

    /**
     * 隐藏目录视图
     */
    public void dismissCatalogView(){
        mReaderView.onShowContentView();
        if (isDismissing == false) {
            isDismissing = true;
            setVisibility(View.INVISIBLE);
            Animation trans1 = new TranslateAnimation(Animation.ABSOLUTE,
                    0.0f, Animation.ABSOLUTE, - getResources().getDisplayMetrics().widthPixels,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f);
            trans1.setDuration(350);
            trans1.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationRepeat(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    if(mListener != null){
                        mListener.onDismiss();
                    }
                    isDismissing = false;
                    isShowing = false;
                }
            });
            startAnimation(trans1);
        }
    }
    /**
     * 弹出目录视图
     * @param parentView
     */
    public void showReaderCatalogView(ViewGroup parentView) {
        if (isShowing == false) {
            if (getParent() == null) {
                parentView.addView(this);
            }
            mReaderView.onHideContentView();
            setVisibility(View.VISIBLE);
            isShowing = true;
            parentView.setVisibility(View.VISIBLE);
            setCatalogData(mReaderView.getChapterList());
            Animation trans1 = new TranslateAnimation(
                    Animation.ABSOLUTE, -getResources().getDisplayMetrics().widthPixels, Animation.ABSOLUTE,
                    0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f);
            trans1.setDuration(600);
            trans1.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationRepeat(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    setAnimation(null);
                    isShowing = false;
                    isDismissing = false;
                }
            });
            startAnimation(trans1);
        }
    }

    /**
     * 设置目录数据结构
     */
    public void setCatalogData(ArrayList<Catalog> catalogs){
		if(catalogs == null){
			return;
		}
		mCatalogList.clear();
		mCatalogList.addAll(catalogs);
		fillCatalogView(mTabHost.getCurrentTabTag());
	}

	public void refreshCatalog(){
		if(catalogAdapter != null){
			catalogAdapter.notifyDataSetInvalidated();
		}
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
			int catalogPosition = mCatalogList.indexOf(mReaderView.getCurrentCatalog());
			if(catalogPosition > -1 && catalogPosition < mCatalogList.size() && catalogAdapter != null){
                catalogAdapter.setSelection(catalogPosition);
                viewHolder.mListView.setSelection(catalogPosition);
			}
			viewHolder.mListViewBG.setImageDrawable(null);
			catalogAdapter.notifyDataSetChanged();
		}else if(tag.equals(TAG_BOOKMARK)){
			viewHolder.mListView.setAdapter(null);
			//viewHolder.mLoadingView.setVisibility(View.VISIBLE);
//			viewHolder.mLoadingView.setVisibility(View.GONE);
			mBookmarkItemAdapter = new BookmarkItemAdapter(mContext,
                    (ArrayList<BookMark>) BookMarkDatas.getInstance().getBookMarks());
			viewHolder.mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
			viewHolder.mListView.setAdapter(mBookmarkItemAdapter);
			if(mBookmarkItemAdapter.getCount() > 0){
				viewHolder.mListViewBG.setImageDrawable(null);
			}else{
				viewHolder.mListViewBG.setImageResource(R.drawable.no_book_mark_sign_bg);
			}
            viewHolder.mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
            viewHolder.mListView.setAdapter(mBookmarkItemAdapter);
            mBookmarkItemAdapter.notifyDataSetChanged();
		}else{
            AbsTextSelectHandler handler = mReaderView.getTextSelectHandler();
            bookDigestsItemAdapter = new BookDigestsItemAdapter(mContext);
            if(handler != null){
                bookDigestsItemAdapter.setData(handler.getBookDigestsData());
            }
            bookDigestsItemAdapter.setCatalogMsg(mCatalogList);
            if(bookDigestsItemAdapter.getCount() > 0){
                viewHolder.mListViewBG.setImageDrawable(null);
            }else{
                viewHolder.mListViewBG.setImageResource(R.drawable.no_digest_bg);
            }
            viewHolder.mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
            viewHolder.mListView.setAdapter(bookDigestsItemAdapter);
            bookDigestsItemAdapter.notifyDataSetChanged();
        }

	}

    @Override
    public void onSettingChange(ReadSetting readSetting, String type) {
        if(type == ReadSetting.SETTING_TYPE_THEME){
            if (mLastTheme != readSetting.getThemeType()){
                if (mLastTheme == ReadSetting.THEME_TYPE_NIGHT && ReadSetting.THEME_TYPE_NIGHT != readSetting.getThemeType()
                        || (mLastTheme != ReadSetting.THEME_TYPE_NIGHT && ReadSetting.THEME_TYPE_NIGHT == readSetting.getThemeType())){
                    mLastTheme =  readSetting.getThemeType();
                    changeStyle(mLastTheme == ReadSetting.THEME_TYPE_NIGHT);
                }
            }
        }
    }

    private void changeStyle(boolean isNightMode){
        mContainer.setBackgroundResource(isNightMode? R.color.catalog_night_bg: R.color.catalog_day_bg);
        if (catalogAdapter != null){
            catalogAdapter.notifyDataSetChanged();
        }
        if (bookDigestsItemAdapter != null){
            bookDigestsItemAdapter.notifyDataSetChanged();
        }
        if (mBookmarkItemAdapter != null){
            mBookmarkItemAdapter.notifyDataSetChanged();
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
			String tag = getTab(position);
			if(tag.equals(TAG_CATALOG)){
                mTabTextView[0] = newIndicator(R.string.btn_text_catalog, 0);
                return mTabTextView[0];
			}else if(tag.equals(TAG_BOOKMARK)){
                mTabTextView[1] = newIndicator(R.string.btn_text_bookmark,1);
                return mTabTextView[1];
			}else if(tag.equals(TAG_BOOKDIGEST)){
                mTabTextView[2] = newIndicator(R.string.btn_text_bookdigest,2);
                return mTabTextView[2];
			}
			return null;
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
                if(catalogAdapter.getCount() > 20 && mListView.getFooterViewsCount() == 0){
                    View footerView = LayoutInflater.from(getContext()).inflate(R.layout.reader_catalog_item_leyue, null);
                    TextView textView = (TextView) footerView.findViewById(R.id.catalog_title_index_tv);
                    textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    textView.setGravity(Gravity.CENTER);
                    textView.setText(R.string.no_more);
                    mListView.addFooterView(footerView);
                }
				mListView.setAdapter(catalogAdapter);
				onItemClickListener = new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
                        if (position >= mCatalogList.size()){
                            return;
                        }
						int catalogPosition = mCatalogList.indexOf(mReaderView.getCurrentCatalog());
						if(mListView.getCheckedItemPosition() != catalogPosition){
							mListView.setItemChecked(catalogPosition, true);
                            mReaderView.gotoChapter(mCatalogList.get(position), true);
                            dismissCatalogView();
						}else{
                            dismissCatalogView();
						}
					}
				};
			}else if(tag.equals(TAG_BOOKMARK)){
				onItemClickListener = new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
							BookMark bookmark = (BookMark) parent.getItemAtPosition(position);
							if(bookmark != null){
                                selectBookMarkCallback(bookmark);
                            }
                    }
				};
				mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                        showLabelDialog(i);
                        return true;
                    }
                });
			}else if(tag.equals(TAG_BOOKDIGEST)){
                onItemClickListener = new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                          selectBookDigest(position);
                    }
                };
                mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                        showDigestDialog(i);
                        return true;
                    }
                });
			}
			mListView.setOnItemClickListener(onItemClickListener);
			return contentView;
		}

		public String getItemViewTag(int position){
			return ITEM_VIEW_TAG + "_" + position;
		}
	}

    private void selectBookDigest(int position){
        BookDigests bookDigests = bookDigestsItemAdapter.getItem(position);
        BookMark bookmark = new BookMark();
        bookmark.setChapterID(bookDigests.getChaptersId());
        int p = bookDigests.getPosition4Txt();
        if (p == -1){
            p = bookDigests.getPosition();
        }
        bookmark.setPosition(p);
        selectBookMarkCallback(bookmark);
    }

    private void selectBookMarkCallback(BookMark bookmark){
        mReaderView.gotoBookmark(bookmark, true);
        dismissCatalogView();
    }

    /**
     * 显示笔记删除对话框
     */
    private void showDigestDialog(final int position){
        final LeYueDialog dialog = new LeYueDialog(mContext);
        View view = View.inflate(mContext, R.layout.book_label_dialog_layout, null);
        dialog.setContentLay(view);
        dialog.setNoButton();
        dialog.setCancelable(true);
        TextView gotoLabelView = (TextView) view.findViewById(R.id.tv_goto_label);
        TextView delLabelView = (TextView) view.findViewById(R.id.tv_del_label);
        gotoLabelView.setText(R.string.book_digest_goto_position);
        delLabelView.setText(R.string.book_digest_delete_label);

        gotoLabelView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                selectBookDigest(position);
                dialog.cancel();
            }
        });
        delLabelView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mReaderView.getTextSelectHandler().deleteBookDigests(bookDigestsItemAdapter.remove(position));
                bookDigestsItemAdapter.notifyDataSetChanged();
                ToastUtil.showToast(mContext, R.string.book_digest_del_success);
                showEmpty();
                dialog.cancel();
            }
        });
        dialog.show();
    }



    private void showEmpty(){
        if(bookDigestsItemAdapter.getCount() == 0){
            View itemContentView = mTabHost.getTabContentView()
                    .findViewWithTag(mViewPagerAdapter.getItemViewTag(mTabHost.getTabIndexByTag(mTabHost.getCurrentTabTag())));
            if(itemContentView == null){
                return;
            }
            final ViewHolder viewHolder = (ViewHolder) itemContentView.getTag(R.layout.reader_catalog_tab_item_lay);
            viewHolder.mListViewBG.setImageResource(R.drawable.no_digest_bg);
        }

    }

    private void showBookMarkEmpty(){
        if(mBookmarkItemAdapter.getCount() == 0){
            View itemContentView = mTabHost.getTabContentView()
                    .findViewWithTag(mViewPagerAdapter.getItemViewTag(mTabHost.getTabIndexByTag(mTabHost.getCurrentTabTag())));
            if(itemContentView == null){
                return;
            }
            final ViewHolder viewHolder = (ViewHolder) itemContentView.getTag(R.layout.reader_catalog_tab_item_lay);
            viewHolder.mListViewBG.setImageResource(R.drawable.no_book_mark_sign_bg);
        }
    }

    /**
     * 显示书签对话框
     */
    private void showLabelDialog(final int position){
        final BookMark bookLabel = BookMarkDatas.getInstance().getBookMark(position);
        final LeYueDialog dialog = new LeYueDialog(mContext);
        View view = View.inflate(mContext, R.layout.book_label_dialog_layout, null);
        dialog.setContentLay(view);
        dialog.setNoButton();
        dialog.setCancelable(true);
        TextView gotoLabelView = (TextView) view.findViewById(R.id.tv_goto_label);
        TextView delLabelView = (TextView) view.findViewById(R.id.tv_del_label);
        gotoLabelView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                selectBookMarkCallback(bookLabel);
                dialog.cancel();
            }
        });
        delLabelView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                BookMarkDatas.getInstance().deleteBookMark(position);
                mBookmarkItemAdapter.notifyDataSetChanged();
                ToastUtil.showToast(mContext, R.string.book_label_del_success);
                showBookMarkEmpty();
                dialog.cancel();

            }
        });
        dialog.show();
    }

	private class ViewHolder {
		public ListView mListView;
		public ImageView mListViewBG;
		public View mLoadingView;
	}

	protected TextView newIndicator(int strResID, int position) {
		TextView tv = (TextView) LayoutInflater.from(mContext).inflate(R.layout.tab_item_with_icon, null);
		tv.setId(android.R.id.title);
		tv.setText(strResID);
        tv.setBackgroundResource(position == 0? R.drawable.btn_left_selector :position == 2?R.drawable.btn_right_selector:R.drawable.btn_center_selector);
		return tv;
	};

	@Override
	public void onCreate() {

	}

	@Override
	public void onDestroy() {

	}

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

        int selectPosition = -1;
        public void setSelection(int position){
            selectPosition = position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if(convertView == null){
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.reader_catalog_item_leyue, null);
                viewHolder.titileTV = (TextView)convertView.findViewById(R.id.catalog_title_tv);
                viewHolder.titileIndexTV = (TextView)convertView.findViewById(R.id.catalog_title_index_tv);
                viewHolder.payIV = convertView.findViewById(R.id.catalog_pay_iv);
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
            float textSize = DimensionsUtil.dip2px(11, mContext);
            for (int i = 1; i < catalog.getLayer(); i++) {
                textSize *= 0.85f;
            }
            if (ReadSetting.getInstance(getContext()).getThemeType() == ReadSetting.THEME_TYPE_NIGHT){
                viewHolder.titileTV.setTextColor(getResources().getColor(R.color.catalog_night_textcolor));
                viewHolder.titileIndexTV.setTextColor(getResources().getColor(R.color.catalog_night_textcolor));
            }else{
                viewHolder.titileTV.setTextColor(getResources().getColor(R.color.catalog_day_textcolor));
                viewHolder.titileIndexTV.setTextColor(getResources().getColor(R.color.catalog_day_textcolor_index));
            }
            int paddingLeft = DimensionsUtil.dip2px(10, mContext) * catalog.getLayer();
            viewHolder.titileTV.setPadding(paddingLeft, convertView.getPaddingTop()
                    , convertView.getPaddingRight(), convertView.getPaddingBottom());
            viewHolder.titileTV.setText(catalog.getText());
//			viewHolder.titileTV.setTextSize(textSize);
            if(position >= CatalogView.this.feeIndex && CatalogView.this.feeIndex >= 0 && !mIsOrder){
                viewHolder.payIV.setVisibility(View.VISIBLE);
                viewHolder.titileIndexTV.setVisibility(View.GONE);
                if (ReadSetting.getInstance(getContext()).getThemeType() == ReadSetting.THEME_TYPE_NIGHT){
                    viewHolder.titileTV.setTextColor(getResources().getColor(R.color.catalog_night_textcolor_needbuy));
                }else{
                    viewHolder.titileTV.setTextColor(getResources().getColor(R.color.catalog_day_textcolor_needbuy));
                }
            }else{
                viewHolder.titileIndexTV.setVisibility(View.VISIBLE);
                viewHolder.payIV.setVisibility(View.INVISIBLE);
            }
            if (position == selectPosition){
                viewHolder.titileTV.setTextColor(getResources().getColor(R.color.catalog_selected));
            }
            return convertView;
        }

        private class ViewHolder{
            TextView titileTV;
            TextView titileIndexTV;
            View payIV;
        }
	}

}
