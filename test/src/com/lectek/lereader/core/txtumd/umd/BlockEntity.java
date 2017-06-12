package com.lectek.lereader.core.txtumd.umd;

/**
 * UMD块信息
 * @author laijp
 * @date 2014-1-16
 * @email 451360508@qq.com
 */
public class BlockEntity {

	public long index;
	public long length;

	public BlockEntity(long index, long length) {
		this.index = index;
		this.length = length;
	}

}
