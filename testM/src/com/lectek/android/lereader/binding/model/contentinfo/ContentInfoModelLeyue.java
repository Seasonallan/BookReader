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
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.openapi.ApiProcess4TianYi;
import com.lectek.android.lereader.net.openapi.ResponseCode;
import com.lectek.android.lereader.net.response.tianyi.ContentInfo;
import com.lectek.android.lereader.net.response.tianyi.OrderedResult;
import com.lectek.android.lereader.ui.model.dataDefine.BookDetailData;
import com.lectek.android.lereader.utils.UserManager;

/**
 * @description

 * @author chendt
 * @date 2013-9-28
 * @Version 1.0
 * @SEE ContentInfoViewModelLeyue
 */
public class ContentInfoModelLeyue extends BaseLoadNetDataModel<BookDetailData> {

	@Override
	protected BookDetailData onLoad(Object... params) throws Exception {
		BookDetailData info = new BookDetailData();
		
		//获取书籍详情
//		if (params!=null && params.length >0) {
//			String bookId = (String) params[0];
//			String userId = (String) params[1];
//			info.bookInfo = ApiProcess4Leyue.getInstance(getContext()).getContentInfo(bookId, userId);
//		}
		
		if (params != null && params.length >0) {
			
			boolean isSurfingBook = ((Boolean)params[0]).booleanValue();
			String bookId = (String) params[1];
			String userId = (String) params[2];
			
			//获取书籍详情
			if(!isSurfingBook) {
				info.bookInfo = ApiProcess4Leyue.getInstance(getContext()).getContentInfo(bookId, userId);
			}else if(params.length >= 4){
				String contentId = (String) params[4];
				ContentInfo contentInfo = ApiProcess4TianYi.getInstance(getContext()).getBaseContent(contentId);
		        if(contentInfo != null
		                && !UserManager.getInstance(getContext()).isGuset()){
		            OrderedResult orderedResult = ApiProcess4TianYi.getInstance(getContext()).isContentOrdered(contentId);
		            if(orderedResult != null && orderedResult.getCode() == ResponseCode.HAD_EXIST_ORDERED){
		                contentInfo.setOrdered(true);
		            }
		        }
		        
		        info.tyBookInfo = contentInfo;
			}
			
			//获取书籍评论
			
			int count = 30;
			if(params.length >= 2){
				count = Integer.parseInt(params[3].toString());
			}
			try {
				info.bookComments = ApiProcess4Leyue.getInstance(getContext()).getLatestBookCommentListByBookId(bookId, 0, count);
			}catch(Exception e){}
			try {
				info.recommendBooks = ApiProcess4Leyue.getInstance(getContext()).getRecommendedBookByBookId(bookId,0,8);
			}catch(Exception e){}
			try {
				info.tagInfos = ApiProcess4Leyue.getInstance(getContext()).getBookTagsByBookId(bookId);
			}catch(Exception e){}
		}
		
		return (info.bookInfo != null || info.tyBookInfo != null) ? info : null;
	}

}
