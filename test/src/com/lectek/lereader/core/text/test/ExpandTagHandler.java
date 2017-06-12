package com.lectek.lereader.core.text.test;

import java.util.HashMap;

import android.text.Editable;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;

import com.lectek.lereader.core.text.Constant;
import com.lectek.lereader.core.text.html.DataProvider;
import com.lectek.lereader.core.text.html.HtmlParser.TagHandler;
import com.lectek.lereader.core.text.html.HtmlParser.TagInfo;
import com.lectek.lereader.core.text.style.AlignSpan;
import com.lectek.lereader.core.text.style.AsyncDrawableSpan;
import com.lectek.lereader.core.text.style.ClickAsyncDrawableSpan;


public class ExpandTagHandler implements TagHandler{
	private HashMap<String, Object> mSpanIDMap;
	private DataProvider mDataProvider;
	private boolean isNoteA;
	private String mFilterChildTag;
	private NoteSpan mNoteSpan;
	private VoiceSpan mVoiceSpan;
	private VideoSpan mVideoSpan;
	private boolean isFigure;
	private String figureTitle;
	private AsyncDrawableSpan figureImg;
	private boolean isImages;

	public ExpandTagHandler(DataProvider dataProvider){
		mDataProvider = dataProvider;
		mSpanIDMap = new HashMap<String, Object>();
	}
	
	@Override
	public boolean handleTag(TagInfo tagInfo,Editable editable,boolean isStart) {
		if(isStart){
			if("true".equalsIgnoreCase(tagInfo.getAttributes().getValue("data-audioRef"))){
				String id = tagInfo.getAttributes().getValue("href");
				if(!TextUtils.isEmpty(id)){
					VoiceSpan voiceSpan = (VoiceSpan) mSpanIDMap.get(id);
					if(voiceSpan == null){
						voiceSpan = new VoiceSpan("");
						mSpanIDMap.put(id, voiceSpan);
					}
					tagInfo.addStyle(voiceSpan);
					editable.append(Constant.REPLACEMENT_SPAN_CHAR);
					mFilterChildTag = tagInfo.getTag();
					return true;
				}
			}else if(tagInfo.getTag().equals("video")){
				String widthStr = tagInfo.getAttributes().getValue( "width");
				String heightStr = tagInfo.getAttributes().getValue( "height");
				Integer size = null;
				int width = 0;
				int height = 0;
				size = tagInfo.handleSize(widthStr);
				if(size != null){
					width = size;
				}
				size = tagInfo.handleSize(heightStr);
				if(size != null){
					height = size;
				}
				mVideoSpan = new VideoSpan("",width,height);
				tagInfo.addStyle(mVideoSpan);
				tagInfo.addStyle(new AlignSpan(AlignSpan.CENTER_ALIGN));
				editable.append(Constant.REPLACEMENT_SPAN_CHAR);
			}else if(tagInfo.getTag().equals("table")){
				String src = tagInfo.getAttributes().getValue("src");
				if(!TextUtils.isEmpty(src)){
					mFilterChildTag = tagInfo.getTag();
					tagInfo.addStyle(new ClickAsyncDrawableSpan(src,null, false,tagInfo.handleSize("100%"),0,mDataProvider));
					editable.append(Constant.REPLACEMENT_SPAN_CHAR);
				}
				return true;
			}else if(tagInfo.getTag().equals("figure")){
				isFigure = true;
			}else if(tagInfo.getTag().equals("a")){
				String type = tagInfo.getAttributes().getValue("epub:type");
				String id = tagInfo.getAttributes().getValue("href");
				if(!TextUtils.isEmpty(type) && !TextUtils.isEmpty(id) && type.equals("noteref")){
					NoteSpan noteSpan = (NoteSpan) mSpanIDMap.get(id);
					if(noteSpan == null){
						noteSpan = new NoteSpan("");
						mSpanIDMap.put(id, noteSpan);
					}
					tagInfo.addStyle(noteSpan);
					tagInfo.removeStyle(BackgroundColorSpan.class);
					editable.append(Constant.REPLACEMENT_SPAN_CHAR);
					isNoteA = true;
					return true;
				}
			}
		}else{
			if(tagInfo.getTag().equals("a") && isNoteA){
				isNoteA = false;
			}else if(tagInfo.getTag().equals("img")){
				if(isFigure){
					if(figureImg != null && !isImages){
						isImages = true;
						figureImg.setPresetWidth(tagInfo.handleSize("50%") - figureImg.getPaddingLeft() - figureImg.getPaddingRight());
						figureImg.setPresetHeight(0);
					}
					figureImg = tagInfo.findStyle(AsyncDrawableSpan.class);
					if(figureImg != null){
						if(isImages){
							figureImg.setPresetWidth(tagInfo.handleSize("50%") - figureImg.getPaddingLeft() - figureImg.getPaddingRight());
							figureImg.setPresetHeight(0);
						}
						if(!TextUtils.isEmpty(figureTitle)){
							figureImg.setTitle(figureTitle);
						}
					}
				}
			}else if(tagInfo.getTag().equals("figure")){
				isFigure = false;
				figureImg = null;
				figureTitle = null;
				isImages = false;
			}else if(tagInfo.getTag().equals("video")){
				mVideoSpan = null;
			}else if(tagInfo.getTag().equals("body")){
				mSpanIDMap.clear();
			}
		}
		return false;
	}

