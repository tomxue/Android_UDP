package com.tomxue.udp;

import android.content.Context;
import android.net.wifi.WifiManager;



public class wifiEnabler {
	
	private WifiManager mWifiManager;
	
	/**
	 * 
	 * <uses-permission
	 * android:name="android.permission.ACCESS_WIFI_STATE"></uses-permission>
	 * <uses-permission
	 * android:name="android.permission.CHANGE_WIFI_STATE"></uses-permission>
	 * 
	 * 
	 * @param isEnable
	 */
	public void enableWifi(Context context, boolean isEnable) {

		//
		if (mWifiManager == null) {
			mWifiManager = (WifiManager) context
					.getSystemService(AndroidUDP.WIFI_SERVICE);
		}

		System.out.println("wifi====" + mWifiManager.isWifiEnabled());
		if (isEnable) {
			// enable wifi
			if (!mWifiManager.isWifiEnabled()) {
				mWifiManager.setWifiEnabled(true);
			}
		} else {
			// disable wifi
			if (mWifiManager.isWifiEnabled()) {
				mWifiManager.setWifiEnabled(false);
			}
		}
	}

}
