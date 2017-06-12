package com.lectek.android.binding.command;

import gueei.binding.viewAttributes.view.TouchEventResult;
import android.view.MotionEvent;
import android.view.View;

public abstract class OnTouchCommand extends BaseCommand {
	@Override
	protected void onInvoke(View view, Object... args) {
		if(args == null || args.length < 2
				|| !(args[0] instanceof MotionEvent)
				|| !(args[1] instanceof TouchEventResult) ){
			return;
		}
		TouchEventResult result = (TouchEventResult)args[1];
		result.eventConsumed = onTouch(view, (MotionEvent)args[0]);
	}

	public abstract boolean onTouch(View v, MotionEvent event);
}
