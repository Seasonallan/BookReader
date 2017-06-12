/*
 * ========================================================
 * ClassName:ContentInfoModelLeyue.java* 
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
 * 2013-9-28     chendt          #00000       create
 */
package com.lectek.android.lereader.binding.model.contentinfo;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.lib.recharge.OrderInfo;
import com.lectek.android.lereader.lib.utils.PkgManagerUtil;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.permanent.LeyueConst;

/**
 * @description

 * @author chendt
 * @date 2013-9-28
 * @Version 1.0
 * @SEE ContentInfoViewModelLeyue
 */
public class OrderInfoModelLeyue extends BaseLoadNetDataModel<OrderInfo> {
	
	
	@Override
	protected OrderInfo onLoad(Object... params) throws Exception {
		OrderInfo info = null;
		if (params!=null && params.length >0) {
			String bookId = (String) params[0];
			String charge = (String) params[1];
			int calType = (Integer) params[2];//计费类型(0：免费，1：按书，2：按卷，3：按章，4：按频道)
			String bookName = (String) params[3];
			String calObj = bookId;//计费对象(按书时，bookId，按卷，volumnId，按章，chapterId，按频道，channelId)
			int purchaseType = (Integer) params[4];//支付类型(2:支付宝，9：沃支付)
			info = ApiProcess4Leyue.getInstance(getContext()).getOrderInfoLeyue(
								bookId, calType, calObj, charge, LeyueConst.SOURCE_TYPE, 
								PkgManagerUtil.getApkInfo(getContext()).versionCode,bookName, purchaseType);
		}
		return info;
	}

}
