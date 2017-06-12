package com.lectek.android.lereader.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.DialogInterface.OnShowListener;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.lectek.android.LYReader.R;
import com.lectek.android.LYReader.dialog.LeYueDialog;
import com.lectek.android.LYReader.dialog.LoadingDialog;
import com.lectek.android.app.BaseApplication;
import com.lectek.android.lereader.lib.utils.ApnUtil;
import com.lectek.android.lereader.lib.utils.ClientInfoUtil;

/**
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2012-4-25
 */
public final class DialogUtil {
	
	public static boolean isDeviceNetword(final Activity mContext){
		if (!ApnUtil.isNetAvailable(mContext)) {
			ConfirmListener sureListener = new ConfirmListener() {

				@Override
				public void onClick(View v) {
//					Intent intent = new Intent(Intent.ACTION_MAIN);
//					intent.setComponent(componentName);
//					mContext.startActivity(intent);
					ClientInfoUtil.gotoSettingActivity(mContext);
				}
			};
			CancelListener cancelListener = new CancelListener() {

				@Override
				public void onClick(View v) {
				}
			};
			specialConfirmDialog2(mContext,
					R.string.conection_unavailable, sureListener,
					cancelListener, R.string.btn_text_now_setting,
					R.string.btn_text_next_setting);
			return false;
		}
		return true;
	}

	public static final void dealDialogBtnWithPrimarySecondary(Button lButton, int lResId, OnClickListener lOnClickListener, 
			Button rButton, int rResId, OnClickListener rOnClickListener){
		Resources resource = BaseApplication.getInstance().getResources();
		if(Build.VERSION.SDK_INT >= 14){//4.0
			int L_Left = lButton.getPaddingLeft();	
			int L_Top = lButton.getPaddingTop();
			int L_Right = lButton.getPaddingRight();
			int L_Bottom = lButton.getPaddingBottom();
			int R_Left = rButton.getPaddingLeft();	
			int R_Top = rButton.getPaddingTop();
			int R_Right = rButton.getPaddingRight();
			int R_Bottom = rButton.getPaddingBottom();
			lButton.setText(rResId);
			lButton.setTextColor(resource.getColor(R.color.book_price_color));
			lButton.setBackgroundResource(R.drawable.btn_book_info_common_bg);
			lButton.setPadding(L_Left, L_Top, L_Right, L_Bottom);
			lButton.setOnClickListener(rOnClickListener);
			rButton.setText(lResId);
			rButton.setOnClickListener(lOnClickListener);
			rButton.setTextColor(resource.getColor(R.color.white));
			rButton.setBackgroundResource(R.drawable.btn_normal_bg);
			rButton.setPadding(R_Left, R_Top, R_Right, R_Bottom);
		}else{//4.0以下
			lButton.setText(lResId);
			lButton.setOnClickListener(lOnClickListener);
			rButton.setText(rResId);
			rButton.setOnClickListener(rOnClickListener);
		}
	}
	
	public static final void dealDialogBtn(Button lButton, int lResId, OnClickListener lOnClickListener, 
			Button rButton, int rResId, OnClickListener rOnClickListener){
		if(Build.VERSION.SDK_INT >= 14){//4.0
			lButton.setText(rResId);
			lButton.setOnClickListener(rOnClickListener);
			rButton.setText(lResId);
			rButton.setOnClickListener(lOnClickListener);
		}else{//4.0以下
			lButton.setText(lResId);
			lButton.setOnClickListener(lOnClickListener);
			rButton.setText(rResId);
			rButton.setOnClickListener(rOnClickListener);
		}
	}
	
	//（目前只用于）注册界面使用
	public static final void dealDialogBtn(Button lButton, int lResId, OnClickListener lOnClickListener, 
			Button rButton, int rResId, OnClickListener rOnClickListener,boolean whichSetEnable){
		dealDialogBtnWithPrimarySecondary(lButton ,lResId ,lOnClickListener ,rButton , rResId , rOnClickListener);
		if(Build.VERSION.SDK_INT >= 14 && whichSetEnable){
			rButton.setEnabled(false);
		}else{
			lButton.setEnabled(false);
		}
	}
	
