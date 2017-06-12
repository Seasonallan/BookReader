package com.lectek.android.lereader.net.openapi;

import java.util.ArrayList;

import com.lectek.android.lereader.lib.api.surfing.IOrderRechargeTY;
import com.lectek.android.lereader.lib.net.exception.GsonResultException;
import com.lectek.android.lereader.net.response.tianyi.Chapter;
import com.lectek.android.lereader.net.response.tianyi.ContentInfo;
import com.lectek.android.lereader.net.response.tianyi.OrderedResult;
import com.lectek.android.lereader.net.response.tianyi.PayAfterResult;
import com.lectek.android.lereader.net.response.tianyi.PayResult;
import com.lectek.android.lereader.net.response.tianyi.PointRule;
import com.lectek.android.lereader.storage.dbase.TianYiUserInfo;

/**
 *  天翼阅读开放平台网络请求接口
 * @author laijp
 * @date 2014-5-26
 * @email 451360508@qq.com
 */
public interface IHttpRequest4TianYi extends IOrderRechargeTY{

    /** 获取<登录>用户信息接口
     * @return
     */
    public TianYiUserInfo queryUserInfo(String accessToken);

    /** 获取内容封面接口
     * @param contentId 内容ID
     * @param coverSize 封面分辨率类型
    1、480*640
    2、124*165
    3、84*112
    4、62*83
    5、48*64
     * @return
     */
    public String getContentCover(String contentId, int coverSize);

    /** 获取内容基本信息接口
     * @param contentId 内容ID
     * @return ContentInfo 如果获取不到数据，返回null
     */
    public ContentInfo getBaseContent(String contentId) ;

    /** 获取内容目录总数接口
     * @param contentId 内容ID
     * @return ArrayList<Chapter>
     * @deprecated
     */
    public ArrayList<Chapter> getBookChapterList(String contentId);


    /** 获取内容目录总数接口
     * @param contentId 内容ID
     * @return ArrayList<Chapter>
     * @deprecated
     */

    public ArrayList<Chapter> getBookChapterListNew(String contentId, int start, int count) ;

    /** 获取内容章节信息接口
     * @param contentId 内容ID
     * @param chapterId 章节ID
     * @return Chapter 返回章节内容，如果获取不到返回null
     */
    public Chapter getChapterInfo(String contentId, String chapterId) ;

    /** 判断内容是否被订购（is_content_ordered）
     * @param contentId 内容ID
     * @return
     */
    public OrderedResult isContentOrdered(String contentId);

    /** 阅点点播内容接口
     * @param contentId 内容ID
     * @param chapterId 章节ID；如果是按本购买为空
     * @return OrderedResult；返回购买结果
     * @throws GsonResultException
     */
    public OrderedResult subscribeContentReadpoint(String contentId, String chapterId) throws GsonResultException ;


    /**阅点包月*/
    @Deprecated
    public OrderedResult subscribeMonthProductReadpoint(String productId) throws GsonResultException;


    /** 发起支付
     * @param productName 产品名称
     * @param costType 资费类型(点播(章、本)、包月)1、包月2、按本3、按章
     * @param bookId 书籍ID ,当cost_type为2、3时必须传入
     * @param chapterId 书籍章节ID,当cost_type为3时必须传入
     * @param colId 包月ID,当cost_type为1时必须传入
     * @return
     * @throws GsonResultException
     */
    public PayResult payConfirm(String productName, int costType, String bookId, String chapterId, String colId, String userId) throws GsonResultException ;


    /**
     * 积分兑换阅点接口
     * @param ruleId
     */
    public OrderedResult pointConvert(String ruleId) throws GsonResultException;


    /**
     * 支付
     * @param url
     * @return
     * @throws GsonResultException
     */
    public PayAfterResult pay(String url) throws GsonResultException;


    public ArrayList<PointRule> getPointRuleList();
}



	





















