package com.lectek.android.lereader.net.response;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;
import com.lectek.android.lereader.lib.utils.StringUtil;

/**
 * 返回{"result":"true"}格式转换
 */
public class CommonResultInfo extends BaseDao{
	
	@Json(name = "result")
	public String result;

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

    /**
     * 是否成功
     * @return
     */
    public boolean isSuccess(){
        if (!StringUtil.isEmpty(result)){
            return result.equalsIgnoreCase("true");
        }
        return false;
    }

	@Override
	public String toString() {
		return "CommonResultInfo [result=" + result + "]";
	}

}
