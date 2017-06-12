package com.lectek.android.lereader.ui.basereader_leyue.span;

import com.lectek.android.lereader.ui.basereader_leyue.widgets.ReaderMediaPlayer.PlayerListener;
import com.lectek.lereader.core.text.style.ClickActionSpan;

public interface IMediaSpan extends ClickActionSpan,PlayerListener,IMedia{
	public boolean isPlay();
	
	public long computePositionByLocal(int x,int y);
}
