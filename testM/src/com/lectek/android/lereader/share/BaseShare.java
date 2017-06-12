package com.lectek.android.lereader.share;

import android.graphics.Bitmap;

import com.lectek.android.lereader.share.entity.MutilMediaInfo;

/**
 * @description

 * @author chendt
 * @date 2013-11-26
 * @Version 1.0
 */
public abstract class BaseShare {

	/**
	 * @param context
	 *            上下文
	 * @param info
	 *            封装好的 信息实体
	 * @param listener
	 *            返回成功接口会返回成功信息，错误会返回ShareError对象
	 */
	public abstract void sendText(MutilMediaInfo info);

	/**
	 * @param context
	 *            上下文
	 * @param info
	 *            封装好的 信息实体
	 * @param listener
	 *            返回成功接口会返回成功信息，错误会返回ShareError对象
	 */
	public abstract void sendTextWithPic(MutilMediaInfo info);

	/**
	 * @param context
	 *            上下文
	 * @param info
	 *            封装好的 信息实体
	 * @param listener
	 *            返回成功接口会返回成功信息，错误会返回ShareError对象
	 */
	public abstract void sendTextWithBtimap(MutilMediaInfo info,Bitmap bitmap);
	

}
