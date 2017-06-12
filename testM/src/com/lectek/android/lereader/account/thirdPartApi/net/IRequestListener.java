package com.lectek.android.lereader.account.thirdPartApi.net;

import java.io.FileNotFoundException;
import java.io.IOException;

public abstract interface IRequestListener{
	
    public abstract void onComplete(String paramString, Object paramObject);

    public abstract void onIOException(IOException paramIOException, Object paramObject);

    public abstract void onFileNotFoundException(FileNotFoundException paramFileNotFoundException, Object paramObject);
}
