package com.lectek.android.lereader.net.openapi;

/** 状态码
 * @author mingkg21
 * @email  mingkg21@gmail.com
 * @date   2013-8-30
 */
public final class ResponseCode {
	
	public static final int OK = 0; //成功返回结果！
	public static final int ORDER_OK = 1; //订购成功
	public static final int UNSUPPORT_MOTHOD = 2; //协议不支持的请求方法
	public static final int EXCEPTION = 10001; //无法预料的异常
	public static final int INTPU_PARMAS = 10002; //参数输入有误
	public static final int DATA_ERROR = 10003;	//数据错误
	public static final int NOT_RETURN_VALUE = 10004; //无返回值
	public static final int PASSOWRD_ERROR = 10101; //用户密码不正确
	public static final int ACCOUT_HAD_EXIST = 10102; //用户账号已存在
	public static final int ACCOUT_NO_EXIST = 10103; //用户账号不存在
	public static final int ACCOUT_NOT_BE_USE = 1005; //用户账号已暂停
	public static final int ACCOUT_HAD_CANCEL = 1006; //用户账号已注销
	public static final int ACCOUT_HAD_LOCK = 1008; //用户账户被锁定
	public static final int ACCOUT_NO_RECOGNITION = 10005; //用户身份无法识别
	public static final int TRY_AGAIN_AFTER_LOGIN = 10006; //请登录再试
	public static final int USE_ID_CANNOT_BE_NULL = 10007; //用户ID不能为空
//	10008	书籍ID不能为空
//	10009	系统书签添加失败
//	10010	系统书签删除失败
//	10011	用户书签删除失败
//	10012	用户书签添加失败
//	10013	书签ID为空
//	10014	删除指定书签失败
//	10015	用户没有订购当前书籍
//	10016	用户信息修改成功
//	10017	用户信息修改失败
//	10018	不存在的排行
//	10019	没有数据返回
//	10020	不存在的推荐榜单
//	10021	当前章节为收费章节，用户未登录
//	10022	不是该用户的书签
//	10023	订购失败
//	10024	不存在订购关系
//	10025	用户已经签到
//	10026	非法的用户标识
//	10027	其他服务器错误
//	10028	用户收藏删除失败
//	10029	用户多个收藏记录删除失败
//	10030	用户指定收藏ID记录删除失败
//	10031	用户收藏添加失败
//	10032	添加收藏达到上限
//	10033	添加收藏记录重复
//	10034	同步书摘失败
//	10035	添加书摘失败
//	10036	删除书摘失败
//	10037	章节ID不能为空
//	10038	书摘内容超出范围
//	10100	评论操作上限
//	10101	用户禁言
//	10102	内容禁止评论
//	10103	内容所在频道达到评论上限
//	10104	积分系列规则不存在或无效
//	10105	评论增加积分失败
//	10106	已经投票或者投票失败
//	10107	内容Id为空或者非法的内容格式
//	10108	内容Id不存在
//	10109	频道Id为空或者非法的频道格式
//	10110	频道Id不存在
//	10111	分栏Id为空或者非法的分栏格式
//	10112	分栏Id不存在
//	10113	当前传入的章节ID不存在
//	10114	书签已经存在
//	10115	章节不存在
//	10116	达到用户书签数量上限
	public static final int INSUFFICIENT_BALANCE = 10117;//	10117	余额不足
//	10118	兑换数量超出单次上限
//	10119	用户积分不足
//	10120	用户id不存在
//	10121	积分兑换规则不存在
//	10122	参数为空
//	10123	兑换数量大于剩余可兑换数量
//	10124	获取阅点卷失败
//	10125	兑换数量超出上限
//	10126	查询剩余阅点卷数量失败
//	10127	剩余阅点卷可兑换数量小于需要兑换的数量
//	10128	用户经验等级不符合兑换规则中的用户经验等级
//	10129	兑换成功
//	10130	时间格式非法
//	10131	时间格式长度不为八位
//	10132	结束时间小于起始时间
//	10133	按本计费无需章节Id
//	10134	按章计费章节Id不为空
//	10135	输入渠道错误
//	10136	阅点券卡号与密码错误
//	10137	此阅点券已经被充值
//	10138	此阅点券已经超过充值截止日期
//	10139	订单信息参数不完整
//	10140	操作失败
	public static final int HAD_EXIST_ORDERED = 10141; //已经存在订购关系
//	10200	用户密码修改失败
//	10201	添加书签异常
//	10202	添加书签达到上限
//	10203	用户删除自己的评论失败
//	10204	添加消费者积分失败
//	10205	用户签到失败
//	10206	在此ruleName下不存在签到规则
//	10207	不存/暂停 ruleName的积分规则
	/**积分格式错误*/
	public static final int SCORE_FORMAT_ERROR = 20303; //
	/**支付积分与后台实际扣减积分不一致*/
	public static final int SCORE_HAS_CHANGE = 20304; //
	/**用户实际积分不够兌换该书*/
	public static final int SCORE_NOT_ENOUGH = 20305; //
	/**书籍已经购买了，不需要再次购买*/
	public static final int BOOK_HAS_BOUGHT = 20306; //
}
