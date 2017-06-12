package com.lectek.android.lereader.net.response;

import java.io.Serializable;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;
import com.lectek.android.lereader.utils.AES;

public class DownloadInfo extends BaseDao implements Serializable {
	
	private static final long serialVersionUID = 5273531303125064656L;

//	public Object tag;
	
	@Json(name ="contentID")
	public String contentID;
	
	@Json(name ="contentName")
	public String contentName;
	
	@Json(name ="authorName")
	public String authorName;
	
	@Json(name ="url")
	public String url;
	
	@Json(name ="size")
	public long size;
	
	@Json(name ="downloadType")
	public String downloadType;
	
	@Json(name ="contentType")
	public String contentType;
	
	@Json(name ="filePathLocation")
	public String filePathLocation;
	
	@Json(name ="state")
	public int state;
	
	@Json(name ="id")
	public long id;
	
	@Json(name ="isOrder")
	public boolean isOrder;
	
	@Json(name ="current_size")
	public long current_size;
	
	@Json(name ="logoUrl")
	public String logoUrl;
	
	@Json(name ="timestamp")
	public long timestamp;
	
	@Json(name ="isOrderChapterNum")
	public String isOrderChapterNum;
	
	@Json(name ="price")
	public String price;
	
	@Json(name ="promotionPrice")
	public String promotionPrice;
	
	@Json(name ="secret_key")
	public String secret_key;
	
	@Json(name ="isDownloadFullVersonBook")
	public boolean isDownloadFullVersonBook;
	
	public DownloadInfo() {
		
	}
	public DownloadInfo(long long1, int int1, String string, long long2,
			long long3, String string2, String string3, String string4,
			String string5,String isOrder,String isOrderChapterNum,String price,String promotionPrice) {
		this.id = long1;
		this.state = int1;
		this.logoUrl = string;
		this.current_size = long2;
		this.size = long3;
		this.contentName = string2;
		this.contentID = string3;
		this.filePathLocation = string4;
		this.authorName = string5;
		this.isOrder = IS_ORDER.equals(isOrder)?true:false;
		this.isOrderChapterNum = isOrderChapterNum;
		this.promotionPrice = promotionPrice;
		this.price = price;
	}
	
	private static String IS_ORDER = "1";
	
	public String getDecryptSecret_key() {
		return AES.decrypt(secret_key);
	}
}
