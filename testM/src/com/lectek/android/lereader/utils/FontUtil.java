package com.lectek.android.lereader.utils;

import android.app.Activity;
import android.content.Context;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.lectek.android.LYReader.R;

/** 阅读字体的工具类
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2011-12-21
 */
public class FontUtil {
	public static final float FONT_LINE_SPACE_TYPE_1 = 0f;
	public static final float FONT_LINE_SPACE_TYPE_2 = 0.5f;
	public static final float FONT_LINE_SPACE_TYPE_3 = 1f;
	
	public static final int FONT_SIZE_MIN = 14;
	public static final int FONT_SIZE_MAX = 34;
	public static final int FONT_SIZE_NUM = FONT_SIZE_MAX - FONT_SIZE_MIN;
	public static final int FONT_SIZE_DEFALUT = FONT_SIZE_MIN + (FONT_SIZE_NUM >> 1) - 6;
	/**
	 * 字体大小变化间隔
	 */
	public static final int FONT_CHANGE_SIZE = 2;
	
	public static int getReaderChapterFontSize(Context context){
		return getSize(context, FONT_SIZE_DEFALUT);
	}

	
	public static int formaSize(Context context,int size){
		int tempSize = FONT_SIZE_MIN + size;
		int fontSize = getSize(context, tempSize);
		return fontSize;
	}

	public static int getFontSize(Context context, int size){
		int fontSize = getSize(context, size);
		return fontSize;
	}

	private static int getSize(Context context, int size){
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return (int) (dm.density * size + 0.5f);
	}
	
	//修改字体颜色
	public static SpannableStringBuilder textSpannableStringBuilder(boolean isNeedColor,String value,int color,int start,int end){
		if (TextUtils.isEmpty(value) || start > value.length()) {
			return null;
		}
		SpannableStringBuilder style = new SpannableStringBuilder(value);
		style.setSpan(new ForegroundColorSpan(color), start, end , Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
		if(isNeedColor){
			style.setSpan(new AbsoluteSizeSpan(20), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return style;
	}
	
	public static void setTextSpanDefault(Activity context,View view, String value, int start, int end, final IActionClickSpan onActionClickSpan){
		setTextSpan(context, view, -1, value, start, end, true, onActionClickSpan);
	}
	
	public static void setTextSpanWithColor(Activity context,View view, int color, String value, int start, int end, final IActionClickSpan onActionClickSpan){
		setTextSpan(context, view, color, value, start, end, true, onActionClickSpan);
	}
	
	public static void setTextSpanWithColorWithUnderLine(Activity context,View view, int color, String value, int start, int end, final IActionClickSpan onActionClickSpan){
		setTextSpan(context, view, color, value, start, end, false, onActionClickSpan);
	}
	
	public static void setTextSpan(Activity context,View view, 
			int color, String value, int start, int end, boolean isUnderLine,
			final IActionClickSpan onActionClickSpan){
		//XXX 暂时去除下划线
		isUnderLine = false;
		SpannableString ss = new SpannableString(value);
		ss.setSpan(new InternalURLSpan(context, color, isUnderLine, new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(onActionClickSpan != null){
					onActionClickSpan.onActionClick(v);
				}
			}
		}), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		if(view instanceof TextView){
			TextView tempView = (TextView)view;
			tempView.setText(ss);
			tempView.setMovementMethod(LinkMovementMethod.getInstance());
		}
	}

	public static class InternalURLSpan extends ClickableSpan {
		private Activity mContext;
		private OnClickListener mListener;
		private int mColor;
		private boolean isUnderLine;
		@Override
		public void updateDrawState(TextPaint ds) {
			ds.setAntiAlias(true);
			if(mColor != -1){
				ds.linkColor = mColor;
			}else{
				ds.linkColor = mContext.getResources().getColor(R.color.book_price_color);
			}
			super.updateDrawState(ds);
			ds.setUnderlineText(isUnderLine);
		}

		public InternalURLSpan(Activity context, OnClickListener listener){
			this(context, -1, true, listener);
		}
		
		public InternalURLSpan(Activity context, int color, boolean isUnderLine, OnClickListener listener) {
			this.mListener = listener;
			this.mContext = context;
			this.mColor = color;
			this.isUnderLine = isUnderLine;
		}

		@Override
		public void onClick(View widget) {
			if(mListener != null){
				mListener.onClick(widget);
			}
		}
	}
	
	public interface IActionClickSpan{
		public void onActionClick(View v);
	}
}
