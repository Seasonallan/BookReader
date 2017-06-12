package com.lectek.android.lereader.lib.storage.dbase.iterface;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表信息
 * @author laijp
 * @date 2014-6-13
 * @email 451360508@qq.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {
	/** 表名. */
	String name();
	
	/** 搜索结果排序 **/
	boolean isOrderBy() default false;
}
