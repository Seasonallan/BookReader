/*
 * ========================================================
 * ClassName:BookDecodeInfo.java* 
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
 * 2013-10-11     chendt          #00000       create
 */
package com.lectek.android.lereader.net.response;

import java.io.UnsupportedEncodingException;

import android.text.TextUtils;

import com.lectek.android.app.BaseApplication;
import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;
import com.lectek.android.lereader.lib.utils.Base64;
import com.lectek.android.lereader.lib.utils.EncryptUtils;
import com.lectek.android.lereader.lib.utils.PkgManagerUtil;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.request.LeyueRequestMessage;
import com.lectek.android.lereader.permanent.ApiConfig;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
/**
 * @description
	书籍解密 密钥
 * @author chendt
 * @date 2013-10-11
 * @Version 1.0
 */
public class BookDecodeInfo extends BaseDao {
 
	
	/**服务端返回秘钥信息(需解密)*/
	@Json(name = "secretKey")
	public String secretKey;
	
	/**校验码（用于验证密钥合法性）*/
	@Json(name = "secretKeyDigest")
	public String secretKeyDigest;
	
	public transient String bookId;
	
	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getSecretKeyDigest() {
		return secretKeyDigest;
	}

	public void setSecretKeyDigest(String secretKeyDigest) {
		this.secretKeyDigest = secretKeyDigest;
	}
	
	public String getBookId() {
		return bookId;
	}

	public void setBookId(String bookId) {
		this.bookId = bookId;
	}
	
	@Override
	public String toString() {
		return "BookDecodeInfo [secretKey=" + secretKey + ", secretKeyDigest="
				+ secretKeyDigest + ", bookId=" + bookId + "]";
	}

	/**
	 * 获取解密后的 书籍密钥
	 * @param bookId
	 * @return
	 */
	public String getEncodeSecretKey(){
		String REK = EncryptUtils.encodeByMd5(PkgManagerUtil.getApkInfo(BaseApplication.getInstance()).versionCode+
								PreferencesUtil.getInstance(BaseApplication.getInstance()).getUserId()+
								bookId+
								LeyueConst.KEY);
		if (REK.length()>16) {
			REK = REK.substring(0, 16);
		}
		if (isSecretKeyValid()) {
			byte[] content = null;
			content = Base64.decode(secretKey);
			String result;
			try {
				result = new String(EncryptUtils.decryptByAES(content, REK),"utf-8");
				return result;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} 
		}
		return null;
	}
	
	/**
	 * 根据密钥信息，判断返回的密钥是否合法
	 * @param REK
	 * @return
	 */
	public boolean isSecretKeyValid(){
		if (TextUtils.isEmpty(secretKeyDigest)) {
			return false;
		}
		String base643DesRestult = EncryptUtils.encryptBase643DES(LeyueRequestMessage.getSourceForDecodekey(
				ApiConfig.VERSION_CODE,
				PreferencesUtil.getInstance(BaseApplication.getInstance()).getUserId(),
				bookId), 
			LeyueConst.KEY);
		return secretKeyDigest.equals(base643DesRestult);
	}
}
