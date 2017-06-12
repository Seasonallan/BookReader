package com.lectek.android.lereader.binding.model.imports;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.text.TextUtils;

import com.lectek.android.lereader.binding.model.BaseLoadDataModel;
import com.lectek.android.lereader.data.FileData;
import com.lectek.android.lereader.download.DownloadHttpEngine;
import com.lectek.android.lereader.net.response.DownloadInfo;
import com.lectek.android.lereader.presenter.DownloadPresenterLeyue;
import com.lectek.android.lereader.ui.basereader_leyue.Book;
import com.lectek.android.lereader.utils.CommonUtil;
import com.lectek.lereader.core.bookformats.BookInfo;
import com.lectek.lereader.core.bookformats.FormatPlugin;
import com.lectek.lereader.core.bookformats.PluginManager;

public class ImportBookModel extends BaseLoadDataModel<Boolean> {
	private DownloadInfo getDownloadInfo(Book book){
		DownloadInfo downloadInfo = new DownloadInfo();
		downloadInfo.filePathLocation = book.getPath();
		downloadInfo.contentID = book.getBookId();
		downloadInfo.isOrderChapterNum = book.getFeeStart();
		downloadInfo.isOrder = book.isOrder();
		downloadInfo.price = book.getPrice();
		downloadInfo.promotionPrice = book.getPromotionPrice();
		downloadInfo.contentName = book.getBookName();
		downloadInfo.authorName = book.getAuthor();
		downloadInfo.contentType = book.getBookType();
		downloadInfo.downloadType = book.getBookFormatType();
		downloadInfo.logoUrl = book.getCoverPath();
		return downloadInfo;
	}
	
	private Book getBook(FileData fileData){
		int start = fileData.mAbsolutePath.lastIndexOf(".");
		String postPath = fileData.mAbsolutePath.substring(start);
		Book book = new Book();
		book.setPath(fileData.mAbsolutePath);
		if(".epub".equalsIgnoreCase(postPath)){
			BookInfo bookInfo = null;
            String cover = null;
            InputStream coverStream = null;
            try {
                FormatPlugin plugin = PluginManager.instance().getPlugin(fileData.mAbsolutePath, null);
				bookInfo = plugin.getBookInfo();
                coverStream = plugin.getCoverStream();
                if (coverStream != null){
                    cover = fileData.mAbsolutePath;
                    CommonUtil.saveImage(cover.hashCode() +"", coverStream);
                }
			} catch (Exception e) {
			} finally {
				if(coverStream != null) {
					try {
						coverStream.close();
					}catch(IOException ie){}
				}
			}
            
			if(bookInfo != null) {
				book.setBookName(bookInfo.title);
				book.setAuthor(bookInfo.author);
			}
            book.setCoverPath(cover);
			book.setBookFormatType(DownloadHttpEngine.VALUE_DOWNLOAD_TYPE_BOOK);
			if(!TextUtils.isEmpty(fileData.mBookTitle) && TextUtils.isDigitsOnly(fileData.mBookTitle)){
				book.setBookId(fileData.mBookTitle);
			}else{
				book.setBookId(fileData.mAbsolutePath);
				book.setOrder(true);
			}
		}else{
			book.setBookName(fileData.mBookTitle);
			book.setBookId(fileData.mAbsolutePath);
			if(".txt".equalsIgnoreCase(postPath) || ".umd".equalsIgnoreCase(postPath)){
				book.setBookFormatType(DownloadHttpEngine.VALUE_DOWNLOAD_TYPE_TEXT_UND);
			}else if(".pdf".equalsIgnoreCase(postPath)){
				book.setBookFormatType(DownloadHttpEngine.VALUE_DOWNLOAD_TYPE_PDF);
			}
			book.setCoverPath("");
			book.setOrder(true);
		}
		return book;
	}
	
	public void addBooks(ArrayList<FileData> fileDatas){
		start(fileDatas);
	}
	
	public void addBook(FileData fileData){
        ArrayList<FileData> list = new ArrayList<FileData>();
        list.add(fileData);
        addBooks(list);
	}
	
	public Book deleteBook(FileData fileData){
		Book book = getBook(fileData);
		if(book != null){
			DownloadPresenterLeyue.deleteDB(getDownloadInfo(book));
		}
		return book;
	}

    /**
     * 最近添加的书籍信息
     */
    private Book mLatestBook;

    public Book getLatestBook() {
        return mLatestBook;
    }

    @Override
	protected Boolean onLoad(Object... params) throws Exception {
		ArrayList<FileData> fileDatas = (ArrayList<FileData>) params[0];
		for (FileData fileData : fileDatas) {
            mLatestBook = getBook(fileData);
			if(mLatestBook != null){
				DownloadPresenterLeyue.addLocalBook(getDownloadInfo(mLatestBook));
			}
		}
		return true;
	}
}
