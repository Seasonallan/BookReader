package com.lectek.android.binding.command;

import android.view.View;
import android.widget.AbsListView;

public abstract class OnScrollStateChangedCommand extends BaseCommand {
	@Override
	protected void onInvoke(View view, Object... args) {
		if(args == null || args.length < 1
				|| !(view instanceof AbsListView)
				|| !(args[0] instanceof Integer)){
			return;
		}
		onScrollStateChanged((AbsListView)view,(Integer)args[0]);
	}
	
	public abstract void onScrollStateChanged(AbsListView view, int scrollState);
}
