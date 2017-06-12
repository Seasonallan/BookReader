package com.lectek.lereader.core.text.test;

import com.lectek.lereader.core.text.style.ClickActionSpan;
import com.lectek.lereader.core.text.test.ReaderMediaPlayer.PlayerListener;

public interface IMediaSpan extends ClickActionSpan,PlayerListener,IMedia{
	public boolean isPlay();
	
	public long computePositionByLocal(int x,int y);
}
