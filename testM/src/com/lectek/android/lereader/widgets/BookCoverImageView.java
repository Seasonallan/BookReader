package com.lectek.android.lereader.widgets;

import android.content.Context;
import android.util.AttributeSet;

import com.lectek.android.widget.QuickImageView;

public class BookCoverImageView extends QuickImageView {

	private static final String ATTRIBUTE_NAMESPACE = "com.lectek.android.widgets.BookCoverImageView";
	private static final String ATTRIBUTE_DEFAULT_BOOK_COVER = "defaultBookCover";
	
	private int mDefaultBookCoverResID;
	
	public BookCoverImageView(Context context) {
		super(context);
	}

	public BookCoverImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		String defaultBookCoverUri = attrs.getAttributeValue(ATTRIBUTE_NAMESPACE, ATTRIBUTE_DEFAULT_BOOK_COVER);
		if(defaultBookCoverUri.startsWith("@drawable")) {
			mDefaultBookCoverResID = context.getResources().getIdentifier(defaultBookCoverUri.substring(1), "drawable", context.getPackageName());
		}else if(defaultBookCoverUri.startsWith("@color")) {}
	}

}
