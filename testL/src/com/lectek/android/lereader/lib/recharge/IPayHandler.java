package com.lectek.android.lereader.lib.recharge;



/**
 * 支付流程处理接口
 * 
 * @author yangwq
 * @date 2014年6月18日
 * @email 57890940@qq.com
 */
public interface IPayHandler {

	public void execute(IDealPayRunnable dealPayRunnable);
	public void abort();
	public int getPayType();
	public boolean isAbort();
}
