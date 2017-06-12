package com.lectek.android.lereader.lib.api;

import com.lectek.android.lereader.lib.api.request.GenerateOrderInfo;
import com.lectek.android.lereader.lib.api.response.OrderResponse;
import com.lectek.android.lereader.lib.api.response.ResponsePlayStatusInfo;
import com.lectek.android.lereader.lib.api.response.ScoreExchangeBookResultInfo;
import com.lectek.android.lereader.lib.net.exception.GsonResultException;
import com.lectek.android.lereader.lib.recharge.OrderInfo;

public interface IOrderRecharge {

	/**
	 * 短代的 2.27.1 下单
	 *
	 * @return
	 * @throws GsonResultException
	 */
	public OrderResponse generateOrder(GenerateOrderInfo goi) throws GsonResultException;

	/**
	 * 短代的2.27.2 更新订单
	 *
	 * @return
	 * @throws GsonResultException
	 */
	public OrderResponse updateOrder(Integer orderId, String orderNo) throws GsonResultException;
	
	/**
	 * 根据订单id查看订单详情接口。
	 * 
	 * @param orderId 订单id
	 * @return
	 * @throws GsonResultException
	 */
	public OrderInfo getOrderInfoById(String orderId) throws GsonResultException ;
	
	/**乐阅获取订单详情
     * @param bookId 书籍id
     * @param calType 计费类型(0：免费，1：按书，2：按卷，3：按章，4：按频道)
     * @param calObj 计费对象(按书时，bookId，按卷，volumnId，按章，chapterId，按频道，channelId)
     * @param charge 费用
    鉴权串BASE64(3DES(userId+ bookId + volumnId + chapterId + channelId + calType + calObj + charge+ purchaser+sourceType+version+key)，key)
     * @return
     * @throws GsonResultException
     */
    public OrderInfo getOrderInfoLeyue(String bookId,int calType,
                                       String calObj, String charge,String sourceType,int version,String bookName, int purchaseType)throws GsonResultException;
    
    /**
     * 客户端支付完回调服务端
     * @param tradeNo
     * @param payPlatform
     * @param payType
     * @param payTradeType
     * @param payGoodName
     * @param payGoodDesc
     * @param paymentAmount
     * @return
     */
    public ResponsePlayStatusInfo responseClientPlayStatus(String tradeNo,String payPlatform,String payType
            ,String payTradeType,String payGoodName,String payGoodDesc,Double paymentAmount) throws GsonResultException;
    
    /**
     * 用积分兌换书籍
     * @param bookId
     * @param calType 计费类型（1：按书，2：按卷，3：按章，4：包月）
     * @param calObj 计费对象（按书时，bookId，按卷，volumnId，按章，chapterId，按频道，channelId）
     * @param charge 所耗积分
     * @param purchaseType 支付类型（8积分订购）
     * @param calObjName 计费对象名称
     * */
    public ScoreExchangeBookResultInfo getScoreExchangeBook(String bookId,int calType,String calObj, int charge,String purchaseType,
                                                            String calObjName)throws GsonResultException;
}
