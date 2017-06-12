package com.lectek.android.lereader.net.response;


import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

import java.util.List;

/**
 * 获取分组下书籍信息 response
 */
public class BookGroupMarkResponse extends BaseDao{

    @Json(name = "systemMarkGroup")
    public SystemBookMarkGroupResponseInfo bookMarkGroup;

    @Json(name = "systemMarks",className = SystemBookMarkResponseInfo.class)
    public List<SystemBookMarkResponseInfo> systemMarks;


}
