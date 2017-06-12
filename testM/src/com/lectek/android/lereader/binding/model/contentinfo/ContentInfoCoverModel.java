package com.lectek.android.lereader.binding.model.contentinfo;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.net.openapi.ApiProcess4TianYi;

/**
 * @author mingkg21
 * @email  mingkg21@gmail.com
 * @date   2013-8-30
 */
public class ContentInfoCoverModel extends BaseLoadNetDataModel<String> {
	@Override
	protected String onLoad(Object... params) throws Exception {
		if(params != null && params.length > 0){
			String contentId = (String) params[0];
			String coverPath = ApiProcess4TianYi.getInstance(getContext()).getContentCover(contentId, 2);
			return coverPath;
		}else{
			return null;
		}
	}

}
