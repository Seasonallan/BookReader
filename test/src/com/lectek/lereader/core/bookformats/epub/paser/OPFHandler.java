package com.lectek.lereader.core.bookformats.epub.paser;

import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.text.TextUtils;

import com.lectek.lereader.core.bookformats.BookInfo;
import com.lectek.lereader.core.bookformats.epub.ManifestResource;

/** 解析opf文件
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2013-2-21
 */
public class OPFHandler extends DefaultHandler {
	private static final String TAG_METADATA = "metadata";
	private static final String TAG_DC_TITLE = "dc:title";
	private static final String TAG_DC_CREATOR = "dc:creator";
	private static final String TAG_DC_PUBLISHER = "dc:publisher";
	private static final String TAG_DC_IDENTIFIER = "dc:identifier";
	private static final String TAG_DC_IS_MEDIA_DECODE = "dc:is_media_file_decode";//TODO:扩展版本号
	private static final String TAG_MANIFEST = "manifest";
	private static final String TAG_ITEM = "item";
	private static final String TAG_SPINE = "spine";
	private static final String TAG_ITEM_REF = "itemref";

	private static final String TAG_META = "meta"; 
	private static final String TAG_META_CARTOON = "contentFormat"; 
	
	private String contentStr;
	
	private boolean isMetadataBlock;
	private boolean isManifest; 
	private boolean isSpine;
	
	private BookInfo bookInfo;
	private String filePath;
	private String navFilePath;
	private String navFileId;
	private String ncxId;
	private HashMap<String, ManifestResource> idResources;
	private HashMap<String, ManifestResource> hrefResources;
	private ArrayList<String> mChapterIds;
	
	public OPFHandler(String filePath){
		this.filePath = filePath;
	}
	
	public String getNavFilePath(){
		return navFilePath;
	}
	/**
	 * @return the bookInfo
	 */
	public BookInfo getBookInfo() {
		return bookInfo;
	}
	
	public HashMap<String, ManifestResource> getIdResources(){
		return idResources;
	}
	
	/**
	 * @return the hrefResources
	 */
	public HashMap<String, ManifestResource> getHrefResources() {
		return hrefResources;
	}

	public String getNcxId(){
		return ncxId;
	}

	/**
	 * @return the spine
	 */
	public ArrayList<String> getChapterIds() {
		if(navFileId != null){
			mChapterIds.remove(navFileId);
			navFileId = null;
		}
		return mChapterIds;
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		String tagName = qName;
		if (tagName.equalsIgnoreCase(TAG_METADATA)){
			isMetadataBlock = false;
		} else if(tagName.equalsIgnoreCase(TAG_DC_TITLE)){
			if(isMetadataBlock && bookInfo != null && contentStr != null){
				bookInfo.title = contentStr;
			}
		} else if(tagName.equalsIgnoreCase(TAG_DC_CREATOR)){
			if(isMetadataBlock && bookInfo != null && contentStr != null){
				bookInfo.author = contentStr;
			}
		} else if(tagName.equalsIgnoreCase(TAG_DC_PUBLISHER)){
			if(isMetadataBlock && bookInfo != null && contentStr != null){
				bookInfo.publisher = contentStr;
			}
		} else if(tagName.equalsIgnoreCase(TAG_DC_IDENTIFIER)){
			if(isMetadataBlock && bookInfo != null && contentStr != null){
				bookInfo.id = contentStr;
			}
		}else if(tagName.equalsIgnoreCase(TAG_DC_IS_MEDIA_DECODE)){
			if(isMetadataBlock && bookInfo != null && contentStr != null){
				if ("0".equals(contentStr)) {
					bookInfo.isMediaDecode = false;
				}else {
					bookInfo.isMediaDecode = true;
				}
			}
		} else if(localName.equalsIgnoreCase(TAG_SPINE)){
			isSpine = false;
		} else if(localName.equalsIgnoreCase(TAG_MANIFEST)){
			isManifest = false;
		}
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		contentStr = new String(ch, start, length);
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (localName.equalsIgnoreCase(TAG_METADATA)){
			isMetadataBlock = true;
			bookInfo = new BookInfo();
		} else if(localName.equalsIgnoreCase(TAG_MANIFEST)){
			isManifest = true;
			idResources = new HashMap<String, ManifestResource>();
			hrefResources = new HashMap<String, ManifestResource>();
		} else if(localName.equalsIgnoreCase(TAG_ITEM)){
			if(isManifest){
				String href = attributes.getValue("href");
				String id = attributes.getValue("id");
				ManifestResource resource = new ManifestResource(filePath, href,id, attributes.getValue("media-type"));
				idResources.put(id, resource);
				hrefResources.put(href, resource);
				if("nav".equals(attributes.getValue("properties"))){
					navFilePath = href;
					navFileId = id;
				}
			}
		} else if(localName.equalsIgnoreCase(TAG_SPINE)){
			isSpine = true;
			mChapterIds = new ArrayList<String>();
			ncxId = attributes.getValue("toc");
		} else if(localName.equalsIgnoreCase(TAG_ITEM_REF)){
			if(isSpine){
				mChapterIds.add(attributes.getValue("idref"));
			}
		} else if(localName.equalsIgnoreCase(TAG_META)){
			String href = attributes.getValue("name");
			if(href != null && href.equals(TAG_META_CARTOON)){
				String content = attributes.getValue("content");
				if(!TextUtils.isEmpty(content)){
					bookInfo.isCartoon = "1".equals(content);
				} 
			}else if(href != null && href.equals("cover")){
				contentCover = attributes.getValue("content");
			}
		}  
		contentStr = null;
	}

	public String contentCover;
}
