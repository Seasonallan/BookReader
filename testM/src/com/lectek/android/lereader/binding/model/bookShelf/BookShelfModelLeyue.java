package com.lectek.android.lereader.binding.model.bookShelf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.lectek.android.lereader.binding.model.BaseLoadDataModel;
import com.lectek.android.lereader.data.BookShelfItem;
import com.lectek.android.lereader.net.response.DownloadInfo;
import com.lectek.android.lereader.permanent.DownloadAPI;
import com.lectek.android.lereader.presenter.DownloadPresenterLeyue;
import com.lectek.android.lereader.storage.dbase.GroupMessage;
import com.lectek.android.lereader.storage.dbase.mark.BookMark;
import com.lectek.android.lereader.storage.dbase.mark.BookMarkDB;
import com.lectek.android.lereader.storage.dbase.util.GroupInfoDB;

public class BookShelfModelLeyue extends BaseLoadDataModel<ArrayList<BookShelfItem>> {

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
	
    public GroupMessage getGroupMessage(List<GroupMessage> list, BookMark bookMark){
        if (bookMark != null && list != null && list.size() > 0){
            for (GroupMessage groupMessage: list){
                if (bookMark.groupId == groupMessage.groupId){
                    return groupMessage;
                }
            }
        }
        return null;
    }

    public BookShelfItem getBookShelfItem(List<BookShelfItem> list, GroupMessage groupMessage){
        if (groupMessage != null && list != null && list.size() > 0){
            for (BookShelfItem bookShelfItem: list){
                if (bookShelfItem.mGroupMessage != null && groupMessage.groupId == bookShelfItem.mGroupMessage.groupId){
                     return bookShelfItem;
                }
            }
        }
        return null;
    }

//    class NumberController{
//        public HashMap<Double, String> maps = new HashMap<Double, String>();
//        public boolean contains(Double num){
//            return maps.containsKey(num);
//        }
//        public void put(Double num){
//            maps.put(num, "");
//        }
//        public Double getNum(){
//            double res = new Random().nextDouble();
//            while (contains(res) || res < 0){
//                res =  new Random().nextDouble();
//            }
//            return res;
//        }
//    }

