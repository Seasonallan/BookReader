package com.lectek.android.lereader.ui.specific;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.BaseViewModel;
import com.lectek.android.lereader.binding.model.search.SearchListViewModel;
import com.lectek.android.lereader.binding.model.search.SearchListViewModel.SearchUserAction;
import com.lectek.android.lereader.lib.storage.dbase.BaseDatabase;
import com.lectek.android.lereader.lib.utils.DimensionsUtil;
import com.lectek.android.lereader.lib.utils.IProguardFilterOnlyPublic;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.storage.dbase.SearchKey;
import com.lectek.android.lereader.ui.IRetryClickListener;
import com.lectek.android.lereader.ui.specific.LoadResultFromJs.LoadReselutCallBack;
import com.lectek.android.lereader.utils.CommonUtil;
import com.lectek.android.lereader.utils.KeyBoardUtil;
import com.lectek.android.lereader.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * @description
	搜索列表
 * @author chendt
 * @date 2013-9-10
 * @Version 1.0
 */
public class SearchListActivity extends FlingExitBaseAcitity implements IRetryClickListener,IProguardFilterOnlyPublic/*, IBindViewCallBack,Observer*/{
	
	private final String PAGE_NAME = "搜索列表";
	private static final String TAG = SearchListActivity.class.getSimpleName();
	public static final String SEARCH_KEYWORD = "SEARCH_KEYWORD";
	private SearchListViewModel mSearchListViewModel;
	private AutoCompleteTextView mEditView;
	private boolean isInited;
//	private View mFootLoadingView;
//	private ViewGroup mFootLoadingLay;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.e("---onCreate---");
		mEditView = (AutoCompleteTextView) findViewById(R.id.EditText01);
		mEditView.setOnEditorActionListener(mEditorActionListener);

