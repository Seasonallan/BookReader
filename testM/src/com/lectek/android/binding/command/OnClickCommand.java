package com.lectek.android.binding.command;

import android.view.View;
/**
 * 点击事件指令
 * @author linyiwei
 */
public abstract class OnClickCommand extends BaseCommand{
	@Override
	protected void onInvoke(View view, Object... args) {
		onClick(view);
	}
	
	public abstract void onClick(View v);
}
