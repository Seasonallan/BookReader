package com.lectek.android.lereader.binding.viewModel;

import android.content.Context;
import android.view.View;

import com.lectek.android.LYReader.R;
import com.lectek.android.app.BaseApplication;
import com.lectek.android.lereader.binding.model.BaseDialogViewModel;
import com.lectek.android.lereader.binding.model.BaseDialogViewModel.OnBtnClickCallBack;
import com.lectek.android.lereader.binding.model.BaseViewModel;
import com.lectek.android.lereader.binding.model.common.CommonDialogViewModel;
import com.lectek.android.lereader.ui.IView;
/**
 * 构造CommonDialogViewModel工具类
 * @author linyiwei
 *
 */
public class DialogVModelUtil {
	/**
	 * 构建确认窗口 提示内容为字符串 取消为关闭窗口
	 * @param context
	 * @param content
	 * @param leftBtnClick
	 * @return
	 */
	public static CommonDialogViewModel createConfirmDialog(IView view,Context context,int content
			,OnBtnClickCallBack leftBtnClick){
		return createConfirmDialog(view,context, getString(R.string.app_label), getString(content)
				, getString(R.string.btn_text_confirm), getString(R.string.btn_text_cancel),leftBtnClick);
	}
	/**
	 * 构建确认窗口 提示内容为字符串 取消为关闭窗口
	 * @param context
	 * @param title
	 * @param content
	 * @param leftBtnClick
	 * @return
	 */
	public static CommonDialogViewModel createConfirmDialog(IView view,Context context,int title,int content
			,OnBtnClickCallBack leftBtnClick){
		return createConfirmDialog(view,context, getString(title), getString(content)
				, getString(R.string.btn_text_confirm), getString(R.string.btn_text_cancel),leftBtnClick);
	}
	/**
	 * 构建确认窗口 提示内容为字符串 取消为关闭窗口
	 * @param context
	 * @param title
	 * @param content
	 * @param letfButStr
	 * @param rightButStr
	 * @param leftBtnClick
	 * @return
	 */
	public static CommonDialogViewModel createConfirmDialog(IView view,Context context,int title,int content
			,int letfButStr,int rightButStr,OnBtnClickCallBack leftBtnClick){
		return createConfirmDialog(view,context, getString(title), getString(content)
				, getString(letfButStr), getString(rightButStr),leftBtnClick);
	}
	/**
	 * 构建确认窗口 无title 提示内容为字符串 取消为关闭窗口
	 * @param context
	 * @param content
	 * @param leftBtnClick
	 * @return
	 */
	public static CommonDialogViewModel createConfirmDialogNotT(IView view,Context context,int content
			,OnBtnClickCallBack leftBtnClick){
		return createConfirmDialog(view,context, null, getString(content)
				, getString(R.string.btn_text_confirm), getString(R.string.btn_text_cancel),leftBtnClick);
	}
	/**
	 * 构建确认窗口 无title 提示内容为字符串 取消为关闭窗口
	 * @param context
	 * @param content
	 * @param leftBtnClick
	 * @return
	 */
	public static CommonDialogViewModel createConfirmDialogNotT(IView view,Context context,String content
			,OnBtnClickCallBack leftBtnClick){
		return createConfirmDialog(view,context, null, content
				, getString(R.string.btn_text_confirm), getString(R.string.btn_text_cancel),leftBtnClick);
	}
	/**
	 * 构建确认窗口 无title 提示内容为字符串 取消为关闭窗口
	 * @param context
	 * @param content
	 * @param letfButStr
	 * @param rightButStr
	 * @param leftBtnClick
	 * @return
	 */
	public static CommonDialogViewModel createConfirmDialogNotT(IView view,Context context,int content
			,int letfButStr,int rightButStr,OnBtnClickCallBack leftBtnClick){
		return createConfirmDialog(view,context, null, getString(content)
				, getString(letfButStr), getString(rightButStr),leftBtnClick);
	}
	/**
	 * 构建确认窗口 无title 提示内容为字符串 取消为关闭窗口
	 * @param context
	 * @param content
	 * @param letfButStr
	 * @param rightButStr
	 * @param leftBtnClick
	 * @return
	 */
	public static CommonDialogViewModel createConfirmDialogNotT(IView view,Context context,String content
			,String letfButStr,String rightButStr,OnBtnClickCallBack leftBtnClick){
		return createConfirmDialog(view,context, null, content
				, letfButStr, rightButStr,leftBtnClick);
	}
	/**
	 * 构建确认窗口  提示内容为字符串  取消为关闭窗口
	 * @param context
	 * @param title 为空则无title
	 * @param content
	 * @param letfButStr
	 * @param rightButStr
	 * @param leftBtnClick
	 * @return
	 */
	public static CommonDialogViewModel createConfirmDialog(IView view,Context context,String title,String content
			,String letfButStr,String rightButStr,OnBtnClickCallBack leftBtnClick){
		CommonDialogViewModel dialogViewModel = new CommonDialogViewModel(
				context, title, content, letfButStr, rightButStr, leftBtnClick
				, new OnBtnClickCallBack() {
					@Override
					public void onClick(View v, BaseDialogViewModel viewModel) {
						if(viewModel.isShowing()){
							viewModel.finish();
						}
					}
				});
		view.bindDialogViewModel(context, dialogViewModel);
		return dialogViewModel;
	}
	/**
	 *  构建确认窗口  无title 提示内容为自定义View 取消为关闭窗口
	 * @param context
	 * @param contentRes
	 * @param viewModel
	 * @param leftBtnClick
	 * @return
	 */
	public static CommonDialogViewModel createConfirmVDialogNotT(IView view,Context context,int contentRes
			,BaseViewModel viewModel,OnBtnClickCallBack leftBtnClick){
		return createConfirmVDialog(view,context, null, contentRes , viewModel
				,getString(R.string.btn_text_confirm), getString(R.string.btn_text_cancel),leftBtnClick);
	}
	
