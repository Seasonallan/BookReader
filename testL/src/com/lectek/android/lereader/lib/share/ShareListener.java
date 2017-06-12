package com.lectek.android.lereader.lib.share;

import com.lectek.android.lereader.lib.share.entity.ShareResponseErr;

public interface ShareListener {
	public void sucess(Object object);
	public void error(ShareResponseErr error);
	public void dissmissProgress();
	public void start();
}
