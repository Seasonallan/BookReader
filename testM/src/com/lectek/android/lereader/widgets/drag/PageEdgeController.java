package com.lectek.android.lereader.widgets.drag;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

/**
 * 边缘控制，用于拖动的时候是否到边界
 */
public class PageEdgeController {
	private int SLEEP_TIME = 1000;
	private int COUNT_SIZE = 4;

	private boolean isSleeping;
	public int mFullWidth, mEdgeMargin;

	private List<Integer> mCountRecord;

	public PageEdgeController(int fullWidth, int margin) {
		this.mCountRecord = new ArrayList<Integer>();
		this.isSleeping = false;
		this.mFullWidth = fullWidth;
		this.mEdgeMargin = margin;

	}

	Handler delayHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			isSleeping = false;
		}

	};

    /**
     * 添加一个事件
     * @param x
     */
	public void addCount(int x) {
		if (x < mFullWidth - mEdgeMargin || x > mEdgeMargin) {
			wakeUp();
		}
		if (isSleeping) {
			mCountRecord = new ArrayList<Integer>();
			return;
		}
		mCountRecord.add(x);
		if (mCountRecord.size() > COUNT_SIZE) {
			mCountRecord.remove(0);
		}
	}

    /**
     * 是否允许ScrollView往下滑动
     * @return
     */
	@SuppressLint("HandlerLeak")
	public boolean isAllow2Snap2Next() {
		synchronized (mCountRecord) {
			if (mCountRecord.size() < COUNT_SIZE) {
				return false;
			}
			for (int i = 0; i < mCountRecord.size(); i++) {
				if (mCountRecord.get(i) < mFullWidth - mEdgeMargin) {
					return false;
				}
			}
			isSleeping = true;
			mCountRecord.clear();
			delayHandler.sendEmptyMessageDelayed(0, SLEEP_TIME);
			return true;
		}
	}
    /**
     * 是否允许ScrollView往上滑动
     * @return
     */
	public boolean isAllow2Snap2Last() {
		synchronized (mCountRecord) {
			if (mCountRecord.size() < COUNT_SIZE) {
				return false;
			}
			for (int i = 0; i < mCountRecord.size(); i++) {
				if (mCountRecord.get(i) > mEdgeMargin) {
					return false;
				}
			}
			isSleeping = true;
			mCountRecord.clear();
			delayHandler.sendEmptyMessageDelayed(0, SLEEP_TIME);
			return true;
		}
	}

	private void wakeUp() {
		// log("wakeUp");
		// delayHandler.removeMessages(0);
		// delayHandler.sendEmptyMessage(0);
	}

}
