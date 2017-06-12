package com.lectek.android.lereader.lib.recharge;

public class PayResultCode {
	public static final int RESULT_CODE_PAY_FAIL = 0x1;
	public static final int RESULT_CODE_PAY_SUCCESS = RESULT_CODE_PAY_FAIL + 1;
	public static final int RESULT_CODE_NOT_LOGIN = RESULT_CODE_PAY_SUCCESS + 1;	//用户尚未登录
	
	//支付宝 支付服务器 回调客户端 返回码
	public static final String RESPONSE_CODE_9000 = "9000";//操作成功 
	public static final String RESPONSE_CODE_4000 = "4000";//系统异常
	public static final String RESPONSE_CODE_4001 = "4001";//数据格式不正确
	public static final String RESPONSE_CODE_4003 = "4003";//支付宝账被冻结或不允许该用户绑定的支付
	public static final String RESPONSE_CODE_4004 = "4004";//该用户已解除绑定 
	public static final String RESPONSE_CODE_4005 = "4005";//绑定 失败或没有绑定 
	public static final String RESPONSE_CODE_4006 = "4006";//订单支付失败 
	public static final String RESPONSE_CODE_4010 = "4010";//重新绑定账户。
	public static final String RESPONSE_CODE_6000 = "6000";//支付服务正在进行升级操作。 
	public static final String RESPONSE_CODE_6001 = "6001";//用户中途取消支付操作。 
	public static final String RESPONSE_CODE_6002 = "6002";//网络连接异常。	
}
