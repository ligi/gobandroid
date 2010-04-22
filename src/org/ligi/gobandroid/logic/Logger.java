package org.ligi.gobandroid.logic;

import android.util.Log;

public class Logger {
	
	public final static String DEFAULT_TAG="gobandroid";
	
	public static void i(String msg) {
		Log.i(DEFAULT_TAG,msg);
	}
}
