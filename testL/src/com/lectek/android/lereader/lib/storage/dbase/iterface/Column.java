package com.lectek.android.lereader.lib.storage.dbase.iterface;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据库列信息
 * @author laijp
 * @date 2014-6-13
 * @email 451360508@qq.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    /** 是否是主键[伪主键，数据库主键统一为 '_id'，此处用于标识唯一码]*/
	boolean isPrimaryKey() default false;

	/** 是否是降序**/
	boolean isOrderDesc() default false;

	/** 是否是升序**/
	boolean isOrderAsc() default false;
	
	/** 列类型 */
	String type() default "TEXT";
	
	/** 列名称 */
	String name();}