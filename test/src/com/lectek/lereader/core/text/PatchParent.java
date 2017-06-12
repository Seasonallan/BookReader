package com.lectek.lereader.core.text;

import com.lectek.lereader.core.text.layout.AbsPatch;

/**
 * 定义DrawUnit可以访问上层哪些功能，整个绘制流程间的交互结构就是由DrawParent和DrawUnit组织构架起来的
 * @author lyw
 * TODO 载体View、控制刷新界面的方法、点击事件的传递
 */
public interface PatchParent{
	/**
	 * 通知DrawParent刷新界面
	 * @param dirty  刷新区域如果为空代表全屏刷新
	 */
	public void invalidate(AbsPatch patch);
}
