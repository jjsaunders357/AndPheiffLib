package com.pheiffware.andpheifflib.log;

import android.util.Log;

public class PLog
{

	public static void error(String message, Exception e)
	{
		Log.e("Error",message,e);
	}

	public static void info(String message)
	{
		Log.i("Info",message);
	}
}
