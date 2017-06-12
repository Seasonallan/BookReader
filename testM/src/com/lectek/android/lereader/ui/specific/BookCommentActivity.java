package com.lectek.android.lereader.ui.specific;

import java.util.ArrayList;

import com.lectek.android.LYReader.R;
import com.lectek.android.app.ITitleBar;
import com.lectek.android.lereader.ui.IPanelView;
import com.lectek.android.lereader.ui.common.BaseActivity;
import com.lectek.android.lereader.widgets.SlideTabWidget;
import com.lectek.android.widget.BaseViewPagerTabHostAdapter;
import com.lectek.android.widget.BaseViewPagerTabHostAdapter.ItemLifeCycleListener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * 评论列表
 * @author Shizq
 *
 */
public class BookCommentActivity extends BaseActivity {
	
	public final ViewPagerAdapter bChildViewPagerTabHostAdapter = new ViewPagerAdapter();
	
	public static final String EXTRA_BOOK_ID = "extra_book_id";
	public static final String EXTRA_LE_BOOK_ID = "extra_le_book_id";
	public static final String EXTRA_IS_SURFING_READER = "extra_is_surfing_rader";
	
	private final String TAB_COMMENT_HOT = "tab_comment_hot";
	private final String TAB_COMMENT_LATEST = "tab_comment_latest";

	private String mBookId,leBookId;
	private boolean mIsSurfingReader;
	
	public static void openActivity(Context context, String bookId, String leBookId,boolean isSurfingReader){
		Intent intent = new Intent(context, BookCommentActivity.class);
		intent.putExtra(EXTRA_BOOK_ID, bookId);
		intent.putExtra(EXTRA_LE_BOOK_ID, leBookId);
		intent.putExtra(EXTRA_IS_SURFING_READER, isSurfingReader);
		context.startActivity(intent);
	}

	@Override
	protected View newContentView(Bundle savedInstanceState) {
		View view = bindView(R.layout.book_comment_layout, this);
		mBookId = getIntent().getStringExtra(EXTRA_BOOK_ID);
		leBookId = getIntent().getStringExtra(EXTRA_LE_BOOK_ID);
		mIsSurfingReader = getIntent().getBooleanExtra(EXTRA_IS_SURFING_READER, false);
		return view;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setRightButtonEnabled(true);
		setRightButton("", R.drawable.icon_comment);
		SlideTabWidget slideTabWidget = (SlideTabWidget)findViewById(android.R.id.tabs);
		slideTabWidget.initialize(0,null);
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
		
		setTitleContent(getResources().getString(R.string.title_all_comment));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(ITitleBar.MENU_ITEM_ID_RIGHT_BUTTON == item.getItemId()){
			PublishCommentActivity.openActivity(this, mBookId, leBookId,
						mIsSurfingReader);
		}
		return super.onOptionsItemSelected(item);
	}

	
	private class ViewPagerAdapter extends BaseViewPagerTabHostAdapter{
		public ArrayList<String> mTags;
		public ViewPagerAdapter(){
			mTags = new ArrayList<String>();
			mTags.add(TAB_COMMENT_HOT);
			mTags.add(TAB_COMMENT_LATEST);
		}
		
		@Override
		public View getIndicator(int position) {
			// 字体描边效果。最土方式，使用层叠效果。
			View indicatorView;
			if(position==0){
				indicatorView = LayoutInflater.from(this_).inflate(
						R.layout.comment_tab_layout_left, null);
			}else{
				indicatorView = LayoutInflater.from(this_).inflate(
						R.layout.comment_tab_layout_right, null);				
			}
			TextView textView1 = (TextView) indicatorView
					.findViewById(R.id.text);
//			TextView textView2 = (TextView) indicatorView
//					.findViewById(R.id.text_bg);

			if (TAB_COMMENT_HOT.equals(getTab(position))) {
				String title = getResources().getString(R.string.comment_hot_text);
				textView1.setText(title);
//				textView2.setText(title);
			} else if (TAB_COMMENT_LATEST.equals(getTab(position))) {
				String title = getResources().getString(R.string.comment_latest_text);
				textView1.setText(title);
//				textView2.setText(title);
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
			if(TAB_COMMENT_HOT.equals(getTab(position))){
				container = new BookCommentHotView(this_, mBookId);
			}else if(TAB_COMMENT_LATEST.equals(getTab(position))){
				container = new BookCommentLatestView(this_, mBookId);
			}
			return container;
		}
	}

}
