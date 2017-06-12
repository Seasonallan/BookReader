package com.lectek.android.lereader.ui.specific;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.animation.OpenBookAnimManagement;
import com.lectek.android.lereader.binding.model.bookShelf.BookShelfViewModelLeyue;
import com.lectek.android.lereader.binding.model.bookShelf.BookShelfViewModelLeyue.ActionbarHandler;
import com.lectek.android.lereader.binding.model.bookShelf.BookShelfViewModelLeyue.TitleViewHandler;
import com.lectek.android.lereader.ui.common.BaseNetPanelView;
import com.lectek.android.widget.ViewPagerTabHost.ViewPagerChild;
import com.umeng.analytics.MobclickAgent;

public class BookShelfView extends BaseNetPanelView implements ViewPagerChild{
	
	private final String TAG = BookShelfView.class.getSimpleName();
	
//	private BookShelfViewModel mBookShelfViewModel;
	private BookShelfViewModelLeyue mBookShelfViewModel;
	
	private boolean mCanScroll;

	protected ActionbarHandler mActionbarHandler;
	
	public BookShelfView(Context context) {
		super(context);
//		showLoadView();
	}

	@Override
	public void onCreate() {
//		mBookShelfViewModel = new BookShelfViewModel(getContext(),this);
		MobclickAgent.onPageStart(TAG);
		setFocusable(true);
		mBookShelfViewModel = new BookShelfViewModelLeyue(getContext(),this);
		mBookShelfViewModel.setTitleViewHandler(new TitleViewHandler() {
			
			@Override
			public void setTitleView(final ActionbarHandler actionbarHandler) {
				
				mActionbarHandler = actionbarHandler;
				mCanScroll = true;
				
				View actionBarView = LayoutInflater.from(getContext()).inflate(R.layout.bookshelf_action_bar, null);
				View quitEditBtn = actionBarView.findViewById(R.id.quit_edit_iv);
				View selectAllBtn = actionBarView.findViewById(R.id.select_all_iv);
				View removeBtn = actionBarView.findViewById(R.id.remove_iv);
				BookShelfView.this.setTitleView(actionBarView);
				
				quitEditBtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						actionbarHandler.cancelEditMode();
					}
				});
				
				selectAllBtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						actionbarHandler.selectAllItem();
					}
				});
				
				removeBtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						actionbarHandler.remove();
					}
				});
			}
			
			@Override
			public void resetTitleView() {
				mCanScroll = false;
				BookShelfView.this.resetTitleBar();
			}
		});
		bindView(R.layout.book_shelf_layout,this, mBookShelfViewModel);
		mBookShelfViewModel.onStart();
	}
	
	@Override
	public void onActivityResume(boolean isFirst) {
		super.onActivityResume(isFirst);
		OpenBookAnimManagement.getInstance().starCloseBookAnim(null);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if(mCanScroll){
				if(mActionbarHandler != null)
					mActionbarHandler.cancelEditMode();
				return false;
			}
		}
		return true;
	}

	@Override
	public void onDestroy() {
		MobclickAgent.onPageEnd(TAG);
		mBookShelfViewModel.onRelease();
	}

	@Override
	public boolean canScroll(ViewPager viewPager, int dx, int x, int y) {
		return mCanScroll;
	}
}