	/**
	 * 构建确认窗口  无title 提示内容为自定义View 取消为关闭窗口
	 * @param context
	 * @param contentRes
	 * @param viewModel
	 * @param letfButStr
	 * @param rightButStr
	 * @param leftBtnClick
	 * @return
	 */
	public static CommonDialogViewModel createConfirmVDialogNotT(IView view,Context context,int contentRes
			,BaseViewModel viewModel,int letfButStr,int rightButStr,OnBtnClickCallBack leftBtnClick){
		return createConfirmVDialog(view,context, null, contentRes , viewModel, getString(letfButStr)
				, getString(rightButStr),leftBtnClick);
	}
	/**
	 * 构建确认窗口  无title 提示内容为自定义View 取消为关闭窗口
	 * @param context
	 * @param contentRes
	 * @param viewModel
	 * @param letfButStr
	 * @param rightButStr
	 * @param leftBtnClick
	 * @return
	 */
	public static CommonDialogViewModel createConfirmVDialogNotT(IView view,Context context,int contentRes
			,BaseViewModel viewModel,String letfButStr,String rightButStr,OnBtnClickCallBack leftBtnClick){
		return createConfirmVDialog(view,context, null, contentRes , viewModel, letfButStr, rightButStr,leftBtnClick);
	}
	/**
	 * 构建确认窗口  提示内容为自定义View 取消为关闭窗口
	 * @param context
	 * @param contentRes
	 * @param viewModel
	 * @param leftBtnClick
	 * @return
	 */
	public static CommonDialogViewModel createConfirmVDialog(IView view,Context context,int contentRes
			,BaseViewModel viewModel,OnBtnClickCallBack leftBtnClick){
		return createConfirmVDialog(view,context, getString(R.string.app_label), contentRes ,viewModel, getString(R.string.btn_text_confirm)
				, getString(R.string.btn_text_cancel),leftBtnClick);
	}
	/**
	 * 构建确认窗口  提示内容为自定义View 取消为关闭窗口
	 * @param context
	 * @param title
	 * @param contentRes
	 * @param viewModel
	 * @param leftBtnClick
	 * @return
	 */
	public static CommonDialogViewModel createConfirmVDialog(IView view,Context context,String title,int contentRes
			,BaseViewModel viewModel,OnBtnClickCallBack leftBtnClick){
		return createConfirmVDialog(view,context, title, contentRes ,viewModel, getString(R.string.btn_text_confirm)
				, getString(R.string.btn_text_cancel),leftBtnClick);
	}
	/**
	 * 构建确认窗口  提示内容为自定义View 取消为关闭窗口
	 * @param context
	 * @param title
	 * @param contentRes
	 * @param viewModel
	 * @param leftBtnClick
	 * @return
	 */
	public static CommonDialogViewModel createConfirmVDialog(IView view,Context context,int title,int contentRes
			,BaseViewModel viewModel,OnBtnClickCallBack leftBtnClick){
		return createConfirmVDialog(view,context, getString(title), contentRes ,viewModel, getString(R.string.btn_text_confirm)
				, getString(R.string.btn_text_cancel),leftBtnClick);
	}
	/**
	 * 构建确认窗口  提示内容为自定义View 取消为关闭窗口
	 * @param context
	 * @param title
	 * @param contentRes
	 * @param viewModel
	 * @param letfButStr
	 * @param rightButStr
	 * @param leftBtnClick
	 * @return
	 */
	public static CommonDialogViewModel createConfirmVDialog(IView view,Context context,int title,int contentRes
			,BaseViewModel viewModel,int letfButStr,int rightButStr,OnBtnClickCallBack leftBtnClick){
		return createConfirmVDialog(view,context, getString(title), contentRes , viewModel,getString(letfButStr)
				, getString(rightButStr),leftBtnClick);
	}
	/**
	 * 构建确认窗口  提示内容为自定义View 取消为关闭窗口
	 * @param context
	 * @param title
	 * @param contentRes
	 * @param viewModel
	 * @param letfButStr
	 * @param rightButStr
	 * @param leftBtnClick
	 * @return
	 */
	public static CommonDialogViewModel createConfirmVDialog(IView view,Context context,String title,int contentRes
			,BaseViewModel viewModel,String letfButStr,String rightButStr,OnBtnClickCallBack leftBtnClick){
		CommonDialogViewModel dialogViewModel = new CommonDialogViewModel(
				context, title, contentRes,viewModel, letfButStr, rightButStr, leftBtnClick
				, new OnBtnClickCallBack() {
					@Override
					public void onClick(View v, BaseDialogViewModel viewModel) {
						if(viewModel.isShowing()){
							viewModel.finish();
						}
					}
				});
		view.bindDialogViewModel(context, dialogViewModel);
		return dialogViewModel;
	}
	/**
	 * 构建等待窗口，无title，无按钮
	 * @param context
	 * @return
	 */
	public static CommonDialogViewModel createWaitingDialog(IView view,Context context){
		CommonDialogViewModel dialogViewModel = new CommonDialogViewModel(
				context, null, getString(R.string.waitting_dialog_load_tip), null, null, null,null);
		view.bindDialogViewModel(context, dialogViewModel);
		return dialogViewModel;
	}
	
	private static String getString(int resId){
		return BaseApplication.getInstance().getString(resId);
	}
}
