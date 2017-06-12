package com.lectek.android.lereader.ui.specific;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.collect.CollectBookViewModel;
import com.lectek.android.lereader.ui.common.BaseActivity;


/**
 * 收藏页面
 * @author donghz
 * @date 2014-3-27
 */
public class CollectBookActivity extends BaseActivity {

	private View mContentView;
	private CollectBookViewModel mCollectViewModel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRightButtonEnabled(false);
		setRightButton("", R.drawable.remove_icon);
		setTitleContent(getString(R.string.user_info_my_collect));
		getRightBtnView().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mCollectViewModel != null) {
					mCollectViewModel.DeleteCollectBook();
				}
			}
		});
		getLeftBtnView().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!cancelEdit()) {
					finish();
				}
			}
		});
	}



	@Override
	protected View newContentView(Bundle savedInstanceState) {
		mCollectViewModel = new CollectBookViewModel(this_,this);
		mContentView = bindView(R.layout.collect_list_layout, mCollectViewModel);
		mCollectViewModel.setUserActionListener(userAction);
		return mContentView;
	}
	
	
	private CollectBookViewModel.UserActionListener userAction = new CollectBookViewModel.UserActionListener(){
		@Override
		public void setButtonRightEnable(boolean isEnable) {
			setRightButtonEnabled(isEnable);
		}

        @Override
        public void showRetryImgView() {
            setTipView(tipImg.request_fail, true);
        }

		@Override
		public void setNoDataView(tipImg tip, boolean bShow) {
			showTipView(tip, bShow,"",null);
			setOprBtnGone();
		}

		@Override
		public void cancelEditModel() {
			cancelEdit();
		}
		
	};
	
	/**
	 * 取消删除
	 */
	private boolean cancelEdit(){
		if(mCollectViewModel != null && mCollectViewModel.isEdit()){
			mCollectViewModel.setCollectCheckBoxVisible(false);
			setRightButtonEnabled(false);
			mCollectViewModel.clearCheckBoxVisible(false);
			return true;
		}
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		mCollectViewModel.onStart();
	}



	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
			if(cancelEdit()){
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	

}
