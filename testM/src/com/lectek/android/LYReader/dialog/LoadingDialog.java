package com.lectek.android.LYReader.dialog;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.viewModel.BaseDialog;
/**
 * loading对话框,可自定义文字内容
 * @author zhouxinghua
 *
 */
public class LoadingDialog extends BaseDialog {
	
	private TextView mContentTV;
	private ViewGroup mContainer;
	
	public LoadingDialog(Activity context) {
		super(context, R.style.loadingDialog);
		mContainer = (ViewGroup)LayoutInflater.from(context).inflate(R.layout.dialog_loading_lay, null);
		setContentView(mContainer);
		mContentTV = (TextView) findViewById(R.id.content_tv);
	}
	
	public void setContentText(int resId) {
		if(resId > 0) {
			mContentTV.setText(resId);
		}
	}

	public void replaceContentView(View view) {
		if(view != null) {
			mContainer.removeAllViews();
			mContainer.addView(view);
		}
	}
}
