package com.lectek.android.lereader.ui.basereader_leyue.bookmarks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.lectek.android.lereader.binding.model.bookmark.UserBookMarkModel;
import com.lectek.android.lereader.storage.dbase.digest.BookDigestsDB;
import com.lectek.android.lereader.storage.dbase.mark.BookMark;
import com.lectek.android.lereader.storage.dbase.mark.BookMarkDB;

/**
 * 用户书签所有数据控制
 * @author ljp
 * @since 2014年4月23日
 */
public class BookMarkDatas {
    private static BookMarkDatas mInstance;
    private BookMarkDB markHandle;
    private HashMap<String, Boolean> mFastCache;

    public static BookMarkDatas getInstance() {
        if(mInstance == null){
            mInstance = new BookMarkDatas();
        }
        return mInstance;
    }
    private BookMarkDatas(){
        markHandle = BookMarkDB.getInstance();
        mFastCache = new HashMap<String, Boolean>();
    }

    private List<BookMark> mBookMarks;

    /**
     * 清空缓存
     */
    public void clearCache(){
        markHandle = null;
        mFastCache.clear();
        mFastCache =  null;
        mInstance = null;
    }

    /**
     * 获取所有书签
     * @return
     */
    public List<BookMark> getBookMarks(){
        return mBookMarks;
    }

    /**
     * 加载所有用户书签
     * @param contentId
     */
    public void loadBookMarks(String contentId){
        if(contentId == null){
            return;
        }
        mBookMarks = markHandle.getUserBookMark(contentId);
    }

    /**
     * 获取某个位置的书签
     */
    public BookMark getBookMark(int position){
        if(mBookMarks == null || mBookMarks.size() < position){
            return null;
        }
        return mBookMarks.get(position);
    }

    //构建唯一码
    private String buildKey(int chapterId, int pageStart, int pageEnd){
        return chapterId +"-"+pageStart +"-"+pageEnd;
    }

    /**
     * 找到该书签页的结束字符位置
     */
    private int findPageEnd(int chapterId, int pageStart){
        String mapKey = chapterId +"-"+pageStart+"-";
        Set<Map.Entry<String, Boolean>> set = mFastCache.entrySet();
        for (Map.Entry<String, Boolean> entry : set){
            String key = entry.getKey();
            if (key.startsWith(mapKey)){
                try {
                    String res = key.substring(mapKey.length());
                    return Integer.parseInt(res);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return pageStart;
    }

    /**
     * 某个页面是否已经被标记为用户书签
     */
    public boolean isPageMarked(int chapterId, int pageStart, int pageEnd){
        String key = buildKey(chapterId, pageStart, pageEnd);
        if(mFastCache.containsKey(key)){
            return mFastCache.get(key);
        }
        boolean res = findBookMarkPosition(chapterId, pageStart, pageEnd) != -1;
        mFastCache.put(key, res);
        return res;
    }

    /**
     * 找到书签位置
     */
    private int findBookMarkPosition(int chapterId, int pageStart, int pageEnd){
        if(mBookMarks != null && mBookMarks.size() > 0){
            for(int i=0;i<mBookMarks.size();i++){
                BookMark bookMark = mBookMarks.get(i);
                if(bookMark.getChapterID() == chapterId){
                    int position = bookMark.getPosition();
                    if(position >= pageStart && position < pageEnd){
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * 添加用户书签
     */
    public boolean addBookMark(BookMark bookMark){
        if(bookMark == null){
            return false;
        }
        if(mBookMarks == null){
            mBookMarks = new ArrayList<BookMark>();
        }
        mFastCache.clear();
        mBookMarks.add(bookMark);
        bookMark.setStatus(BookDigestsDB.STATUS_LOCAL);
        markHandle.updateOrCreateUserBookMark(bookMark);
        UserBookMarkModel.getInstance().addBookMark(bookMark);
        return true;
    }

    /**
     * 删除用户书签【智能删除】
     */
    public boolean deleteBookMark(BookMark bookMark){
        if(mBookMarks == null || bookMark == null){
            return false;
        }
        int position = findBookMarkPosition(bookMark);
        return deleteBookMark(position);
    }
    /**
     * 删除用户书签
     */
    public boolean deleteBookMark(int position){
        mFastCache.clear();
        if(position != -1){
            BookMark bookMark = mBookMarks.remove(position);
            markHandle.softDeleteUserBookmark(bookMark);
            UserBookMarkModel.getInstance().deleteBookMark(bookMark);
            return true;
        }
        return false;
    }

    /**
     * 找到某个用户书签在所有书签里面的位置
     */
    public int findBookMarkPosition(BookMark bookMark){
        if(bookMark == null || mBookMarks == null || mBookMarks.size() == 0){
            return -1;
        }
        int endPosition = findPageEnd(bookMark.getChapterID(), bookMark.getPosition());
        return findBookMarkPosition(bookMark.getChapterID(), bookMark.getPosition(), endPosition);
/*        for(int i=0;i<mBookMarks.size();i++){
            BookMark mark = mBookMarks.get(i);
            if(bookMark.getContentID().equals(mark.getContentID())
                    && bookMark.getChapterID()== mark.getChapterID()
                    && bookMark.getPosition() == mark.getPosition()){
                return i;
            }
        }
        return -1;*/
    }

}


















