/*
 * ========================================================
 * ClassName:Product.java* 
 * Description:
 * Copyright (C) 
 * ========================================================
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *===================================================================*
 * Revision History
 *
 * Modification                    Tracking
 * Date         Author          Number       Description of changes
 *____________________________________________________________________
 * 
 * 2013-9-11     chendt          #00000       create
 */
package com.lectek.android.lereader.net.response.tianyi;

import java.io.Serializable;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

public class MonthlyProduct extends BaseDao implements Serializable{
	
	public static final long serialVersionUID = -3658741927664062645L;

	/**包月产品id*/
	@Json(name ="month_product_id")
	public String monthProId;
	
	/**包月产品名称*/
	@Json(name ="month_product_name")
	public String monthProName;
	
	/**描述简介*/
//	@Json(name ="description")
	@Json(name ="month_product_description")
	public String description;
	
	/**价格*/
	@Json(name ="price")
	public String price;
	
	/**包月阅点价格*/
	@Json(name ="readpoint_price")
	public String readPointPrice;
	
	/**包月产品图片地址*/
	@Json(name ="cover_path")
	public String coverPath;
	
	/**包月产品详情的URl*/
	@Json(name ="wap_url")
	public String wapUrl;
	
	/**返回状态码*/
	@Json(name ="status")
	public String status;

	public String getMonthProId() {
		return monthProId;
	}

	public void setMonthProId(String monthProId) {
		this.monthProId = monthProId;
	}

	public String getMonthProName() {
		return monthProName;
	}

	public void setMonthProName(String monthProName) {
		this.monthProName = monthProName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getReadPointPrice() {
		return readPointPrice;
	}

	public void setReadPointPrice(String readPointPrice) {
		this.readPointPrice = readPointPrice;
	}

	public String getCoverPath() {
		return coverPath;
	}

	public void setCoverPath(String coverPath) {
		this.coverPath = coverPath;
	}

	public String getWapUrl() {
		return wapUrl;
	}

	public void setWapUrl(String wapUrl) {
		this.wapUrl = wapUrl;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Product [monthProId=" + monthProId + ", monthProName="
				+ monthProName + ", description=" + description
				+ ", price=" + price + ", readPointPrice=" + readPointPrice
				+ ", coverPath=" + coverPath + ", wapUrl=" + wapUrl
				+ ", status=" + status + "]";
	}
}