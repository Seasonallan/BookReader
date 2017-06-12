package com.lectek.android.binding.command;

import android.view.View;
import android.widget.RatingBar;

public abstract class OnRatingChangedCommand extends BaseCommand {
	@Override
	protected void onInvoke(View view, Object... args) {
		if(args == null || args.length < 2
				|| !(view instanceof RatingBar)
				|| !(args[0] instanceof Float)
				|| !(args[1] instanceof Boolean)){
			return;
		}
		onRatingChanged((RatingBar)view,(Float)args[0],(Boolean)args[1]);
	}
	
	public abstract void onRatingChanged(RatingBar ratingBar, float rating,boolean fromUser);
}
