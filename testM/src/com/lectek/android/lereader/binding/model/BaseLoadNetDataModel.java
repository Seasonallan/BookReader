package com.lectek.android.lereader.binding.model;

import com.lectek.android.lereader.lib.utils.ApnUtil;
/**
 * 加载网络数据的Model基类
 * @author linyiwei
 *
 * @param <Result>
 */
public abstract class BaseLoadNetDataModel<Result> extends BaseLoadDataModel<Result>{
	/**
	 * 没有网络而导致无法启动任务
	 */
	public static final String START_RESULT_NOT_NET = "START_RESULT_NOT_NET";
	
	@Override
	protected String onStartPreCheck(Object... params) {
		if(!ApnUtil.isNetAvailable(getContext())){
			return START_RESULT_NOT_NET;
		}
		return super.onStartPreCheck(params);
	}
}
