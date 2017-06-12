package com.lectek.android.lereader.ui.common.dialog;

import android.content.Context;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.BaseViewModel;
import com.lectek.android.lereader.binding.model.common.CommonDialogViewModel;
import com.lectek.android.lereader.binding.viewModel.BaseDialog;
/**
 * 通用Dialog
 * @author linyiwei
 */
public class CommonDialog extends BaseDialog {
	public static final boolean bindViewModel(Context context, BaseViewModel viewModel) {
		if(viewModel instanceof CommonDialogViewModel){
			new CommonDialog(context,(CommonDialogViewModel) viewModel);
		}
		return false;
	}
	
	protected CommonDialogViewModel mViewModel;
	
	public CommonDialog(Context context,CommonDialogViewModel viewModel) {
		super(context,R.style.CustomDialog);
		mViewModel = viewModel;
		mViewModel.bindDialog(this);
		setContentView(bindView(R.layout.common_dialog_layout, null, mViewModel));
	}

	@Override
	public boolean bindDialogViewModel(Context context, BaseViewModel baseViewModel) {
		return bindViewModel(context, baseViewModel);
	}

	@Override
	public int getRes(String type) {
		return super.getRes(type);
	}
}
