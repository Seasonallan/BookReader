package com.lectek.android.lereader.data;

import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.net.response.*;
import com.lectek.android.lereader.storage.dbase.GroupMessage;
import com.lectek.android.lereader.storage.dbase.mark.BookMark;
import com.lectek.android.lereader.storage.dbase.mark.BookMarkDB;
import com.lectek.android.lereader.storage.dbase.util.GroupInfoDB;
import com.lectek.android.lereader.widgets.drag.IDragDatas;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 14-7-14.
 */
public class BookShelfItem implements IDragDatas{

    /**
     * 书架位置
     * @return
     */
	@Deprecated
    public double getShelfPosition(){
        if (mGroupMessage == null || mGroupMessage.defaultType == 0){
            return mBookMark.shelfPosition;
        }else{
            return mGroupMessage.shelfPosition;
        }
    }

    /**
     * 重置书架位置并保存
     * @param position
     */
	@Deprecated
    public void setShelfPosition(double position){
        if (mGroupMessage == null || mGroupMessage.defaultType == 0){
            mBookMark.shelfPosition = position;
            int res = BookMarkDB.getInstance().updateSysBookMarkPosition(mBookMark.contentID, position);
            LogUtil.i(""+res);
        }else{
            mGroupMessage.shelfPosition = position;
            GroupInfoDB.getInstance().updateSysBookMarkGroupPosition(mGroupMessage.groupId, position);
        }
    }

    /**
     * 是否已读
     * @return
     */
    public boolean isReaded(){
        if (mBookMark != null){
//            if (mBookMark.chapterID <= 0 && mBookMark.position <= 0){
//                return false;
//            }
//            if (mBookMark.chapterID > 0 || mBookMark.position > 0){
//                return true;
//            }
        	return mBookMark.chapterID > 0 || mBookMark.position > 0;
        }
        return false;
    }

    /**
     * 获取阅读进度
     * @return
     */
    public float getReadPercent(){
        if (mBookMark != null){
            int max = mBookMark.max;
            if (max != -1 && max != 0){
                if (max < 0){
                    int percent = mBookMark.position;
                    return Math.abs(((float)percent + 1)/((float)max));
                }else{
                    int percent = mBookMark.chapterID;
                    return Math.abs(((float)percent + 1)/((float)max));
                }
            }
        }
        return 0;
    }

    /**
     * 是否选中删除，用于展示
     */
    public boolean isDelSelect;

    /**
     * 下载状态，用于展示操作
     */
    public DownloadInfo mDownLoadInfo;

    /**
     * 是否为文件夹，用于展示操作
     */
    public boolean isFile;

    /**
     * 是否存在子文件夹之下，用于展示操作
     */
    public boolean isInFile;

    /**
     * 是否是添加按钮
     */
    public boolean isEmpty;

    /**
     * 文件书籍信息
     */
    public List<BookShelfItem> mItems;

    /**
     * 书签索引，用于数据操作
     */
    public BookMark mBookMark;

    /**
     * 组信息，用于数据操作
     */
    public GroupMessage mGroupMessage;

    /**
     * 添加文件书籍信息
     * @param item
     */
    public void addItem(BookShelfItem item){
        if (mItems == null || mItems.size() == 0){
            mItems = new ArrayList<BookShelfItem>();
            if (!isFile){
                BookShelfItem cop = copy();
                cop.isFile = false;
                cop.isInFile = true;
                mItems.add(cop);
            }
        }
        isFile = true;
        isInFile = false;
        if (item != null){
            mItems.add(0, item);
        }
    }

    @Override
    public boolean isFile() {
        return isFile;
    }

    @Override
    public boolean isInFile() {
        return isInFile;
    }

    @Override
    public boolean isEmpty() {
        return isEmpty;
    }


    public BookShelfItem copy(){
        BookShelfItem item = new BookShelfItem();
        item.mDownLoadInfo = mDownLoadInfo;
        item.mBookMark = mBookMark;
        item.mGroupMessage = mGroupMessage;
        return item;
    }

	@Override
	public String toString() {
		return "BookShelfItem [ isRead=" + isReaded() +" , mBookMark=" + mBookMark +"]";
	}
    
}













