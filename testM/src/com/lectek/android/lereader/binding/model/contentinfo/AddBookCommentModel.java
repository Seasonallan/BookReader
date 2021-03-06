package com.lectek.android.lereader.binding.model.contentinfo;

import android.text.TextUtils;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.account.AccountManager;
import com.lectek.android.lereader.binding.model.BaseLoadNetDataModel;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;

public class AddBookCommentModel extends BaseLoadNetDataModel<Boolean> {

	@Override
	protected Boolean onLoad(Object... params) throws Exception {
		String bookId = params[0].toString();
		String content = params[1].toString();
		int ratingValue = Integer.parseInt(params[2].toString());
		
		String userId = PreferencesUtil.getInstance(getContext()).getUserId();
		String username = PreferencesUtil.getInstance(getContext()).getUserName();
		String userNickname = PreferencesUtil.getInstance(getContext()).getUserNickName();
		String password = PreferencesUtil.getInstance(getContext()).getUserPSW();
		
		userNickname = userNickname != null ? userNickname.trim() : null;
		if(TextUtils.isEmpty(userNickname)){
			userNickname = AccountManager.getInstance().getUserInfo().getAccount();
		}
		
		if(AccountManager.getInstance().isVisitor()){
			userNickname = getContext().getResources().getString(R.string.no_login_visitor);
		}
		
		return ApiProcess4Leyue.getInstance(getContext()).addComment(bookId, null, content, userId, "0", ratingValue+"", userNickname, username, password);
	}

}
