/*
 * Created by Stephen Pheiffer.
 * Do not edit, distribute, modify or use without his permission.
*/
package com.pheiffware.andpheifflib.fatalError;

import java.lang.Thread.UncaughtExceptionHandler;

import android.util.Log;

/**
 * Handles unrecoverable failures in a standard way. Also provides a utility to install and uncaught exception handler in any thread which treats it
 * as an unrecoverable error.
 */
public class FatalErrorHandler implements UncaughtExceptionHandler
{
	public static void installUncaughtExceptionHandler()
	{
		Thread.setDefaultUncaughtExceptionHandler(new FatalErrorHandler());
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread, java.lang.Throwable)
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable t)
	{
		handleFatalError(t);
	}

	public static void handleFatalError(Throwable t)
	{
		handleFatalError("\"" + t.getMessage() + "\"\n" + Log.getStackTraceString(t));
	}

	public static void handleFatalError(String message)
	{
		Log.e("Critical Failure", message);
	}
}
