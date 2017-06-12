package com.lectek.android.binding.command;

import android.view.View;

public abstract class OnLongClickCommand extends BaseCommand{
	@Override
	protected void onInvoke(View view, Object... args) {
		onLongClick(view);
	}
	
	public abstract boolean onLongClick(View v);
}
