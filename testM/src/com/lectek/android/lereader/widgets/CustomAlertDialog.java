package com.lectek.android.lereader.widgets;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.ui.pay.RechargeSmsActivity;
import com.lectek.android.lereader.utils.DialogUtil;
import com.lectek.android.widget.UpperBoundFrameLayout;

/**
 * 自定义AlertDialog
 * 
 * @author mingkg21
 * @date 2010-5-4
 * @email mingkg21@gmail.com
 */
public class CustomAlertDialog extends Dialog {

	private Builder builder;

	private CustomAlertDialog(Builder builder, int styleId) {
		super(builder.context, styleId);
		this.builder = builder;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_alert_short);
		if (builder.view instanceof ListView) {
			ListAdapter mListAdapter = ((ListView) builder.view)
					.getAdapter();
			if (mListAdapter != null && mListAdapter.getCount() > 0) {
				findViewById(R.id.dialog_line_gone).setVisibility(
						View.VISIBLE);
			}
		}
		builder.dialog = this;
		TextView titleTV = (TextView) findViewById(R.id.dialog_title);
		Button positiveBtn = (Button) findViewById(R.id.positive_btn);
		Button negativeBtn = (Button) findViewById(R.id.negative_btn);
		LinearLayout goToSmsRecharge = (LinearLayout) findViewById(R.id.go_to_sms_recharge);
		
		UpperBoundFrameLayout content = (UpperBoundFrameLayout) findViewById(R.id.dialog_content);
		
		if(builder.bottomView != null){
			((LinearLayout)findViewById(R.id.buy_info_ll)).addView(builder.bottomView);
		}

		if (TextUtils.isEmpty(builder.title)) {
			titleTV.setVisibility(View.GONE);
			// findViewById(R.id.dialog_divider).setVisibility(View.GONE);
		} else {
			titleTV.setText(builder.title);
		}
		int positiveBtnTextId = R.string.btn_text_confirm;
		int negativeBtnTextId = R.string.btn_text_cancel;
		if (builder.textId != -1) {
			positiveBtnTextId = builder.textId;
		}
		negativeBtn.setTag(false);
		positiveBtn.setTag(false);
		View.OnClickListener negativeBtnClickListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(builder.negativeListener != null){
					builder.negativeListener.onClick(v, 0);
				}
				dismiss();
			}
		};
		View.OnClickListener positiveBtnClickListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(builder.positiveListener != null){
					builder.positiveListener.onClick(v, 0);
				}
				if(!(Boolean) v.getTag()){
					dismiss();	
				}else{
					v.setTag(false);
				}
			}
		};
		
		goToSmsRecharge.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				builder.context.startActivity(new Intent(builder.context, RechargeSmsActivity.class));
			}
		});
		
		if (builder.negativeListener == null) {
			negativeBtn.setVisibility(View.GONE);
		}
		if (builder.positiveListener == null) {
			positiveBtn.setVisibility(View.GONE);
		}
		if(builder.isGotoSmsRecharge){
			goToSmsRecharge.setVisibility(View.VISIBLE);
		}
		
		DialogUtil.dealDialogBtnWithPrimarySecondary(positiveBtn, positiveBtnTextId, positiveBtnClickListener, negativeBtn, negativeBtnTextId, negativeBtnClickListener);
		if (builder.view != null) {
			ViewGroup rootView = (ViewGroup) findViewById(R.id.alert_dialog_root_view);
			rootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			int displayHeight = getContext().getResources().getDisplayMetrics().heightPixels;
			if(builder.isGotoSmsRecharge){
				if(displayHeight == 320){
					content.setMaxHeight(displayHeight - (rootView.getMeasuredHeight()) - 60);
				}else{
					content.setMaxHeight(displayHeight - (rootView.getMeasuredHeight() * 2 - rootView.getMeasuredHeight()/4));
				}
			}else{
				content.setMaxHeight(displayHeight - rootView.getMeasuredHeight() * 2);
			}
			FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			lp.bottomMargin = 10;
			content.addView(builder.view, lp);
		}
	}

	public static class Builder {
		protected boolean isShort = true;// 标示是长Dialog，还是短的,将匹配不同xml
		protected boolean isGotoSmsRecharge = false; //是否显示短信充值界面
		protected Context context;
		protected CharSequence title = "";
		protected View view;
		protected View bottomView;
		protected CustomDialogInterface.OnClickListener positiveListener;
		protected CustomDialogInterface.OnClickListener negativeListener;
		protected String[] items;
		protected int textId = -1;
		protected CustomAlertDialog dialog;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setTitle(CharSequence title) {
			this.title = title;
			return this;
		}

		public Builder setTitle(int resId) {
			this.title = context.getString(resId);
			return this;
		}
		
		public Builder setIsShort(boolean isShort){
			this.isShort = isShort;
			return this;
		}
		
		public void setIsGotoSmsRecharge(boolean isGotoSmsRecharge){
			this.isGotoSmsRecharge = isGotoSmsRecharge;
		}
		
		public Builder setView(View view) {
			this.view = view;
			if (this.view instanceof ListView) {
				ListAdapter mListAdapter = ((ListView) this.view).getAdapter();
				if (mListAdapter != null && mListAdapter.getCount() > 10 || !isShort) {
					isShort = false;
				}
				((ListView) this.view).setDivider(context.getResources()
						.getDrawable(R.drawable.line));
				((ListView) this.view).setCacheColorHint(Color.TRANSPARENT);
			}
			return this;
		}
		
		public void setBottomView(View view){
			this.bottomView = view;
		}
		
		public Builder setPositiveButton(
				CustomDialogInterface.OnClickListener listener) {
			this.positiveListener = listener;
			return this;
		}

		public Builder setPositiveButton(int textId,
				CustomDialogInterface.OnClickListener listener) {
			this.textId = textId;
			this.positiveListener = listener;
			return this;
		}

		public Builder setNegativeButton(
				CustomDialogInterface.OnClickListener listener) {
			this.negativeListener = listener;
			return this;
		}
		
		public CustomAlertDialog create() {
			CustomAlertDialog dialog = new CustomAlertDialog(this, R.style.CustomDialog);
			return dialog;
		}

		public Builder setItems(String[] items,
				final CustomDialogInterface.OnClickListener listener) {
			isShort = false;
			this.items = items;
			ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(
					context, R.layout.dialog_alert_list_item, items);
			ListView mListView = new ListView(context);
			mListView.setAdapter(mArrayAdapter);
			mListView.setDivider(context.getResources().getDrawable(
					R.drawable.line));
			mListView.setCacheColorHint(Color.TRANSPARENT);
			this.view = mListView;
			mListView.setAdapter(mArrayAdapter);
			mListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View view,
						int position, long id) {
					listener.onClick(view, position);
					if (dialog != null) {
						dialog.dismiss();
					}
				}
			});
			return this;
		}
	}
}
