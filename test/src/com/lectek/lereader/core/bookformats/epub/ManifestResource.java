package com.lectek.lereader.core.bookformats.epub;


/** 保存EPUB的资源
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2013-2-21
 */
public class ManifestResource extends Resource {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2492192809223663472L;
	protected String id;
	protected String mediaType;
	
	public ManifestResource(String filePath, String href, String id, String mediaType){
		super(filePath, href);
		this.id = id;
		this.mediaType = mediaType;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the mediaType
	 */
	public String getMediaType() {
		return mediaType;
	}
}
