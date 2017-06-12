package com.lectek.android.lereader.ui.basereader_leyue.span;


public interface IMedia {
	public String getVoiceSrc();

	public long getStartPosition();
	
	public boolean contains(long position);
}
