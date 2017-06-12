package com.lectek.android.binding.command;

import android.view.View;
import android.widget.AdapterView;

public abstract class OnItemClickCommand extends BaseCommand {
	@Override
	protected void onInvoke(View view, Object... args) {
		if(args == null || args.length < 3
				|| !(view instanceof AdapterView<?>)
				|| !(args[0] instanceof View)
				|| !(args[1] instanceof Integer)
				|| !(args[2] instanceof Long)){
			return;
		}
		onItemClick((AdapterView<?>)view,(View)args[0],(Integer)args[1],(Long)args[2]);
	}
	
	public abstract void onItemClick(AdapterView<?> parent, View view, int position, long id);
}