	public static Dialog commonConfirmDialog(Activity context, String title, String msg, int lResId, int rResId,
			final ConfirmListener confirmListener,
			final CancelListener cancelListener ,final ShowListener showListener) {
		context = CommonUtil.getRealActivity(context);
		final Dialog dialog = customDialog(context);
		if(lResId == -1){
			lResId = R.string.btn_text_confirm;
		}
		if(rResId == -1){
			rResId = R.string.btn_text_cancel;
		}
		Display display = context.getWindowManager().getDefaultDisplay();
		LinearLayout.LayoutParams lp = new LayoutParams(display.getWidth()*4/5, LayoutParams.WRAP_CONTENT);
		dialog.setContentView(LayoutInflater.from(context).inflate(R.layout.common_dialog_confirm, null),lp);
		dialog.setCancelable(false);
		if(!TextUtils.isEmpty(title)){
			dialog.findViewById(R.id.dialog_title_lay).setVisibility(View.VISIBLE);
			TextView titleTV = (TextView) dialog.findViewById(R.id.dialog_title);
			titleTV.setText(title);
		}
		TextView contentTV = (TextView) dialog.findViewById(R.id.dialog_content);
		contentTV.setText(msg);
		
		OnClickListener confirmOnClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (confirmListener != null) {
					confirmListener.onClick(v);
				}
				dialog.dismiss();
			}
			
		};
		
		OnClickListener cancelOnClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (cancelListener != null) {
					cancelListener.onClick(v);
				}
				dialog.dismiss();
			}
			
		};
		dealDialogBtn(dialog, lResId, confirmOnClickListener, rResId, cancelOnClickListener);
		
		dialog.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (cancelListener != null) {
					if (keyCode == KeyEvent.KEYCODE_BACK) {
						cancelListener.onClick(null);
						return true;
					}
				}
				return false;
			}
		});
		dialog.setOnShowListener(new OnShowListener() {
			
			@Override
			public void onShow(DialogInterface dialog) {
				showListener.onShow(dialog);
			}
		});
		if (!context.isFinishing()) {
			dialog.show();
		}
		
		return dialog;
	}
	
	public static interface HandlerListener {
		public void flash(String str);
	}
	
	public static interface ConfirmListener {
		public void onClick(View v);
	}
	
	public static interface CancelListener {
		public void onClick(View v);
	}
	
	public static interface ShowListener {
		public void onShow(DialogInterface dialog);
	}
	
	public static final Dialog customDialog(Context context) {
		Dialog dialog = new Dialog(context, R.style.commonDialog);
		dialog.setCanceledOnTouchOutside(true);
		//设置暗度为50%
		WindowManager.LayoutParams lp=dialog.getWindow().getAttributes();   
        lp.dimAmount = 0.5f;   
        dialog.getWindow().setAttributes(lp);   
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);  
		return dialog;
	}
	
	public static final void dealDialogBtn(Dialog dialog, int lResId, OnClickListener lOnClickListener, 
			int rResId, OnClickListener rOnClickListener){
		Button okButton ;
		Button cancleButton ;
		if(Build.VERSION.SDK_INT >= 14){//4.0
			dialog.findViewById(R.id.common_btn_lay).setVisibility(View.GONE);
			dialog.findViewById(R.id.deal_btn_lay).setVisibility(View.VISIBLE);
			okButton = (Button) dialog.findViewById(R.id.dialog_deal_ok);
			cancleButton = (Button) dialog.findViewById(R.id.dialog_deal_cancel);
		}else{//4.0以下
			dialog.findViewById(R.id.deal_btn_lay).setVisibility(View.GONE);
			dialog.findViewById(R.id.common_btn_lay).setVisibility(View.VISIBLE);
			okButton = (Button) dialog.findViewById(R.id.dialog_ok);
			cancleButton = (Button) dialog.findViewById(R.id.dialog_cancel);
		}
		okButton.setText(lResId);
		okButton.setOnClickListener(lOnClickListener);
		cancleButton.setOnClickListener(rOnClickListener);
		cancleButton.setText(rResId);
	}
	
	public static void commonConfirmDialog(Activity context, String title, int msgId,
			final ConfirmListener confirmListener) {
		context = CommonUtil.getRealActivity(context);
		commonConfirmDialog(context, title, context.getString(msgId), confirmListener);
	}
	
	/**
	 * 通用的确认对话框
	 * 
	 * @param msg
	 *            对话框的内容
	 * @param okListener
	 *            确定按钮的点击事件
	 */
	public static void commonConfirmDialog(Activity context, String title, String msg,
			final ConfirmListener confirmListener) {
		context = CommonUtil.getRealActivity(context);
		commonConfirmDialog(context, title, msg, -1, -1, confirmListener, null);
	}
	
	/**
	 * 只有一个确认按钮的对话框
	 * @param context
	 * @param title
	 * @param msg
	 * @param lResId
	 * @param confirmListener
	 */
	public static Dialog oneConfirmBtnDialog(Activity context, String title, String msg, int confirmResId,
			final ConfirmListener confirmListener) {
		context = CommonUtil.getRealActivity(context);
		final Dialog dialog = commonConfirmDialog(context, title, msg, -1, -1, null, null);
		if(dialog instanceof LeYueDialog) {
			((LeYueDialog) dialog).oneConfirmBtn(confirmResId, confirmListener);
		}
		return dialog;
	}
	
	/**
	 * 只有一个取消按钮的对话框
	 * @param context
	 * @param title
	 * @param msg
	 * @param cancelResId
	 * @param confirmListener
	 */
	public static Dialog oneCancelBtnDialog(Activity context, String title, String msg, int cancelResId,
			final CancelListener cancelListener) {
		context = CommonUtil.getRealActivity(context);
		final Dialog dialog = commonConfirmDialog(context, title, msg, -1, -1, null, null);
		if(dialog instanceof LeYueDialog) {
			((LeYueDialog) dialog).oneCancelBtn(cancelResId, cancelListener);
		}
		return dialog;
	}
	
	/**
	 * 通用的确认对话框
	 * 
	 * @param context
	 * @param msg
	 * @param confirmListener
	 *           确定按钮的点击事件
	 * @param cancelListener
	 *            取消按钮的点击事件
	 */
	public static Dialog commonConfirmDialog(Activity context, String title, String msg, int lResId, int rResId,
			final ConfirmListener confirmListener,
			final CancelListener cancelListener) {
		context = CommonUtil.getRealActivity(context);
		final LeYueDialog dialog = new LeYueDialog(context);
		dialog.setTitle(title);
		dialog.setContent(msg);
		dialog.dealDialogBtn(lResId, confirmListener, rResId, cancelListener);
		
		dialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (cancelListener != null) {
					if (keyCode == KeyEvent.KEYCODE_BACK) {
						cancelListener.onClick(null);
						return true;
					}
				}
				
				return false;
			}
		});
		
		if (!context.isFinishing()) {
			dialog.show();
		}
		
		return dialog;
	}
	
	/**
	 * 
	 * @param context
	 * @param title 标题
	 * @param Content 对话框内容，传入一个VIEW
	 * @param lResId confirmListener的按钮文字,默认为"确定"
	 * @param rResId cancelListener的按钮文字,默认为"取消"
	 * @param confirmListener
	 * @param cancelListener
	 * @return
	 */
	public static Dialog commonConfirmDialog(Activity context, String title, View Content, int lResId, int rResId,
			final ConfirmListener confirmListener,
			final CancelListener cancelListener){
		context = CommonUtil.getRealActivity(context);
		final LeYueDialog dialog = new LeYueDialog(context);
		dialog.setTitle(title);
		dialog.setContentLay(Content);
		dialog.dealDialogBtn(lResId, confirmListener, rResId, cancelListener);
		
		dialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (cancelListener != null) {
					if (keyCode == KeyEvent.KEYCODE_BACK) {
						cancelListener.onClick(null);
						return true;
					}
				}
				
				return false;
			}
		});
		
		if (!context.isFinishing()) {
			dialog.show();
		}
		
		return dialog;
	}
	
	public static Dialog getLoadingDialog(Activity context, int resId) {
		context = CommonUtil.getRealActivity(context);
		LoadingDialog dialog = new LoadingDialog(context);
		dialog.setContentText(resId);
		return dialog;
	}
	
	public static Dialog getLoadingDialog(Activity context, View contentView) {
		context = CommonUtil.getRealActivity(context);
		LoadingDialog dialog = new LoadingDialog(context);
		dialog.replaceContentView(contentView);
		return dialog;
	}
	
	/**
	 * 自定义弹窗
	 * @param context
	 * @param view 自定义view
	 * @return
	 */
	public static Dialog commonViewDialog(Context context, View view){
		final Dialog dialog = customDialog(context);
		dialog.setContentView(view);
		return dialog;
	}
	
	public static void specialYesOrNoDialog(final Activity context, int resId,
			final ConfirmListener confirmListener) {
		// specialConfirmDialog(context, resId, confirmListener, null,
		// R.drawable.btn_confirm_yes, R.drawable.btn_confirm_no);
		specialConfirmDialog2(context, resId, confirmListener, null,
				R.string.btn_text_confirm_yes, R.string.btn_text_confirm_no);
	}
	
	public static void specialConfirmDialog2(final Activity context, int resId,
			final ConfirmListener confirmListener,
			final CancelListener cancelListener, int sureResId, int cancelResId) {
		specialConfirmDialog2(context, context.getString(resId),
				confirmListener, cancelListener, sureResId, cancelResId);
	}

	public static void specialConfirmDialog2(final Activity context,
			String msg, final ConfirmListener confirmListener,
			final CancelListener cancelListener, int sureResId, int cancelResId) {
		Dialog dialog = createSpecialConfirmDialog2(context, msg, confirmListener, cancelListener, sureResId, cancelResId);
		if (!context.isFinishing()) {
			dialog.show();
		}
	}
	
	public static Dialog createSpecialConfirmDialog2(final Activity context,
			int resId, final ConfirmListener confirmListener,
			final CancelListener cancelListener, int sureResId, int cancelResId) {
		return createSpecialConfirmDialog2(context, context.getString(resId), confirmListener, cancelListener, sureResId, cancelResId);
	}
	
	
	public static Dialog createSpecialConfirmDialog2(final Activity context,
			String msg, final ConfirmListener confirmListener,
			final CancelListener cancelListener, int sureResId, int cancelResId) {
		final Dialog dialog = customDialog(context);
		dialog.setContentView(R.layout.dialog_common_confirm);
		TextView contentTV = (TextView) dialog
				.findViewById(R.id.dialog_content);
		contentTV.setText(msg);
		Button okBtn = (Button) dialog.findViewById(R.id.dialog_ok);
		if (sureResId != -1) {
			okBtn.setText(sureResId);
		}
		
		OnClickListener confirmOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (confirmListener != null) {
					confirmListener.onClick(v);
				}
			}

		};
		
		Button cancelBtn = (Button) dialog.findViewById(R.id.dialog_cancel);
		if (cancelResId != -1) {
			cancelBtn.setText(cancelResId);
		}
		OnClickListener cancelOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (cancelListener != null) {
					cancelListener.onClick(v);
				}
			}

		};
		DialogUtil.dealDialogBtn(okBtn, sureResId, confirmOnClickListener, cancelBtn, cancelResId, cancelOnClickListener);
		
		dialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					dialog.dismiss();
					if (cancelListener != null) {
						cancelListener.onClick(null);
					} else {
						CommonUtil.exitApplication(context);
					}
					return true;
				} else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
					return true;
				}
				return false;
			}
		});
		return dialog;
	}

}
