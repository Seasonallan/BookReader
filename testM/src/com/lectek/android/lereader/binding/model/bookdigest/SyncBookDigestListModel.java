package com.lectek.android.lereader.binding.model.bookdigest;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

import com.lectek.android.lereader.binding.model.BaseLoadNetDataBackgroundModel;
import com.lectek.android.lereader.lib.storage.dbase.JsonArrayList;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.DigestResponse;
import com.lectek.android.lereader.net.response.SyncDigestResponse;
import com.lectek.android.lereader.storage.dbase.digest.BookDigests;
import com.lectek.android.lereader.storage.dbase.digest.BookDigestsDB;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;

/**
 * 上传本地未同步笔记信息到云端
 * @author ljp
 * @date 2014年4月29日
 * @email 451360508@qq.com.com
 */
public class SyncBookDigestListModel extends BaseLoadNetDataBackgroundModel<String> {

	@Override
	protected String onLoad(Object... params) {
        List<BookDigests> bookDigests;
        String bookId = null;
        if(params.length == 1){
            Object object = params[0];
            if(object instanceof  String && !TextUtils.isEmpty((String)params[0])){
                bookId = (String)params[0];
                bookDigests = BookDigestsDB.getInstance().getSyncBookDigests(bookId);
            }else{
                bookDigests = BookDigestsDB.getInstance().getSyncBookDigests();
            }
        }else{
            bookDigests = BookDigestsDB.getInstance().getSyncBookDigests();
        }
        if (bookDigests.size() == 0){
            return bookId;
        }
		JsonArrayList<DigestResponse> reqLists = new JsonArrayList<DigestResponse>(DigestResponse.class);
		String userId = PreferencesUtil.getInstance(getContext()).getUserId();
		for (int i = 0; i < bookDigests.size(); i++) {
			reqLists.add(new DigestResponse(bookDigests.get(i), i, userId));
		}
        try{
            ArrayList<SyncDigestResponse> res = ApiProcess4Leyue.getInstance(getContext()).syncBookDigestList(reqLists);
            for (int i = 0; i < res.size(); i++) {
                SyncDigestResponse response = res.get(i);
                String poStr = response.getSerial();
                if(!TextUtils.isEmpty(poStr)){
                    int position = Integer.parseInt(poStr);
                    BookDigests digest = reqLists.get(position).toBookDigest();
                    String result = response.getResult();
                    if(response.getAction().equals("add")){
                        if(!TextUtils.isEmpty(result)){
                            digest.setServerId(result);
                            digest.setStatus(BookDigestsDB.STATUS_SYNC);
                            BookDigestsDB.getInstance().updateBookDigest(digest, false);
                        }
                    }else if(!TextUtils.isEmpty(result) && result.equals("true")){
                        digest.setStatus(BookDigestsDB.STATUS_SYNC);
                        BookDigestsDB.getInstance().updateBookDigest(digest, false);
                    }
                }
            }
        }catch (Exception e){
            LogUtil.i("SyncBookDigestListModel exception occur>> "+e.toString());
        }
		return bookId;
	}

}
