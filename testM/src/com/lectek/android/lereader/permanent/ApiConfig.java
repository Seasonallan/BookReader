package com.lectek.android.lereader.permanent;

import com.lectek.android.lereader.application.MyAndroidApplication;
import com.lectek.android.lereader.lib.utils.PkgManagerUtil;

public class ApiConfig {

	/**邮箱注册*/
    public static final String EMAIL_REGISTER = "11";
    /**QQ注册*/
    public static final String QQ_REGISTER = "21";
    /**微博注册*/
    public static final String SINA_REGISTER = "22";
    /**天翼阅读*/
    public static final String TIAN_YI_REGISTER = "23";//用户来源（20：第三方，21：腾讯， 22：新浪，23：天翼阅读，24：移动设备身份识别码，25：爱动漫，26：微信）
    /** 设备唯一id注册 */
    public static final String DEVICEID_REGISTER = "24";//用户来源（20：第三方，21：腾讯， 22：新浪，23：天翼阅读，24：移动设备身份识别码，25：爱动漫，26：微信）
    /** 微信  */
    public static final String WEIXIN_REGISTER = "26";
    /**支付类型*/
    public static final String PURCHASE_TYPE  = "8";

    /**产品来源（0：乐阅平台）*/
    public static final int SOURCE_LEYUE = 0;
    /**产品来源（1：天翼阅读）*/
    public static final int SOURCE_SURFING = 1;
    /**产品来源（2：中信出版社）*/
    private static final int SOURCE_ZHONGXING = 2;
    /**产品来源（3：单本书）*/
    private static final int SOURCE_DBS = 3;
    /**产品来源（4：爱动漫）*/
    private static final int SOURCE_AI_DONG_MAN = 4;

    /**发布渠道*/
    public static final String PUBLISH_CHANNEL = PkgManagerUtil.getUmengChannelId(MyAndroidApplication.getInstance());

    /**销售渠道*/
    public static final String SALE_CHANNEL = PkgManagerUtil.getSaleChannelId(MyAndroidApplication.getInstance());

    /**产品来源*/
    public static final int PLATFORM_SOURCE = SOURCE_LEYUE;

    /**版本号（版本名称）_#AndroidManifest_versionName */
    public static final String VERSION_NAME = PkgManagerUtil.getApkVersionName(MyAndroidApplication.getInstance());

    /**版本序号_#AndroidManifest_versionCode*/
    public static final int VERSION_CODE = PkgManagerUtil.getApkVersionCode(MyAndroidApplication.getInstance());

    public static String VERSION = PkgManagerUtil.getApkVersion(MyAndroidApplication.getInstance());

//	public static final String URL = "http://220.160.111.214:28082/lereader";//乐阅测试服务器
    public static final String URL = "http://115.29.171.102:8081/lereader";//乐阅正式阿里服务器

}
