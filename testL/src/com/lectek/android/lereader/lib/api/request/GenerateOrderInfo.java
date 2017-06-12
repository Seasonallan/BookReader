package com.lectek.android.lereader.lib.api.request;

public class GenerateOrderInfo {

	
	//用户ID
	private String userId = "";
	//书籍ID
	private String bookId = "";
	//卷ID
	private String volumnId = "";
	//章节ID
	private String chapterId = "";
	//频道ID
	private String channelId = "";
	//计费类型（0：免费，1：按书，2：按卷，3：按章，4：按频道）
	private Integer calType = 0;
	//计费对象（按书时，bookId，按卷，volumnId，按章，chapterId，按频道，channelId）
	private String calObj = "";
	//费用
	private Double charge = 0.0;
	//购买人
	private String purchaser = "";
	//
	private Integer purchaseType = 13;
	//0：乐阅平台、1：天翼阅读、2：中信出版社、3：单本书，4：爱动漫，5：中信书院，6：沃阅读
	private Integer sourceType = 0;
	//客户端版本号
	private String version = "";
	//鉴权串BASE64(3DES(userId+ bookId + volumnId + chapterId + channelId + calType + calObj + charge+ purchaser+sourceType+version+key)，key)
	private String authorcator = "";
	//账号（客户端用户账号）
	private String account = "";
	//书籍所属cpid
	private String cpid = "";
	//计费对象名称
	private String calObjName = "";
	//发布渠道
	private String releaseChannel = "";
	//销售渠道
	private String salesChannel = "";

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getBookId() {
		return bookId;
	}

	public void setBookId(String bookId) {
		this.bookId = bookId;
	}

	public String getVolumnId() {
		return volumnId;
	}

	public void setVolumnId(String volumnId) {
		this.volumnId = volumnId;
	}

	public String getChapterId() {
		return chapterId;
	}

	public void setChapterId(String chapterId) {
		this.chapterId = chapterId;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public Integer getCalType() {
		return calType;
	}

	public void setCalType(Integer calType) {
		this.calType = calType;
	}

	public String getCalObj() {
		return calObj;
	}

	public void setCalObj(String calObj) {
		this.calObj = calObj;
	}

	public Double getCharge() {
		return charge;
	}

	public void setCharge(Double charge) {
		this.charge = charge;
	}

	public String getPurchaser() {
		return purchaser;
	}

	public void setPurchaser(String purchaser) {
		this.purchaser = purchaser;
	}

	public Integer getSourceType() {
		return sourceType;
	}

	public void setSourceType(Integer sourceType) {
		this.sourceType = sourceType;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getAuthorcator() {
		return authorcator;
	}

	public void setAuthorcator(String authorcator) {
		this.authorcator = authorcator;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getCpid() {
		return cpid;
	}

	public void setCpid(String cpid) {
		this.cpid = cpid;
	}

	public String getCalObjName() {
		return calObjName;
	}

	public void setCalObjName(String calObjName) {
		this.calObjName = calObjName;
	}

	public String getReleaseChannel() {
		return releaseChannel;
	}

	public void setReleaseChannel(String releaseChannel) {
		this.releaseChannel = releaseChannel;
	}

	public String getSalesChannel() {
		return salesChannel;
	}

	public void setSalesChannel(String salesChannel) {
		this.salesChannel = salesChannel;
	}

	public Integer getPurchaseType() {
		return purchaseType;
	}

	public void setPurchaseType(Integer purchaseType) {
		this.purchaseType = purchaseType;
	}

}
