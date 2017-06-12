package com.lectek.android.lereader.lib.storage.dbase.iterface;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 网络JSON数据类型
 * @author laijp
 * @date 2014-6-13
 * @email 451360508@qq.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Json { 
	/** JsonObject中的键值中的key */
	String name();
    /** 用于复合型实体中包含有JsonArrayList列表数据标识 */
    Class<?> className() default Json.class;
}