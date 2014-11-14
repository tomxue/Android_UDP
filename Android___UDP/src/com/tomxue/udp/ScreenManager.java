package com.tomxue.udp;

import android.os.PowerManager;
import android.annotation.SuppressLint;
import android.content.Context;

public class ScreenManager {

	private PowerManager.WakeLock wakeLock;
	private Context mContext;
	
	public ScreenManager(Context context) {
		this.mContext = context;
	}

	public void keepScreenOn(boolean enabled) {
		if (wakeLock == null)
		{
			try {
				wakeLock = ((PowerManager) mContext
					.getSystemService(Context.POWER_SERVICE)).newWakeLock(
					PowerManager.SCREEN_BRIGHT_WAKE_LOCK
							| PowerManager.ON_AFTER_RELEASE, "MyActivity");
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}

		if (enabled)
			try {
				wakeLock.acquire();
			} catch (Exception e) {
				e.printStackTrace();
			}
		else
		{
			try {
				wakeLock.release();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
