package com.lectek.android.lereader.widgets;

import android.os.CountDownTimer;

public class CustomCount extends CountDownTimer {
	private ICountDownRunnable mCountDownRunnable;

	public CustomCount(long millisInFuture, long countDownInterval,
			ICountDownRunnable countDownRunnable) {
		super(millisInFuture, countDownInterval);
		this.mCountDownRunnable = countDownRunnable;
	}

	public static int timeMinute(int second) {
		return second / 60;
	}

	public static int timeSecond(int second) {
		return second % 60;
	}

	@Override
	public void onFinish() {
		if (mCountDownRunnable != null) {
			mCountDownRunnable.timeFinish();
		}
	}

	@Override
	public void onTick(long millisUntilFinished) {
		int savingTime = (int) millisUntilFinished / 1000;
		int timeMinutes = timeMinute(savingTime);
		int timeSeconds = timeSecond(savingTime);
		if (timeMinutes == 0) {
			if (mCountDownRunnable != null) {
				mCountDownRunnable.timeSecondRunning(timeSeconds);
			}
		} else {
			if (mCountDownRunnable != null) {
				mCountDownRunnable.timeRunning(timeMinutes, timeSeconds);
			}
		}
		if (mCountDownRunnable != null) {
			mCountDownRunnable.timeSave(savingTime);
		}
	}

	public interface ICountDownRunnable {
		/** 计时(分、秒) */
		public void timeRunning(int minutes, int seconds);

		/** 计时(秒) */
		public void timeSecondRunning(int minutes);

		/** 实时保存时间 */
		public void timeSave(int savingTime);

		/** 处理计时结束 */
		public void timeFinish();
	}
}
