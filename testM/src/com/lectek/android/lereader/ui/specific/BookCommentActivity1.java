package com.lectek.android.lereader.ui.specific;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.contentinfo.BookCommentViewModel1;
import com.lectek.android.lereader.binding.model.contentinfo.BookCommentViewModel1.UserActionListener;
import com.lectek.android.lereader.ui.common.BaseActivity;

public class BookCommentActivity1 extends BaseActivity {
	
	public static final String EXTRA_BOOKID = "extra_bookid";
	public static final String EXTRA_KEYBOARD_STATE = "extra_keyboard_state";

	private BookCommentViewModel1 mBookCommentViewModel;
	private String mBookId;
	private boolean mIsShowKeyboard;
	
	public static void OpenActivity(Context context, String bookId, boolean isShowSoftKeyboard){
		Intent intent = new Intent(context, BookCommentActivity1.class);
		intent.putExtra(EXTRA_BOOKID, bookId);
		intent.putExtra(EXTRA_KEYBOARD_STATE, isShowSoftKeyboard);
		context.startActivity(intent);
	}
	
	//该方法在newContentView方法之前调用,用于初始化变量
	@Override
	protected void initVar() {
		Intent intent = getIntent();
		mBookId = intent.getStringExtra(EXTRA_BOOKID);
		mIsShowKeyboard = intent.getBooleanExtra(EXTRA_KEYBOARD_STATE, false);
	}

	@Override
	protected View newContentView(Bundle savedInstanceState) {
		mBookCommentViewModel = new BookCommentViewModel1(this, this, mBookId);
		mBookCommentViewModel.onStart();
		View mView = bindView(R.layout.book_comment_list_layout, mBookCommentViewModel);
//		HandleSoftKeyboardStateRelativeLayout layout = (HandleSoftKeyboardStateRelativeLayout)mView.findViewById(R.id.resize_listener_layout);
		
		//这边监听布局改变是为了判断软键盘的显示和隐藏状态
//		layout.setOnSoftKeyBoardStateChangeListener(new onSoftKeyBoardStateChangeListener() {
//			
//			@Override
//			public void onSoftKeyBoardStateChanged(int state) {
//				switch (state) {
//					case HandleSoftKeyboardStateRelativeLayout.SOFTKEYBOARD_STATE_INIT:
//						break;
//					case HandleSoftKeyboardStateRelativeLayout.SOFTKEYBOARD_STATE_SHOW:
//						//此方法延迟这种控件的显示模式，为了避开目前界面出去布局的过程设置可显示状态无效
//						mHandler.sendEmptyMessageDelayed(View.VISIBLE, 5);
//						break;
//					case HandleSoftKeyboardStateRelativeLayout.SOFTKEYBOARD_STATE_HIDE:
//						mHandler.sendEmptyMessageDelayed(View.GONE, 5);
//						break;
//				}
//			}
//		});
		setTitleContent(getResources().getString(R.string.book_comment_title));
		return mView;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mBookCommentViewModel.setUserActionListener(new UserActionListener() {
			
			@Override
			public void loadCompleted() {
				if(mIsShowKeyboard){
					//显示软键盘
//					getWindow().setAttributes(new WindowManager.LayoutParams(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE));
					InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
					imm.showSoftInput(findViewById(R.id.comment_et), InputMethodManager.RESULT_UNCHANGED_SHOWN);
					mIsShowKeyboard = false;
				}
			}

			@Override
			public void hideSoftKeyboard() {
				InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(BookCommentActivity1.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});
	}

	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			int visibility = msg.what;
			if(visibility == View.VISIBLE){
				mBookCommentViewModel.setEditExtraVisibility(true);
			}else{
				mBookCommentViewModel.setEditExtraVisibility(false);
			}
		}
		
	};
	
}
