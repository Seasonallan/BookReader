package com.lectek.android.lereader.ui.specific;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.lectek.android.LYReader.R;
import com.lectek.android.app.ITitleBar;
import com.lectek.android.lereader.binding.model.feedback.FeedBackNewViewModel;
import com.lectek.android.lereader.ui.common.BaseActivity;
import com.lectek.android.lereader.utils.KeyBoardUtil;

public class FeedBackNewActivity extends BaseActivity{
	
	private View mContentView;
	
	private FeedBackNewViewModel viewModel;
	
	private IFeedBackAction mFeedBackAction;

	@Override
	protected View newContentView(Bundle savedInstanceState) {
		viewModel = new FeedBackNewViewModel(this, this);
		mContentView = bindView(R.layout.feedback_new_layout, viewModel);
		return mContentView;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleContent((getString(R.string.main_menu_feedback,"")));
		setRightButtonEnabled(true);
		setRightButton(getResources().getString(R.string.dialog_recommend_send), -1);
		KeyBoardUtil.showInputMethodManager(getApplicationContext());
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		KeyBoardUtil.hideInputMethodManager(this, this.findViewById(R.id.et_feedback_content));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	public interface IFeedBackAction{
		public void onSendFeedBackClick();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == ITitleBar.MENU_ITEM_ID_RIGHT_BUTTON){
			//点击右键保存
			if(mFeedBackAction != null){
				mFeedBackAction.onSendFeedBackClick();
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void setFeedBackAction(IFeedBackAction aFeedBackAction){
		mFeedBackAction = aFeedBackAction;
	}

}