	@Override
	public boolean handleCharacters(TagInfo tagInfo,Editable editable,StringBuilder charContainer) {
		if(mFilterChildTag != null){
			return true;
		}
		if(mVoiceSpan != null){
			return true;
		}
		if(isNoteA){
			return true;
		}
		if(mNoteSpan != null){
			String note = mNoteSpan.getNote();
			if(TextUtils.isEmpty(note)){
				note = charContainer.toString();
			}else{
				note += charContainer.toString();
			}
			mNoteSpan.setNote(note);
			return true;
		}
		if(tagInfo.getTag().equals("figcaption")){
			if(figureImg != null){
				figureImg.setTitle(charContainer.toString());
			}else{
				figureTitle = charContainer.toString();
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isFilter(TagInfo tagInfo, boolean isStar) {
		if(isStar){
			if(tagInfo.getTag().equals("source")){
				String src = tagInfo.getAttributes().getValue("src");
				if(!TextUtils.isEmpty(src)){
					if(mVoiceSpan != null){
						mVoiceSpan.setSrc(src);
					}else if(mVideoSpan != null){
						mVideoSpan.setSrc(src);
					}
				}
				return true;
			}else if(tagInfo.getTag().equals("audio")){
				String id = tagInfo.getId();
				if(!TextUtils.isEmpty(id)){
					id = "#" + id;
					VoiceSpan voiceSpan = (VoiceSpan) mSpanIDMap.get(id);
					if(voiceSpan == null){
						voiceSpan = new VoiceSpan("");
						mSpanIDMap.put(id, voiceSpan);
					}
					mVoiceSpan = voiceSpan;
				}
				return true;
			}else if(tagInfo.getTag().equals("figcaption")){
				return true;
			}else if(tagInfo.getTag().equals("aside")){
				String id = tagInfo.getId();
				if(!TextUtils.isEmpty(id)){
					id = "#" + id;
					NoteSpan noteSpan = (NoteSpan) mSpanIDMap.get(id);
					if(noteSpan == null){
						noteSpan = new NoteSpan("");
						mSpanIDMap.put(id, noteSpan);
					}
					mNoteSpan = noteSpan;
				}
				return true;
			}
		}else{
			if(tagInfo.getTag().equals("audio")){
				mVoiceSpan = null;
				return true;
			}else if(tagInfo.getTag().equals("source")){
				return true;
			}else if(tagInfo.getTag().equals("figcaption")){
				return true;
			}else if(tagInfo.getTag().equals("aside")){
				mNoteSpan = null;
				return true;
			}
			if(mFilterChildTag != null && tagInfo.getTag().equals(mFilterChildTag)){
				mFilterChildTag = null;
			}
		}
		if(mVideoSpan != null && !tagInfo.getTag().equals("video")){
			return true;
		}
		if(mFilterChildTag != null){
			return true;
		}
		return false;
	}
	
	private float getFloat(String value,float defaultValue){
		if(!TextUtils.isEmpty(value)){
			return Float.valueOf(value);
		}
		return defaultValue;
	}
	
	private long getLong(String value,long defaultValue){
		if(!TextUtils.isEmpty(value) && TextUtils.isDigitsOnly(value)){
			return Long.valueOf(value);
		}
		return defaultValue;
	}
}
