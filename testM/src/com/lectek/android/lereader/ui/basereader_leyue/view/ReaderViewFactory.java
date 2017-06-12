package com.lectek.android.lereader.ui.basereader_leyue.view;

import android.app.Activity;
import android.content.Context;

import com.lectek.android.lereader.download.DownloadHttpEngine;
import com.lectek.android.lereader.ui.basereader_leyue.Book;
import com.lectek.android.lereader.ui.basereader_leyue.view.IReaderView.IReadCallback;

/**
 * @author linyiwei
 */
public final class ReaderViewFactory {
	public static IReaderView newReaderView(Context context, Book book, String secretKey,IReadCallback readCallback){

        if( book.getBookFormatType() == null){
            return new EpubReadView(context, book, readCallback);
        }
		if(DownloadHttpEngine.VALUE_DOWNLOAD_TYPE_BOOK.equals(book.getBookFormatType())){
            if(book.isOnline()){
                return new NetEpubReadView(context, book, readCallback);
            }
			/*try {
				FormatPlugin plugin = PluginManager.instance().getPlugin(book.getPath(), secretKey);
				BookInfo bookInfo = plugin.getBookInfo();
				if(bookInfo.isCartoon){
					return new EpubCartoonView(context, book, readCallback); 
				}
			} catch (Exception e) {
				e.printStackTrace();
			}*/
			return new EpubReadView(context, book, readCallback); 
		}else if(DownloadHttpEngine.VALUE_DOWNLOAD_TYPE_TEXT_UND.equals(book.getBookFormatType())){
			return new ReadTxtView(context, book, readCallback);
		}else if(DownloadHttpEngine.VALUE_DOWNLOAD_TYPE_PDF.equals(book.getBookFormatType())){ 
			return new PdfReadView((Activity)context, book, readCallback); 
		}else if(DownloadHttpEngine.VALUE_DOWNLOAD_TYPE_CEB.equals(book.getBookFormatType())){ 
			return new NetCebReadView(context, book, readCallback);
		}
		return null;
	}
}
