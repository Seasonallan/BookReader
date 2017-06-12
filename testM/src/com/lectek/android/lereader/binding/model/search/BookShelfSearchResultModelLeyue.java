package com.lectek.android.lereader.binding.model.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.lectek.android.lereader.binding.model.BaseLoadDataModel;
import com.lectek.android.lereader.data.BookShelfItem;
import com.lectek.android.lereader.net.response.DownloadInfo;
import com.lectek.android.lereader.permanent.DBConfig;
import com.lectek.android.lereader.presenter.DownloadPresenterLeyue;
import com.lectek.android.lereader.storage.dbase.GroupMessage;
import com.lectek.android.lereader.storage.dbase.digest.BookDigestsDB;
import com.lectek.android.lereader.storage.dbase.mark.BookMark;
import com.lectek.android.lereader.storage.dbase.mark.BookMarkDB;
import com.lectek.lereader.core.util.LogUtil;

public class BookShelfSearchResultModelLeyue extends
		BaseLoadDataModel<ArrayList<BookShelfItem>> {

	@Override
	protected ArrayList<BookShelfItem> onLoad(Object... params)
			throws Exception {
		ArrayList<DownloadInfo> downloadInfos = DownloadPresenterLeyue.searchBook((String) params[0],
				 DownloadPresenterLeyue.SEARCH_TYPE_ALL);
		List<BookMark> bookMarks = BookMarkDB.getInstance().getAllSysBookMark(null);
		ArrayList<BookShelfItem> bookShelfItems = new ArrayList<BookShelfItem>();

		for(DownloadInfo downloadInfo: downloadInfos){
            BookMark bookMark = getBookMark(bookMarks, downloadInfo);
            if (bookMark == null){
                bookMark = new BookMark();
                bookMark.setContentID(downloadInfo.contentID);
                bookMark.setStatus(BookDigestsDB.STATUS_LOCAL);
                bookMark.setSoftDelete(DBConfig.BOOKMARK_STATUS_SOFT_DELETE_NO);
                bookMark.setBookmarkType(DBConfig.BOOKMARK_TYPE_SYSTEM);
                BookMarkDB.getInstance().createSysBookMarkIfNotExit(bookMark);
            }
            
            BookShelfItem bookShelfItem = new BookShelfItem();
	          bookShelfItem.isFile = false;
	          bookShelfItem.mDownLoadInfo = downloadInfo;
	          bookShelfItem.mBookMark = bookMark;
	          bookShelfItems.add(bookShelfItem);
        }
		return bookShelfItems;
	}
	private BookMark getBookMark(List<BookMark> list, DownloadInfo downloadInfo){
        if (downloadInfo != null && list != null && list.size() > 0){
            for (BookMark bookMark: list){
                if (downloadInfo.contentID.equals(bookMark.contentID)){
                    return bookMark;
                }
            }
        }
        return null;
    }
}