		setTitleBarEnabled(false);
		resetLoadingView();
		setmRetryClickListener(this);	
		Intent intent = getIntent();
		String keyWord = intent.getStringExtra(SEARCH_KEYWORD);
		if(!TextUtils.isEmpty(keyWord)){
			mEditView.setText(keyWord);
			mSearchListViewModel.startBookCitySearch();
		}
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);//一进去就弹出键盘
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(PAGE_NAME);
		MobclickAgent.onResume(this);
		
		if(!isInited){
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					
				}
			}, 800);
		}
		mSearchListViewModel.onCreate();
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(PAGE_NAME);
		MobclickAgent.onPause(this);
	}

	/**由于基类的loadingView是设置在titleBar下面，此界面隐藏Title，采用自定义的TitleBar，故需此操作，以及
	 * 重写 needLoadView() 方法*/
	private void resetLoadingView() {
		mLoadingView = getLayoutInflater().inflate(R.layout.load_and_retry_lay, null);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		params.addRule(RelativeLayout.BELOW, R.id.header_bar);
		RelativeLayout searchRootView = (RelativeLayout)findViewById(R.id.search_root_view);
		searchRootView.addView(mLoadingView, params);
		getHandleView();
	}

	@Override
	protected View newContentView(Bundle savedInstanceState) {
		mSearchListViewModel = new SearchListViewModel(this_, this,new MySerchUserAciton());
//		mSearchListViewModel.bFootLoadingVisibility.subscribe(this);
//		mSearchListViewModel.bFootLoadCompletedVisibility.subscribe(this);
//		mFootLoadingView = getLayoutInflater().inflate(R.layout.loading_data_lay, mFootLoadingLay);
//		mFootLoadingLay = new FrameLayout(this);
		return bindTempView(R.layout.search_layout, mSearchListViewModel);
	}
	
	class MySerchUserAciton implements SearchUserAction, IProguardFilterOnlyPublic{

		@Override
		public void finish() {
			hideKeyBoard();
			this_.finish();
		}

		@Override
		public boolean bindDialogViewModel(Context context,
				BaseViewModel baseViewModel) {
			return false;
			// TODO Auto-generated method stub
			
		}

		@Override
		public int getRes(String type) {
			return 0;
			// TODO Auto-generated method stub
		}

		@Override
		public void showHistoryRecord(View view) {
			changPopState(view);
		}

		@Override
		public void searchInBookCityAction(String targetStr, boolean dismissPop) {
			if (dismissPop) {
				dismissPopWindow();
			}
			hideKeyBoard();
			searchInBookCity(targetStr);
		}

		@Override
		public void showKeyBoardAction() {
			showKeyBoard();
		}

		@Override
		public void showLoadingView() {
			resetLoadingView();
			SearchListActivity.this.showLoadView();
		}

		@Override
		public void hidePopWindow() {
			dismissPopWindow();
		}
	}
	
	@Override
	protected boolean needLoadView() {
		return false;
	}
	
	
	private void hideKeyBoard(){
		KeyBoardUtil.hideInputMethodManager((InputMethodManager)this_.getSystemService(Context.INPUT_METHOD_SERVICE)
				,mEditView);
	}
	
	private void showKeyBoard(){
		KeyBoardUtil.showInputMethodManager(this_);
	}
	
	private void searchInBookCity(String targetStr){
		if (checkNetWrok()) {
			if (!TextUtils.isEmpty(targetStr)&&!TextUtils.isEmpty(targetStr.trim())) {
				LogUtil.i(TAG, "---search_targetStr = "+targetStr);
				//保存数据库操作
				new BaseDatabase<SearchKey>(SearchKey.class).intelligenceInsert(new SearchKey(targetStr));
				
				mSearchListViewModel.startBookCitySearchMode(targetStr);;
				showLoadView();//TODO:只有这里调用，才有过渡效果。
			}else {
				ToastUtil.showToast(this_, "请输入您要搜索的内容");
			}
		}
	}
	
	private boolean isOpenPop = false;
	private PopupWindow window;

	/**
	 * 更改Pop状态
	 * */
	public void changPopState(View v) {
		isOpenPop = !isOpenPop;
		if (isOpenPop) {
			popAwindow(v);
		} else {
			dismissPopWindow();
		}
	}

	private void popAwindow(View parent) {
		if (window == null) {
			if (mEditView == null) {
				mEditView = (AutoCompleteTextView) parent;
			}
			View v = bindView(R.layout.search_pop_window, mSearchListViewModel);
			window = new PopupWindow(v, mEditView.getWidth(), android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		}else {
			if (window.isShowing()) {
				return;
			}
		}
		window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
		window.setFocusable(false);
		window.setOutsideTouchable(true);
		window.setTouchable(true);
		window.setTouchInterceptor(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				window.setFocusable(true);
				window.update();
				return false;
			}
		});
		window.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				isOpenPop = false;
			}
		});
		window.update();
		if (isAttachWindowFinish) {//（第一次初始化时）部分机型，加载慢，故延时方式避免异常，非最佳方案;故作为非初始化后的处理
			mHandler.sendEmptyMessageDelayed(SHOWWINDOW, 200); 
		}
	}
	
	private boolean isAttachWindowFinish = false;
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		isAttachWindowFinish = true;
		if (mHandler!=null) {//第一次初始化在此处理
			mHandler.sendEmptyMessageDelayed(SHOWWINDOW, 200);
		}
	}
	
	private void dismissPopWindow(){
		if (window!=null && window.isShowing()) {
			window.dismiss();
		}
	}
	
	private final int SHOWWINDOW = 0x1;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SHOWWINDOW:
				if (window!=null) {
					LogUtil.e("---SHOWWINDOW---");
					int x = (int) getResources().getDimension(R.dimen.b_pop_window_offset_x);
					int y = (int) getResources().getDimension(R.dimen.b_pop_window_offset_y)-DimensionsUtil.dip2px(5, this_);
					window.setWidth(mEditView.getWidth());// 这里重新设置一遍，因为上面设置时，可能view还未绘制。
					window.showAtLocation(mEditView, Gravity.CENTER_HORIZONTAL | Gravity.TOP,
							x, y);
				}
				break;

			default:
				break;
			}
		}
		
	};
	
	@Override
	public View getFlingView() {
		return null;
	}

	@Override
	public void flingExitHandle() {
		hideKeyBoard();//在OnDestroy 中调用，无法关闭输入法
		finish();
	}

	@Override
	public void onRetryClick() {
		// TODO Auto-generated method stub
		CommonUtil.saveTimeStampForRetryNetClick(BaseWebView.getCurrentWebUrlTag(this));
	}
	
	LoadReselutCallBack mLoadReselutCallBack = new LoadReselutCallBack() {
		
		@Override
		public void onLoadReselutCallBack() {
			// TODO Auto-generated method stub
			this_.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					hideLoadView();
				}
			});
		}
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
	};
	
	private OnEditorActionListener mEditorActionListener = new OnEditorActionListener() {
		
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_SEARCH) {
				//软键盘搜索键监听
				mSearchListViewModel.startBookCitySearch();
			}     
			return false;
			
		}
	};
	
//	@Override
//	public void onPropertyChanged(IObservable<?> prop, Collection<Object> initiators) {
//		if ((Boolean) prop.get()) {
//			if (mFootLoadingView.getParent() == null) {
//				mFootLoadingLay.addView(mFootLoadingView);
//			}
//		} else {
//			mFootLoadingLay.removeAllViews();
//		}
//	}

//	@Override
//	public void onPreBindView(View rootView, int layoutId) {
//		if (layoutId == R.layout.search_layout) {
//			ListView listView = (ListView) rootView.findViewById(R.id.bookcity_search_list);
//			listView.addFooterView(mFootLoadingLay);
//		}
//	}
}
