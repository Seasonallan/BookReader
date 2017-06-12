package com.lectek.android.binding.command;

import gueei.binding.Command;
import android.view.View;
/**
 * 指令基类
 * @author linyiwei
 *
 */
public abstract class BaseCommand extends Command{
	
	@Override
	public final void Invoke(View view, Object... args) {
		onInvoke(view, args);
	}
	
	protected abstract void onInvoke(View view, Object... args);
}