	@Override
	protected ArrayList<BookShelfItem> onLoad(Object... params) throws Exception {
		ArrayList<DownloadInfo> downloadInfos = DownloadPresenterLeyue.loadDownLoadUnits(null, null, DownloadAPI._ID + " DESC");
        List<BookMark> bookMarks = BookMarkDB.getInstance().getAllSysBookMark("_id DESC");
        List<GroupMessage> groupMessages = GroupInfoDB.getInstance().getMessageInfos();

        ArrayList<BookShelfItem> bookShelfItems = new ArrayList<BookShelfItem>();
//        if(downloadInfos == null)
//            return null;

        //过滤重复数据
        HashMap<String, DownloadInfo> maps = new HashMap<String, DownloadInfo>();
        for(int i = 0; i < downloadInfos.size();i++){
            //如果数据重复了HashMap的put方法会返回重复数据，但是不会添加重复数据到HashMap中
            DownloadInfo downloadInfo = maps.put(downloadInfos.get(i).contentID, downloadInfos.get(i));
            if(downloadInfo != null){
                downloadInfos.remove(i);
                i--;
            }
        }
        
//        if (bookMarks != null && bookMarks.size() >= 1){
//                int size = bookMarks.size();
//            NumberController numberController = new NumberController();
//                for(int i = 0; i< size;i++){
//                    BookMark bookMark1 = bookMarks.get(i);
//                    if (bookMark1.shelfPosition  == -1){
//                        bookMark1.shelfPosition = numberController.getNum();
//                        BookMarkDB.getInstance().updateSysBookMarkPosition(bookMark1.contentID, bookMark1.shelfPosition);
//                    }else{
//                        numberController.put(bookMark1.shelfPosition);
//                    }
//                }
//                if (groupMessages != null){
//                    for(int i = 0; i< groupMessages.size();i++){
//                        GroupMessage groupMessage = groupMessages.get(i);
//                        if (groupMessage.shelfPosition  == -1){
//                            groupMessage.shelfPosition = numberController.getNum();
//                            GroupInfoDB.getInstance().updateSysBookMarkGroupPosition(groupMessage.groupId, groupMessage.shelfPosition);
//                        }else{
//                            numberController.put(groupMessage.shelfPosition);
//                        }
//                    }
//                }
//        }

        for(BookMark bookMark: bookMarks){
        	GroupMessage groupMessage = getGroupMessage(groupMessages, bookMark);
        	DownloadInfo refDownloadInfo = getDownloadInfo(downloadInfos, bookMark);
        	if (refDownloadInfo == null){
                refDownloadInfo = new DownloadInfo();
                refDownloadInfo.contentID = bookMark.contentID;
                refDownloadInfo.contentName = bookMark.contentName;
                refDownloadInfo.logoUrl = bookMark.logoUrl;
            }
            if (groupMessage == null || groupMessage.defaultType == 0){//默认展开分组
                BookShelfItem bookShelfItem = new BookShelfItem();
                bookShelfItem.isFile = false;
                bookShelfItem.mDownLoadInfo = refDownloadInfo;
                bookShelfItem.mGroupMessage = groupMessage;
                bookShelfItem.mBookMark = bookMark;
                bookShelfItems.add(bookShelfItem);
            }else{//文件夹
                BookShelfItem bookShelfItem = getBookShelfItem(bookShelfItems, groupMessage);
                
                BookShelfItem bookShelfItem_ = new BookShelfItem();
                bookShelfItem_.isFile = false;
                bookShelfItem_.isInFile = true;
                bookShelfItem_.mDownLoadInfo = refDownloadInfo;
                bookShelfItem_.mGroupMessage = groupMessage;
                bookShelfItem_.mBookMark = bookMark;
                
                if (bookShelfItem != null){
//                    BookShelfItem bookShelfItem_ = new BookShelfItem();
//                    bookShelfItem_.isFile = false;
//                    bookShelfItem_.isInFile = true;
//                    bookShelfItem_.mDownLoadInfo = refDownloadInfo;
//                    bookShelfItem_.mGroupMessage = groupMessage;
//                    bookShelfItem_.mBookMark = bookMark;
                    bookShelfItem.addItem(bookShelfItem_);
                }else{
                    bookShelfItem = new BookShelfItem();
                    bookShelfItem.isFile = true;
                    bookShelfItem.isInFile = false;
                    bookShelfItem.mDownLoadInfo = refDownloadInfo;
                    bookShelfItem.mGroupMessage = groupMessage;
                    bookShelfItem.mBookMark = bookMark;

                    bookShelfItem.addItem(bookShelfItem_);
                    bookShelfItems.add(bookShelfItem);

                }
            }
        }
        
//        //过滤重复数据
//        HashMap<String, DownloadInfo> maps = new HashMap<String, DownloadInfo>();
//        for(int i = 0; i < downloadInfos.size();i++){
//            //如果数据重复了HashMap的put方法会返回重复数据，但是不会添加重复数据到HashMap中
//            DownloadInfo downloadInfo = maps.put(downloadInfos.get(i).contentID, downloadInfos.get(i));
//            if(downloadInfo != null){
//                downloadInfos.remove(i);
//                i--;
//            }
//        }
//        for(DownloadInfo downloadInfo: downloadInfos){
//            BookMark bookMark = getBookMark(bookMarks, downloadInfo);
//            if (bookMark == null){
//                bookMark = new BookMark();
//                bookMark.setContentID(downloadInfo.contentID);
//                bookMark.setStatus(BookDigestsDB.STATUS_LOCAL);
//                bookMark.setSoftDelete(DBConfig.BOOKMARK_STATUS_SOFT_DELETE_NO);
//                bookMark.setBookmarkType(DBConfig.BOOKMARK_TYPE_SYSTEM);
//                BookMarkDB.getInstance().createSysBookMarkIfNotExit(bookMark);
//            }
//            if (bookMark != null){
//                GroupMessage groupMessage = getGroupMessage(groupMessages, bookMark);
//                if (groupMessage == null || groupMessage.defaultType == 0){//默认展开分组
//                    BookShelfItem bookShelfItem = new BookShelfItem();
//                    bookShelfItem.isFile = false;
//                    bookShelfItem.mDownLoadInfo = downloadInfo;
//                    bookShelfItem.mGroupMessage = groupMessage;
//                    bookShelfItem.mBookMark = bookMark;
//                    bookShelfItems.add(bookShelfItem);
//                }else{//文件夹
//                    BookShelfItem bookShelfItem = getBookShelfItem(bookShelfItems, groupMessage);
//                    if (bookShelfItem != null){
//                        BookShelfItem bookShelfItem_ = new BookShelfItem();
//                        bookShelfItem_.isFile = false;
//                        bookShelfItem_.isInFile = true;
//                        bookShelfItem_.mDownLoadInfo = downloadInfo;
//                        bookShelfItem_.mGroupMessage = groupMessage;
//                        bookShelfItem_.mBookMark = bookMark;
//                        bookShelfItem.addItem(bookShelfItem_);
//                    }else{
//                        bookShelfItem = new BookShelfItem();
//                        bookShelfItem.isFile = true;
//                        bookShelfItem.isInFile = false;
//                        bookShelfItem.mDownLoadInfo = downloadInfo;
//                        bookShelfItem.mGroupMessage = groupMessage;
//                        bookShelfItem.mBookMark = bookMark;
//
//                        bookShelfItem.addItem(null);
//                        bookShelfItems.add(bookShelfItem);
//
//                    }
//                }
//            }
//        }
//        Collections.sort(bookShelfItems, new Comparator<BookShelfItem>() {
//            @Override
//            public int compare(BookShelfItem bookShelfItem, BookShelfItem bookShelfItem2) {
//                Double p1 = bookShelfItem.getShelfPosition();
//                Double p2 = bookShelfItem2.getShelfPosition();
//
//                return p1.compareTo(p2);
//            }
//        });
//        if (bookShelfItems != null && bookShelfItems.size() > 0){
//            BookMarkDB.getInstance().resetShelfPosition(bookShelfItems.get(0).getShelfPosition());
//        }
        BookShelfItem empty = new BookShelfItem();
        empty.isEmpty = true;
        bookShelfItems.add(empty);
		return bookShelfItems;
	}

}
