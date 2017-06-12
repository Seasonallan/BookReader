/*
 * ========================================================
 * ClassName:ShakeListenerUtils.java* 
 * Description:
 * Copyright (C) 
 * ========================================================
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *===================================================================*
 * Revision History
 *
 * Modification                    Tracking
 * Date         Author          Number       Description of changes
 *____________________________________________________________________
 * 
 * 2014-3-12     chendt          #00000       create
 */
package com.lectek.android.lereader.ui.shake;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Vibrator;

public class ShakeListenerUtils implements SensorEventListener {
	private Activity context;
	/** 临界值 */
	public static final int CRITICAL_VALUES = 15;// 小于13基本上拿着手机就会执行了
	private ShakeOverListener shakeOverListener;
	
	public ShakeListenerUtils(Activity context,ShakeOverListener shakeOverListener) {
		super();
		this.context = context;
		this.shakeOverListener = shakeOverListener;
	}
	
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		int sensorType = event.sensor.getType();
		// values[0]:X轴，values[1]：Y轴，values[2]：Z轴
		float[] values = event.values;

		if (sensorType == Sensor.TYPE_ACCELEROMETER) {

			/*
			 * 正常情况下，任意轴数值最大就在9.8~10之间，只有在突然摇动手机 的时候，瞬时加速度才会突然增大或减少。
			 * 监听任一轴的加速度大于指定临界值即可
			 */
			if ((Math.abs(values[0]) > CRITICAL_VALUES
					|| Math.abs(values[1]) > CRITICAL_VALUES || Math
					.abs(values[2]) > CRITICAL_VALUES)) {
				shakeOverListener.shakeOver();
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// 当传感器精度改变时回调该方法，Do nothing.
	}

	public static interface ShakeOverListener{
		/**摇一摇结束回调*/
		public void shakeOver();
	} 
}