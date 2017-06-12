package com.lectek.android.lereader.binding.model.user;

import com.lectek.android.lereader.binding.model.bookmark_sys.SysBookMarkModel;
import com.lectek.android.lereader.binding.model.common.PagingLoadModel;
import com.lectek.android.lereader.net.response.DownloadInfo;
import com.lectek.android.lereader.presenter.DownloadPresenterLeyue;
import com.lectek.android.lereader.storage.dbase.digest.BookDigests;
import com.lectek.android.lereader.storage.dbase.digest.BookDigestsDB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MyDigestModelLeyue extends PagingLoadModel<BookDigests> {
	
	private int mPageSize = 1;
	private int mPageItemSize = Integer.MAX_VALUE;
	private int mLoadPage = 0;
	private int start = 0;
	private int totalCurPageCount;

	@Override
	protected ArrayList<BookDigests> onLoadCurrentPageData(int loadPage,
			int pageItemSize, Object... params) throws Exception {
        ArrayList<BookDigests> res = catalogDigest(BookDigestsDB.getInstance().getListBookDigests());
        if (res == null || res.size() == 0){
            int sleepTime = 0;
            while (SysBookMarkModel.getInstance().isSyncDigestMark() && sleepTime < 5000){//是否正在同步书签笔记信息，等待
                sleepTime += 300;
                Thread.sleep(300);
            }

            if (SysBookMarkModel.getInstance().isSyncDigestMark()){

            }else{
                //new GetBookDigestModel().onLoad();
                res = catalogDigest(BookDigestsDB.getInstance().getListBookDigests());
            }
        }
        return res;
	}
	
	private ArrayList<BookDigests> catalogDigest(ArrayList<BookDigests> orDigest){
		if(orDigest != null && orDigest.size() > 0){
			ArrayList<BookDigests> result = new ArrayList<BookDigests>();
            ArrayList<BookDigests> res = new ArrayList<BookDigests>();
			while(orDigest.size() > 0){
				BookDigests digest = orDigest.remove(0);
				int bookDigestCount = 1;
				long maxDate = digest.getDate();
				for (int i = 0; i < orDigest.size(); i++) {
					BookDigests model = orDigest.get(i);
					if(digest.getContentId().equals(model.getContentId())){
						orDigest.remove(i);
						bookDigestCount++;
						maxDate = Math.max(maxDate, model.getDate());
						i--;
					}
				}
				digest.setDate(maxDate);
				digest.setCount(bookDigestCount);
				result.add(digest);
			}
			ArrayList<DownloadInfo> downloadInfos = DownloadPresenterLeyue.loadDownLoadUnits(null, null, null);
            for (int i = 0; i < downloadInfos.size(); i++) {
                DownloadInfo info = downloadInfos.get(i);
                for (int j = 0; j < result.size(); j++) {
                    BookDigests digest = result.get(j);
                    if(digest.getContentId().equals(info.contentID)){
                        digest.setAuthor(info.logoUrl);
                        digest.setContentName(info.contentName);
                        res.add(digest);
                    }
                }
            }
		/*	for (int i = 0; i < result.size(); i++) {
				BookDigests digest = result.get(i);
				for (int j = 0; j < downloadInfos.size(); j++) {
					DownloadInfo info = downloadInfos.get(j);
					if(digest.getContentId().equals(info.contentID)){
						digest.setAuthor(info.logoUrl);
                        digest.setContentName(info.contentName);
                        if (TextUtils.isEmpty(digest.getChaptersName())){

                        }

                        res.add(digest);
					}
				}
			}*/
            ComparatorDigestTime comparator=new ComparatorDigestTime();
            Collections.sort(res, comparator);
			return res;
		}
		return null;
	}

    public class ComparatorDigestTime implements Comparator<BookDigests> {

        public int compare(BookDigests arg0, BookDigests arg1) {
            long res = arg0.getDate() - arg1.getDate();
            if(res == 0){
                return 0;
            }else{
                return res <0 ?1: -1;
            }
        }
    }
	@Override
	protected int getPageItemSize() {
		return mPageItemSize;
	}

	@Override
	protected Integer getPageSize() {
		return mPageSize;
	}

	@Override
	protected String getGroupKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isDataCacheEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean isValidDataCacheEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

}
