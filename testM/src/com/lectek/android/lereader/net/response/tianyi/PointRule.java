package com.lectek.android.lereader.net.response.tianyi;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

/** 积分兑换阅点规则
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2013-9-9
 */
public class PointRule extends BaseDao{
	
	@Json(name ="readpoint")
	public String readPoint;
	@Json(name ="rule_id")
	public String ruleId;
	@Json(name ="rule_name")
	public String ruleName;
	@Json(name ="convert_point")
	public String converPoint;
	@Json(name ="convert_state")
	public String convertState;
	/**
	 * @return the readPoint
	 */
	public String getReadPoint() {
		return readPoint;
	}
	/**
	 * @param readPoint the readPoint to set
	 */
	public void setReadPoint(String readPoint) {
		this.readPoint = readPoint;
	}
	/**
	 * @return the ruleId
	 */
	public String getRuleId() {
		return ruleId;
	}
	/**
	 * @param ruleId the ruleId to set
	 */
	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}
	/**
	 * @return the ruleName
	 */
	public String getRuleName() {
		return ruleName;
	}
	/**
	 * @param ruleName the ruleName to set
	 */
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	/**
	 * @return the converPoint
	 */
	public String getConverPoint() {
		return converPoint;
	}
	/**
	 * @param converPoint the converPoint to set
	 */
	public void setConverPoint(String converPoint) {
		this.converPoint = converPoint;
	}
	/**
	 * @return the convertState
	 */
	public String getConvertState() {
		return convertState;
	}
	/**
	 * @param convertState the convertState to set
	 */
	public void setConvertState(String convertState) {
		this.convertState = convertState;
	}

}
