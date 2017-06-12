package com.lectek.android.lereader.binding.model.bookShelf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.lectek.android.lereader.binding.model.BaseLoadDataModel;
import com.lectek.android.lereader.data.BookShelfItem;
import com.lectek.android.lereader.net.response.DownloadInfo;
import com.lectek.android.lereader.permanent.DBConfig;
import com.lectek.android.lereader.presenter.DownloadPresenterLeyue;
import com.lectek.android.lereader.storage.dbase.GroupMessage;
import com.lectek.android.lereader.storage.dbase.digest.BookDigestsDB;
import com.lectek.android.lereader.storage.dbase.mark.BookMark;
import com.lectek.android.lereader.storage.dbase.mark.BookMarkDB;

/**
 * 获取最新阅读书籍Model
 * 
 * @author yangwq
 * @date 2014年7月16日
 * @email 57890940@qq.com
 */
public class BookShelfRecentReadModelLeyue extends BaseLoadDataModel<ArrayList<BookShelfItem>> {

//    private BookMark getBookMark(List<BookMark> list, DownloadInfo downloadInfo){
//        if (downloadInfo != null && list != null && list.size() > 0){
//            for (BookMark bookMark: list){
//                if (downloadInfo.contentID.equals(bookMark.contentID)){
//                    return bookMark;
//                }
//            }
//        }
//        return null;
//    }

	private DownloadInfo getDownloadInfo(List<DownloadInfo> list, BookMark bookMark){
      if (bookMark != null && list != null && list.size() > 0){
          for (DownloadInfo downloadInfo: list){
              if (bookMark.contentID.equals(downloadInfo.contentID)){
                  return downloadInfo;
              }
          }
      }
      return null;
  }
	
	@Override
	protected ArrayList<BookShelfItem> onLoad(Object... params) throws Exception {
		ArrayList<DownloadInfo> downloadInfos = DownloadPresenterLeyue.loadDownLoadUnits(null, null, null);
        List<BookMark> bookMarks = BookMarkDB.getInstance().getRecentReadBooks();

        ArrayList<BookShelfItem> bookShelfItems = new ArrayList<BookShelfItem>();

        //过滤重复数据
        if(downloadInfos != null) {
        HashMap<String, DownloadInfo> maps = new HashMap<String, DownloadInfo>();
	        for(int i = 0; i < downloadInfos.size();i++){
	            //如果数据重复了HashMap的put方法会返回重复数据，但是不会添加重复数据到HashMap中
	            DownloadInfo downloadInfo = maps.put(downloadInfos.get(i).contentID, downloadInfos.get(i));
	            if(downloadInfo != null){
	                downloadInfos.remove(i);
	                i--;
	            }
	        }
        }
        for(BookMark bookMark: bookMarks){
            DownloadInfo downloadInfo = getDownloadInfo(downloadInfos, bookMark);
            BookShelfItem bookShelfItem = new BookShelfItem();
            bookShelfItem.isFile = false;
            bookShelfItem.mDownLoadInfo = downloadInfo;
            bookShelfItem.mGroupMessage = null;
            bookShelfItem.mBookMark = bookMark;
            bookShelfItems.add(bookShelfItem);
            
        }
		return bookShelfItems;
	}

}
